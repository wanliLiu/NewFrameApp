package com.soli.newframeapp

import android.os.Handler
import android.view.View
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.TabFragmentManager
import com.soli.newframeapp.databinding.ActivityFragmentTestBinding

class FragmentTestActivity : BaseActivity<ActivityFragmentTestBinding>(), View.OnClickListener {

    private val inputStr = "我是输入的文字，到最后，还是\n这样看到了是打开塑料袋上看到了上来的就是多了"

    private val pageManager by lazy {
        TabFragmentManager(ctx, R.id.container)
    }

    override fun initView() {
        title = "Framgent Test"
        rootView.justyProgressAreaContentTo(binding.container)
    }

    override fun initListener() {
        binding.activityTest.setOnClickListener(this)
        binding.fragementTest1.setOnClickListener(this)
        binding.fragementTest2.setOnClickListener(this)
    }

    override fun initData() {
        val tab1 = TestFragment.getInstance("$inputStr \n-----tab1")
        pageManager.addTab(R.id.fragementTest1, tab1::class.java, tab1.arguments)
        val tab2 = TestFragment.getInstance("$inputStr \n-----tab2")
        pageManager.addTab(R.id.fragementTest2, tab2::class.java, tab2.arguments)
    }

    private fun activityTest() {
        showProgress()
        Handler().postDelayed({
            dismissProgress()
        }, 1000)
    }

    private fun FragemntTest() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TestFragment.getInstance(inputStr)).commit()
        //just make difference
        Handler().postDelayed({ rootView.justyProgressAreaContentTo(binding.container) }, 500)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activityTest -> activityTest()
            R.id.fragementTest1, R.id.fragementTest2 -> pageManager.setCurrentTab(v.id)
        }
    }
}
