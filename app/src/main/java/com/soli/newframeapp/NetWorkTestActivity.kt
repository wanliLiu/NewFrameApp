package com.soli.newframeapp

import com.soli.lib_common.base.BaseActivity
import com.soli.lib_common.net.ApiHelper
import kotlinx.android.synthetic.main.activity_net_work_test.*
import java.text.SimpleDateFormat
import java.util.*

class NetWorkTestActivity : BaseActivity() {


    override fun getContentView() = R.layout.activity_net_work_test

    override fun initView() {

    }

    override fun initListener() {
    }

    override fun initData() {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        getNewsDate(simpleDateFormat.format(calendar.time))
    }


    private fun getNewsDate(date: String) {
        showProgress()

        ApiHelper.Builder()
                .baseUrl("http://news.at.zhihu.com/api/4/news/before/")
                .url(date)
                .build()
                .get { result ->
                    dismissProgress()
                    if (result!!.isSuccess) {
                        jsonContent.text = result.result as CharSequence
                    } else {
                        errorHappen { getNewsDate(date) }
                    }
                }
    }

}
