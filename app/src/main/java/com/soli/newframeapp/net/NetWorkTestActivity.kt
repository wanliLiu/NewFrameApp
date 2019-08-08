package com.soli.newframeapp.net

import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.DataType
import com.soli.libCommon.util.ViewUtil
import com.soli.newframeapp.R
import com.soli.newframeapp.model.StoryList
import com.soli.pullupdownrefresh.PullRefreshLayout
import kotlinx.android.synthetic.main.pull_refresh_layout.*
import java.text.SimpleDateFormat
import java.util.*

class NetWorkTestActivity : BaseActivity() {

    private val mAadapter: NewsAdapter by lazy { NewsAdapter(ctx) }
    private var index = 0
    override fun getContentView() = R.layout.pull_refresh_layout

    override fun initView() {
        title = "网络测试"

        refreshLayout.setPageSize(20)

        itemList.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            adapter = mAadapter
        }

    }

    override fun initListener() {

        refreshLayout.setRefreshListener(object : PullRefreshLayout.onRefrshListener {
            override fun onPullupRefresh(actionFromClick: Boolean) {
                index++
                getNewsDate()
            }

            override fun onPullDownRefresh() {
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

                if (index == 0)
                    mAadapter.removeAll()

                if (result.result is StoryList) {
                    mAadapter.addAll((result.result as StoryList).stories)
                    if (index == 5) {
                        mAadapter.removeAll()
                    }
                }
            } else {
                errorHappen(index, result) {
                    getNewsDate(show)
                }
            }
        }
    }

}
