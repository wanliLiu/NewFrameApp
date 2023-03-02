package com.soli.newframeapp.net

import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.net.DataType
import com.soli.libcommon.util.ViewUtil
import com.soli.newframeapp.databinding.PullRefreshLayoutBinding
import com.soli.newframeapp.model.StoryList
import com.soli.pullupdownrefresh.PullRefreshLayout
import java.text.SimpleDateFormat
import java.util.*

class NetWorkTestActivity : BaseActivity<PullRefreshLayoutBinding>() {

    private val mAadapter: NewsAdapter by lazy { NewsAdapter(ctx) }
    private var index = 0
    override fun initView() {
        title = "网络测试"

        binding.refreshLayout.setPageSize(20)

        binding.itemList.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            adapter = mAadapter
        }

    }

    override fun initListener() {

        binding.refreshLayout.setRefreshListener(object : PullRefreshLayout.onRefrshListener {
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


        ApiHelper.build {
            baseUrl = "http://news.at.zhihu.com/api/4/news/before/"
            bodyType = DataType.JSON_OBJECT
            clazz = StoryList::class.java
            url = simpleDateFormat.format(calendar.time)
        }.get<StoryList> { result ->

            ViewUtil.setNoDataEmptyView(context = ctx,
                listview = binding.itemList,
                message = "没有数据哦,测试RecyclerView加载没有数据的空视图显示!",
                listener = {
                    index = 0
                    getNewsDate(true)
                })

            dismissProgress()
            binding.refreshLayout.onRefreshComplete()
            if (result.isSuccess && result.result != null) {

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
