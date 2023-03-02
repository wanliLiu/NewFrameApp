package com.soli.newframeapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.soli.libcommon.base.BaseActivity;
import com.soli.libcommon.net.ApiHelper;
import com.soli.libcommon.net.ApiResult;
import com.soli.libcommon.net.DataType;
import com.soli.libcommon.util.NetworkUtil;
import com.soli.libcommon.view.loading.LoadingType;
import com.soli.newframeapp.databinding.ActivitySecondBinding;
import com.soli.newframeapp.model.StoryList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author Soli
 * @Time 18-5-15 下午5:04
 */
public class SecondAcitivity extends BaseActivity<ActivitySecondBinding> {

    @Override
    protected void initView() {
        setTitle("Java Activity");
    }

    @Override
    protected void initListener() {

//        findViewById(R.id.button).setOnClickListener(v -> hasNoResult(R.layout.has_no_content_layout));
    }

    @Override
    protected void initData() {

        loadingErrorTest();
        NetworkUtil.INSTANCE.isAvailableByPing();
    }

    /**
     *
     */
    private void addFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.topView, TestFragment.Companion.getInstance("我事来自java过来的哦")).commit();
    }

    /**
     *
     */
    private void pageFragmentManager() {
//        TabFragmentManager manager = new TabFragmentManager(this, R.id.root_content);
//        manager.addTab(1, TestFragment.Companion.getInstance("dksld").getClass(), null);
//        manager.addTab(2, TestFragment.Companion.getInstance("dksl2332d").getClass(), null);
//        manager.setCurrentTab(1);
    }

    private void loadingErrorTest() {
        showProgress(true, true, LoadingType.TypeDialog);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            dismissProgress();
            addFragment();

            javaHttpLoadingTest();

//            errorHappen(() -> {
//                loadingErrorTest();
//                return null;
//            });
        }, 2000);
    }

    /**
     *
     */
    private void javaHttpLoadingTest() {

//        showProgress();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());


        ApiHelper.Builder api = new ApiHelper.Builder();
        api.setBaseUrl("http://news.at.zhihu.com/api/4/news/before/");
        api.setBodyType(DataType.JSON_OBJECT);
        api.setClazz(StoryList.class);
        api.setUrl(simpleDateFormat.format(calendar.getTime()));

        api.build().get((Function1<ApiResult<StoryList>, Unit>) result -> {
            if (result.isSuccess()) {
                Log.e("result", result.getFullData());
            }
            return null;
        });
    }
}
