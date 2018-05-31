package com.soli.newframeapp.net

import android.support.v7.widget.LinearLayoutManager
import com.soli.lib_common.base.BaseActivity
import com.soli.lib_common.net.ApiHelper
import com.soli.lib_common.net.DataType
import com.soli.newframeapp.R
import com.soli.newframeapp.model.StoryList
import com.soli.pullupdownrefresh.PullRefreshLayout
import com.soli.pullupdownrefresh.more.LoadMoreRecyclerAdapter
import kotlinx.android.synthetic.main.activity_net_work_test.*
import java.text.SimpleDateFormat
import java.util.*

class NetWorkTestActivity : BaseActivity() {

    private val adapter: NewsAdapter by lazy { NewsAdapter(ctx) }
    private val mAdapter: LoadMoreRecyclerAdapter by lazy { LoadMoreRecyclerAdapter(adapter) }

    override fun getContentView() = R.layout.activity_net_work_test

    override fun initView() {
        title = "网络测试"

        refreshLayout.setPageSize(20)

        itemList.apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = mAdapter
        }

    }

    override fun initListener() {

        refreshLayout.setRefreshListener(object : PullRefreshLayout.onRefrshListener {
            override fun onPullupRefresh(actionFromClick: Boolean) {
                getNewsDate(false)
            }

            override fun onPullDownRefresh() {
                adapter.removeAll()
                getNewsDate(false)
            }

        })
    }

    override fun initData() {

        getNewsDate(true)
    }

    /**
     *
     */
    private fun getNewsDate(show: Boolean = false) {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        showProgress(show)

        ApiHelper.Builder()
                .baseUrl("http://news.at.zhihu.com/api/4/news/before/")
                .bodyType(DataType.JSON_OBJECT, StoryList::class.java)
                .url(simpleDateFormat.format(calendar.time))
                .build()
                .get { result ->
                    dismissProgress()
                    refreshLayout.onRefreshComplete()
                    if (result.isSuccess) {
                        if (result.result is StoryList) {
                            adapter.addAll((result.result as StoryList).stories)
                            mAdapter.notifyDataSetChangedHF()
                        }
                    } else {
                        errorHappen { getNewsDate(show) }
                    }
                }
    }

}
