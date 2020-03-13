package com.soli.newframeapp;

import android.os.Handler;
import android.util.Log;
import com.soli.libcommon.base.BaseActivity;
import com.soli.libcommon.net.ApiCallBack;
import com.soli.libcommon.net.ApiHelper;
import com.soli.libcommon.net.DataType;
import com.soli.libcommon.util.NetworkUtil;
import com.soli.libcommon.util.TabFragmentManager;
import com.soli.libcommon.view.root.LoadingType;
import com.soli.newframeapp.model.StoryList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Soli
 * @Time 18-5-15 下午5:04
 */
public class SecondAcitivity extends BaseActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_second;
    }

    @Override
    protected void initView() {
        setTitle("Java Activity");
    }

    @Override
    protected void initListener() {

        findViewById(R.id.button).setOnClickListener(v -> hasNoResult(R.layout.has_no_content_layout));
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
        TabFragmentManager manager = new TabFragmentManager(this, R.id.root_content);
        manager.addTab(1, TestFragment.Companion.getInstance("dksld").getClass(), null);
        manager.addTab(2, TestFragment.Companion.getInstance("dksl2332d").getClass(), null);
        manager.setCurrentTab(1);
    }

    private void loadingErrorTest() {
        showProgress(true,true,LoadingType.TypeDialog);
        new Handler().postDelayed(() -> {
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

        ApiHelper api = new ApiHelper.Builder()
                .baseUrl("http://news.at.zhihu.com/api/4/news/before/")
                .bodyType(DataType.JSON_OBJECT, StoryList.class)
                .url(simpleDateFormat.format(calendar.getTime()))
                .build();

        api.get((ApiCallBack<StoryList>) result -> {
//            dismissProgress();
            if (result.isSuccess()) {
                Log.e("result", result.getFullData());
            } else {
            }
        });
    }
}
