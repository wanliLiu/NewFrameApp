package com.soli.libcommon.util;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.view.View;
import android.view.ViewConfiguration;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Soli on 2016/10/24.
 */

public class RxJavaUtil {

    /**
     * @param delayTime 单位毫秒
     * @param consumer
     */
    @SuppressLint("CheckResult")
    public static void delayAction(int delayTime, final Consumer consumer) {
        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (consumer != null) {
                        consumer.accept(aLong);
                    }
                });
    }

    /**
     * 可以传递数据
     *
     * @param delayTime
     * @param object
     * @param consumer
     */
    @SuppressLint("CheckResult")
    public static void delayAction(int delayTime, final Object object, final Consumer consumer) {
        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .map(aLong -> object)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (consumer != null) {
                        consumer.accept(o);
                    }
                });
    }

    /**
     * @param view
     * @param listener
     */
    @SuppressLint("CheckResult")
    public static void click(final View view, final View.OnClickListener listener) {
        RxView.clicks(view)
                .throttleFirst(ViewConfiguration.getDoubleTapTimeout(), TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if (listener != null)
                        listener.onClick(view);
                });
    }

    /**
     * 运行于UI线程
     *
     * @param action
     */
    public static void runOnUiThread(Action action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                if (action != null)
                    action.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            runThread(AndroidSchedulers.mainThread(), action);
        }
    }

    /**
     * 运行于那个线程
     *
     * @param scheduler
     * @param action
     */
    public static void runThread(Scheduler scheduler, Action action) {
        Observable.empty()
                .observeOn(scheduler)
                .doOnComplete(action)
                .subscribe();
    }

    /**
     * 运行于非ui线程中
     *
     * @param action
     */
    public static void runOnThread(Action action) {
        runThread(Schedulers.io(), action);
    }
}
