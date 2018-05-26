package com.soli.newframeapp

import com.google.gson.Gson
import com.soli.lib_common.base.BaseActivity
import com.soli.lib_common.net.ApiHelper
import com.soli.lib_common.net.DataType
import com.soli.newframeapp.model.StoryList
import kotlinx.android.synthetic.main.activity_net_work_test.*
import java.text.SimpleDateFormat
import java.util.*

class NetWorkTestActivity : BaseActivity() {


    override fun getContentView() = R.layout.activity_net_work_test

    override fun initView() {
        title = "网络测试"

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
                .bodyType(DataType.JSON_OBJECT,StoryList::class.java)
                .url(date)
                .build()
                .get { result ->
                    dismissProgress()
                    if (result!!.isSuccess) {
                        if (result.result is StoryList){
                            println(result.result)
                        }
                        jsonContent.text = result.fullData
                        val storylist : StoryList = Gson().fromJson(result.fullData as String,StoryList::class.java)
                        println(storylist)

                    } else {
                        errorHappen { getNewsDate(date) }
                    }
                }
    }

}
