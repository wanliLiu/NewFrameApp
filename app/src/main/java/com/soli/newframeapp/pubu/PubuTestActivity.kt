package com.soli.newframeapp.pubu

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.RxJavaUtil
import com.soli.libcommon.view.recyclerview.decoration.SpacingDecoration
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.PullRefreshLayoutBinding
import com.soli.pullupdownrefresh.PullRefreshLayout

/*
 * @author soli
 * @Time 2018/12/28 23:18
 */
class PubuTestActivity : BaseActivity<PullRefreshLayoutBinding>() {

    private val list = mutableListOf<String>().apply {
        add("https://dev-img01-joker.taihe.com/0208/M00/89/8D/ChR47FwYkYKAAW79AABN7tsd56o502.jpg?_width=640&_height=356")
        add("https://dev-img01-joker.taihe.com/0209/M00/86/08/ChR47FwbBDqALq7_AAC-Xm_IM9g615.jpg?_width=720&_height=1280")
        add("https://dev-img01-joker.taihe.com/0209/M00/86/08/ChR461wbBDyALLlrAABArcsWCmo209.jpg?_width=640&_height=362")
        add("https://dev-img01-joker.taihe.com/0209/M00/86/08/ChR47FwbBD-AXl31AAGCaMleCN8630.jpg?_width=1600&_height=1000")
        add("https://dev-img01-joker.taihe.com/0208/M00/89/7F/ChR47FwYaPKAAU7xAACPc8LoTAY342.jpg?_width=636&_height=359")
        add("https://dev-img01-joker.taihe.com/0208/M00/8C/83/ChR461wYka2AQhQrAACV9DjtiEA885.jpg?_width=640&_height=430")
        add("https://dev-img01-joker.taihe.com/0207/M00/84/F3/ChR47FwYkWCABXtiAABX9XDUsz4527.jpg?_width=605&_height=407")
        add("https://dev-img01-joker.taihe.com/0207/M00/85/10/ChR461wZsciActNFAACuPTJUZJY990.jpg?_width=580&_height=870")
        add("https://dev-img01-joker.taihe.com/0209/M00/86/08/ChR47FwbBDqALq7_AAC-Xm_IM9g615.jpg?_width=720&_height=1280")
        add("https://dev-img01-joker.taihe.com/0207/M00/85/0F/ChR47FwZsXOADg1UAAEmfHTK9D4488.jpg?_width=580&_height=870")
        add("https://dev-img01-joker.taihe.com/0207/M00/85/0F/ChR47FwZuTWACb_6AABL46xmGu0146.jpg?_width=720&_height=432")
        add("https://dev-img01-joker.taihe.com/0207/M00/85/0F/ChR47FwZtQGAec1IAADBjIVtaY4992.jpg?_width=580&_height=580")
        add("https://dev-img01-joker.taihe.com/0207/M00/85/0F/ChR47FwZsgGAAzLcAAEOdBky6fI612.jpg?_width=580&_height=871")
        add("https://dev-img01-joker.taihe.com/0207/M00/84/F4/ChR47FwYkhOAT2XdAAA6gJHjlGE246.jpg?_width=571&_height=372")
        add("https://dev-img01-joker.taihe.com/0208/M00/89/8D/ChR47FwYkZSAIJynAADEgYppxXA640.jpg?_width=639&_height=398")
        add("https://dev-img01-joker.taihe.com/0208/M00/89/8D/ChR47FwYkXKAHNS2AAB5ydlY7NY398.jpg?_width=598&_height=374")
    }
    private val adapter by lazy { PhotoAdapter(ctx) }
    override fun initView() {
        title = "瀑布流"

        val manager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        manager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_NONE

        binding.itemList.setHasFixedSize(true)

        binding.refreshLayout.setPageSize(20)

        binding.itemList.layoutManager = manager
        binding.itemList.adapter = adapter
        binding.itemList.markIsStaggeredGridLayoutManager()

        val space = resources.getDimensionPixelOffset(R.dimen.dimen_sw_15dp)
        val decoration = SpacingDecoration(space, space, true)
        binding.itemList.addItemDecoration(decoration)
    }

    override fun initListener() {

        binding.refreshLayout.setRefreshListener(object : PullRefreshLayout.onRefrshListener {
            override fun onPullupRefresh(actionFromClick: Boolean) {
                RxJavaUtil.delayAction(1000) {
                    binding.refreshLayout.onRefreshComplete()
                    adapter.addAll_Range(list)
                }
            }

            override fun onPullDownRefresh() {
                RxJavaUtil.delayAction(1000) {
                    binding.refreshLayout.onRefreshComplete()
                    adapter.removeAll()
                    adapter.addAll_Range(list)
                }
            }

        })
    }

    override fun initData() {

        adapter.addAll_Range(list)
    }
}