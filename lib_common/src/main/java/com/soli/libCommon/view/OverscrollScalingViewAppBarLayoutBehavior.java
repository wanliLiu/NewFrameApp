package com.soli.libCommon.view;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.soli.libCommon.R;

/**
 * @author Soli
 * @Time 2019/1/16 18:17
 */
public class OverscrollScalingViewAppBarLayoutBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final String TAG = "overScrollScale";
    private View mTargetScalingView;
    private int mPivotX;
    private int mPivotY;


    private Scaler mScaleImpl;

    public OverscrollScalingViewAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleImpl = mScaleImpl == null ? new ViewScaler() : mScaleImpl;
    }

    public OverscrollScalingViewAppBarLayoutBehavior() {
        super();
        mScaleImpl = mScaleImpl == null ? new ViewScaler() : mScaleImpl;
    }

    private int mTotalDyUnconsumed = 0;
    private int mTotalTargetDyUnconsumed;

    public void setScaler(Scaler scaler) {
        this.mScaleImpl = scaler;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View abl, int layoutDirection) {
        boolean superLayout = super.onLayoutChild(parent, abl, layoutDirection);
        if (mTargetScalingView == null) {
            mTargetScalingView = parent.findViewById(R.id.zoom_image);
            if (mTargetScalingView != null) {
                mScaleImpl.obtainInitialValues();
            }
        }
        return superLayout;
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if (mTargetScalingView == null || dyConsumed != 0) {
            mScaleImpl.cancelAnimations();
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            return;
        }

        if (dyUnconsumed < 0 && getTopAndBottomOffset() >= mScaleImpl.getInitialParentBottom()) {
            int absDyUnconsumed = Math.abs(dyUnconsumed);
            mTotalDyUnconsumed += absDyUnconsumed;
            mTotalDyUnconsumed = Math.min(mTotalDyUnconsumed, mTotalTargetDyUnconsumed);
            mScaleImpl.updateViewScale();
        } else {
            mTotalDyUnconsumed = 0;
            mScaleImpl.setShouldRestore(false);
            if (dyConsumed != 0) {
                mScaleImpl.cancelAnimations();
            }
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == View.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        mScaleImpl.retractScale();
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }


    private class ViewScaler extends ParentScaler {
        private boolean mRetracting = false;

        private ViewPropertyAnimatorListener mShouldRestoreListener = new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                mShouldRestore = false;
            }
        };
        private ViewPropertyAnimatorCompat mScaleAnimator;

        @Override
        public void updateViewScale() {
            float scale = getScale();
            setScale(scale);
            mCurrentScale = scale;
            mShouldRestore = true;
        }

        @Override
        public void retractScale() {
            super.retractScale();
            if (mShouldRestore) {
                mRetracting = true;
                mScaleAnimator = ViewCompat.animate(mTargetScalingView).setListener(mShouldRestoreListener).scaleY(1f).scaleX(1f);
                mScaleAnimator.start();
                mTotalDyUnconsumed = 0;
            }
        }

        @Override
        public void setScale(float scale) {
            super.setScale(scale);
            ViewCompat.setScaleX(mTargetScalingView, scale);
            ViewCompat.setScaleY(mTargetScalingView, scale);
        }

        @Override
        public void obtainInitialValues() {
            super.obtainInitialValues();
            mInitialScale = Math.max(ViewCompat.getScaleY(mTargetScalingView), ViewCompat.getScaleX(mTargetScalingView));
        }

        @Override
        public boolean isRetracting() {
            return mRetracting;
        }

        @Override
        public void cancelAnimations() {
            super.cancelAnimations();
            mShouldRestore = false;
            ViewCompat.animate(mTargetScalingView).cancel();
            ViewCompat.setScaleY(mTargetScalingView, 1f);
            ViewCompat.setScaleX(mTargetScalingView, 1f);
        }
    }

    private class ParentScaler implements Scaler {
        float mCurrentScale;
        float mInitialScale;
        boolean mShouldRestore = false;
        private int mInitialParentBottom;
        private ViewGroup mParent;
        IntEvaluator mIntEvaluator = new IntEvaluator();
        private int mTargetParentBottom;
        private ValueAnimator mBottomAnimator;

        @Override
        public void setShouldRestore(boolean restore) {
            mShouldRestore = restore;
        }


        public float getCurrentScale() {
            return mCurrentScale;
        }

        @Override
        public void cancelAnimations() {
            mShouldRestore = false;
            if (mBottomAnimator != null && mBottomAnimator.isRunning())
                mBottomAnimator.cancel();
        }

        @Override
        public int getInitialParentBottom() {
            return mInitialParentBottom;
        }

        @Override
        public boolean isShouldRestore() {
            return mShouldRestore;
        }

        @Override
        public boolean isRetracting() {
            return false;
        }


        public float getScale() {
            float ratio = (float) mTotalDyUnconsumed / mTotalTargetDyUnconsumed;
            float scale = 1f + ratio;
            return scale;
        }

        @Override
        public void updateViewScale() {

        }

        @Override
        public void retractScale() {
            final View parent = mParent;
            if (parent.getBottom() > mInitialParentBottom) {
                mBottomAnimator = ValueAnimator.ofInt(parent.getBottom(), mInitialParentBottom);
                mBottomAnimator.setEvaluator(mIntEvaluator);
                mBottomAnimator.addUpdateListener(animation -> {
                    int bottom = (int) animation.getAnimatedValue();
                    parent.setBottom(bottom);
                });
                mBottomAnimator.start();
            }
        }

        @Override
        public void setScale(float scale) {
            final View parent = mParent;
            Integer evaluate = mIntEvaluator.evaluate(scale, mInitialParentBottom, mTargetParentBottom);
            parent.setBottom(evaluate);
            parent.postInvalidate();

        }

        @Override
        public void obtainInitialValues() {
            mParent = (ViewGroup) mTargetScalingView.getParent();
            mInitialParentBottom = mParent.getHeight();
            mTargetParentBottom = (int) (mInitialParentBottom * 1.1);
            mPivotX = mTargetScalingView.getWidth() / 2;
            mPivotY = mTargetScalingView.getHeight() / 2;
            ViewCompat.setPivotX(mTargetScalingView, mPivotX);
            ViewCompat.setPivotY(mTargetScalingView, mPivotY);
            mTotalTargetDyUnconsumed = 50;
        }
    }

   /* private class MatrixScaler extends DefaultScaler {
        private int mIntialViewBottom;
        private double mTargetParentBottom;
        @Override
        public boolean isRetracting() {
            return mRetracting;
        }
        @Override
        public float getCurrentScale() {
            return mCurrentScale;
        }
        private boolean mRetracting = false;
        private AnimatorListenerAdapter mShouldRestoreListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animation a) {
                mShouldRestore = false;
            }
        };
        private ValueAnimator.AnimatorUpdateListener mRestoreScaleListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                setScale(animatedValue);
            }
        };
        @Override
        public void updateViewScale() {
            float scale = getScale();
            setScale(scale);
            mCurrentScale = scale;
            mShouldRestore = true;
        }
        @Override
        public void retractScale() {
            super.retractScale();
            if (mShouldRestore) {
                mRetracting = true;
                mShouldRestore = false;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentScale, mInitialScale);
                valueAnimator.addListener(mShouldRestoreListener);
                valueAnimator.addUpdateListener(mRestoreScaleListener);
                valueAnimator.start();
                mTotalDyUnconsumed = 0;
            }
        }
        @Override
        public void setScale(float scale) {
            super.setScale(scale);
            Integer evaluate = mIntEvaluator.evaluate(scale, mIntialViewBottom, (int) mTargetParentBottom);
            mTargetScalingView.setBottom(evaluate);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale, mPivotX, mPivotY);
            mTargetScalingView.setImageMatrix(matrix);
        }
        @Override
        public void obtainInitialValues() {
            super.obtainInitialValues();
            mIntialViewBottom = mTargetScalingView.getBottom();
            mTargetParentBottom = mIntialViewBottom * 1.1;
            float[] values = new float[9];
            mTargetScalingView.getImageMatrix().getValues(values);
            mInitialScale = Math.min(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        }
    }
*/

    public interface Scaler {
        void setShouldRestore(boolean restore);

        float getCurrentScale();

        void cancelAnimations();

        int getInitialParentBottom();

        boolean isShouldRestore();

        boolean isRetracting();

        float getScale();

        void updateViewScale();

        void retractScale();

        void setScale(float scale);

        void obtainInitialValues();
    }

}
