package com.soli.libcommon.view.loading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.soli.libcommon.R;
import com.soli.pullupdownrefresh.ptr.PtrFrameLayout;
import com.soli.pullupdownrefresh.ptr.PtrUIHandler;
import com.soli.pullupdownrefresh.ptr.indicator.PtrIndicator;


/**
 * @author Soli
 * @Time 2017/10/18
 */
public class PullRefreshHeader extends FrameLayout implements PtrUIHandler {

    private PullLoadingImageView imageView;

    public PullRefreshHeader(Context context) {
        super(context);
        init(context);
    }

    public PullRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * @param dipValue
     * @param ctx
     * @return
     */
    private int dip2px(float dipValue, Context ctx) {
        return (int) (dipValue * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }


    /**
     * @param ctx
     */
    private void init(Context ctx) {
        imageView = new PullLoadingImageView(ctx);
        imageView.setAutoAnimation(false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.height = ctx.getResources().getDimensionPixelOffset(R.dimen.new_pull_loading_height);
        params.width = ctx.getResources().getDimensionPixelOffset(R.dimen.new_pull_loading_width);
        params.gravity = Gravity.CENTER;
        params.topMargin = params.bottomMargin = dip2px(10, ctx);
        addView(imageView, params);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        imageView.setVisibility(View.VISIBLE);
        imageView.resetDefault(true);
        imageView.stopAnim();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
//        imageView.startAnim();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        imageView.startAnim();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
    }

    /**
     * 动画完成 才进行刷新完成
     */
    public void onRefreshComplete(final PtrFrameLayout ptrLayout) {
        if (ptrLayout.isRefreshing()) {
            imageView.animate().alpha(0.0f).setDuration(400).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imageView.stopAnim();
                    imageView.setVisibility(INVISIBLE);
                    imageView.setAlpha(1.0f);
                    ptrLayout.performRefreshComplete_eyeblink();
                }
            }).start();
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }

    /**
     * 显示加载进度条
     */
    public void showProgress() {
        imageView.setVisibility(View.VISIBLE);
        imageView.startAnim();
    }
}
