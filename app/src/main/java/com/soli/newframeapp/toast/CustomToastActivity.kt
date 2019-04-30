package com.soli.newframeapp.toast

import android.view.LayoutInflater
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.util.ToastUtils
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_toast.*

/**
 *
 * @author Soli
 * @Time 2019/1/9 15:31
 */
class CustomToastActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_toast

    override fun initView() {
        title = "自定义toast"
    }

    override fun initListener() {

        toast.setOnClickListener {
            val toast = LayoutInflater.from(ctx).inflate(R.layout.toastlayout, null)
            ToastManager.getInstance(ctx).makeToastSelfViewAnim(toast!!, R.style.MyToast)

            ToastUtils.showShortToast("我长度但是看到了斯柯达了SDK类似都开始来得快熟练度说的了")
        }
    }

    override fun initData() {
    }
}