package com.soli.newframeapp

import android.os.Handler
import android.view.View
import com.soli.lib_common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_fragment_test.*

class FragmentTestActivity : BaseActivity(), View.OnClickListener {

    private val inputStr = "我是输入的文字，到最后，还是\n这样看到了是打开塑料袋上看到了上来的就是多了"

    override fun getContentView() = R.layout.activity_fragment_test

    override fun initView() {
        title = "Framgent Test"
        rootView.justyProgressAreaContentTo(container)
    }

    override fun initListener() {
        activityTest.setOnClickListener(this)
        fragementTest.setOnClickListener(this)
    }

    override fun initData() {
    }

    private fun activityTest() {
        showProgress()
        Handler().postDelayed({
            dismissProgress()
        }, 1000)
    }

    private fun FragemntTest() {
        supportFragmentManager.beginTransaction()?.replace(R.id.container, TestFragment.getInstance(inputStr))?.commit()
        //just make difference
        Handler().postDelayed({ rootView.justyProgressAreaContentTo(container) }, 500)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activityTest -> activityTest()
            R.id.fragementTest -> FragemntTest()
        }
    }
}
