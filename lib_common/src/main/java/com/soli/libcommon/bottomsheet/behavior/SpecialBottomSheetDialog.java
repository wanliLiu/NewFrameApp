package com.soli.libcommon.bottomsheet.behavior;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.*;
import android.widget.FrameLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.soli.libcommon.R;

/** Base class for {@link android.app.Dialog}s styled as a bottom sheet. */
public class SpecialBottomSheetDialog extends AppCompatDialog {

    private SpecialBottomSheetBehavior<FrameLayout> behavior;

    boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;
    private boolean canceledOnTouchOutsideSet;

    public SpecialBottomSheetDialog(@NonNull Context context) {
        this(context, 0);
    }

    public SpecialBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, getThemeResId(context, theme));
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected SpecialBottomSheetDialog(
            @NonNull Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.cancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapInBottomSheet(layoutResId, null, null));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params));
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        if (this.cancelable != cancelable) {
            this.cancelable = cancelable;
            if (behavior != null) {
                behavior.setHideable(cancelable);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (behavior != null && behavior.getState() == SpecialBottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(SpecialBottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !cancelable) {
            cancelable = true;
        }
        canceledOnTouchOutside = cancel;
        canceledOnTouchOutsideSet = true;
    }

    @NonNull
    public SpecialBottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }

    /**
     *
     * @param layoutResId
     * @param view
     * @param params
     * @return
     */
    protected View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        FrameLayout container = (FrameLayout) View.inflate(getContext(), R.layout.special_design_bottom_sheet_dialog, null);
        CoordinatorLayout coordinator =  container.findViewById(R.id.coordinator);
        if (layoutResId != 0 && view == null) {
            view = getLayoutInflater().inflate(layoutResId, coordinator, false);
        }
        FrameLayout bottomSheet =  coordinator.findViewById(R.id.design_bottom_sheet);
        behavior = SpecialBottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(bottomSheetCallback);
        behavior.setHideable(cancelable);
        if (params == null) {
            bottomSheet.addView(view);
        } else {
            bottomSheet.addView(view, params);
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator
                .findViewById(R.id.touch_outside)
                .setOnClickListener(
                        view12 -> {
                            if (cancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
                                cancel();
                            }
                        });
        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(
                bottomSheet,
                new AccessibilityDelegateCompat() {
                    @Override
                    public void onInitializeAccessibilityNodeInfo(
                            View host, AccessibilityNodeInfoCompat info) {
                        super.onInitializeAccessibilityNodeInfo(host, info);
                        if (cancelable) {
                            info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS);
                            info.setDismissable(true);
                        } else {
                            info.setDismissable(false);
                        }
                    }

                    @Override
                    public boolean performAccessibilityAction(View host, int action, Bundle args) {
                        if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && cancelable) {
                            cancel();
                            return true;
                        }
                        return super.performAccessibilityAction(host, action, args);
                    }
                });
        bottomSheet.setOnTouchListener((View view1, MotionEvent event) -> {
                    // Consume the event and prevent it from falling through
                    return true;
                });

        return container;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!canceledOnTouchOutsideSet) {
            TypedArray a =
                    getContext()
                            .obtainStyledAttributes(new int[] {android.R.attr.windowCloseOnTouchOutside});
            canceledOnTouchOutside = a.getBoolean(0, true);
            a.recycle();
            canceledOnTouchOutsideSet = true;
        }
        return canceledOnTouchOutside;
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
//            TypedValue outValue = new TypedValue();
//            if (context.getTheme().resolveAttribute(R.attr.bottomSheetDialogTheme, outValue, true)) {
//                themeId = outValue.resourceId;
//            } else {
//                // bottomSheetDialogTheme is not provided; we default to our light theme
//                themeId = R.style.Theme_Design_Light_BottomSheetDialog;
//            }
        }
        return themeId;
    }

    private SpecialBottomSheetBehavior.BottomSheetCallback bottomSheetCallback =
            new SpecialBottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(
                        @NonNull View bottomSheet, @SpecialBottomSheetBehavior.State int newState) {
                    if (newState == SpecialBottomSheetBehavior.STATE_HIDDEN) {
                        cancel();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
            };
}
