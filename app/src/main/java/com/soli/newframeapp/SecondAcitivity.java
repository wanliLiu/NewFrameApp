package com.soli.newframeapp;

import android.os.Handler;

import com.soli.lib_common.base.BaseActivity;
import com.soli.lib_common.net.ApiCallBack;
import com.soli.lib_common.net.ApiHelper;
import com.soli.lib_common.net.ApiResult;
import com.soli.lib_common.util.NetworkUtil;
import com.soli.lib_common.util.TabFragmentManager;
import com.soli.lib_common.view.root.LoadingType;

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

        ApiHelper api = new ApiHelper.Builder()
                .baseUrl("http://news.at.zhihu.com/api/4/news/before/")
                .url("20180510")
                .build();
        api.get(new ApiCallBack() {
            @Override
            public void receive(ApiResult result) {
                dismissProgress();
                if (result.isSuccess()) {

                } else {
                }
            }
        });
    }

    private void loadingErrorTest() {
        showProgress(LoadingType.TypeDialog);
        new Handler().postDelayed(() -> {
            dismissProgress();
            addFragment();
//            errorHappen(() -> {
//                loadingErrorTest();
//                return null;
//            });
        }, 2000);
    }
}
