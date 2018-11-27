package com.soli.newframeapp.net

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.DataType
import com.soli.libCommon.util.ViewUtil
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
    private var index = 0
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
                index++
                getNewsDate()
            }

            override fun onPullDownRefresh() {
                adapter.clear()
                index = 0
                getNewsDate()
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
        calendar.add(Calendar.DAY_OF_MONTH, -index)

        showProgress(show)

        val apiHelper = ApiHelper.Builder()
            .baseUrl("http://news.at.zhihu.com/api/4/news/before/")
            .bodyType(DataType.JSON_OBJECT, StoryList::class.java)
            .url(simpleDateFormat.format(calendar.time))
            .build()


        apiHelper.get { result ->

            ViewUtil.setNoDataEmptyView(context = ctx,
                listview = itemList,
                message = "没有数据哦,测试RecyclerView加载没有数据的空视图显示!",
                listener = View.OnClickListener {
                    index = 0
                    getNewsDate(true)
                })

            dismissProgress()
            refreshLayout.onRefreshComplete()
            if (result.isSuccess) {
                if (result.result is StoryList) {
                    adapter.addAll((result.result as StoryList).stories)
                    if (index == 5) {
                        adapter.clear()
                    }
                    mAdapter.notifyDataSetChangedHF()
                }
            } else {
                errorHappen(index, result) {
                    getNewsDate(show)
                }
            }
        }
    }

}
