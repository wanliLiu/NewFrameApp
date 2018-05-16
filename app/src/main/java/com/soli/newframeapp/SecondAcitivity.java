package com.soli.newframeapp;

import android.os.Handler;

import com.soli.lib_common.base.BaseActivity;
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
    }

    /**
     *
     */
    private void addFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.topView, TestFragment.Companion.getInstance("我事来自java过来的哦")).commit();
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
