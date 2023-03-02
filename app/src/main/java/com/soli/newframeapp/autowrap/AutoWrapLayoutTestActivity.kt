package com.soli.newframeapp.autowrap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.view.flexboxlayout.AutoWrapAdapter
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.ActivityAutoWrapBinding

/**
 *
 * @author Soli
 * @Time 2018/11/27 14:22
 */
class AutoWrapLayoutTestActivity : BaseActivity<ActivityAutoWrapBinding>() {

    private var showRecycle = false

    override fun initView() {
        title = "AutoWrapLayout"
        binding.AutoRecycle.visibility = if (showRecycle) View.VISIBLE else View.GONE
        binding.wrapLayout.visibility = if (!showRecycle) View.VISIBLE else View.GONE
    }

    override fun initListener() {
        binding.btnAdd.setOnClickListener {
            showRecycle = !showRecycle
            binding.AutoRecycle.visibility = if (showRecycle) View.VISIBLE else View.GONE
            binding.warp.visibility = if (!showRecycle) View.VISIBLE else View.GONE
            binding.btnAdd.text = if (showRecycle) "用FlexboxLayoutManager" else "自己定义的"
        }
    }

    override fun initData() {

        val list = ArrayList<String>()
        list.add("薛之谦")
        list.add("真爱粉")
        list.add("薛之谦")
        list.add("真爱粉")
        list.add("雪")
        list.add("真爱谦")
        list.add("薛之谦w我爱你w")
        list.add("真爱粉薛之谦")
        list.add("真爱之谦")
        list.add("吴世勋")
        list.add("周杰伦的我")
        list.add("的我范儿")
        list.add("bigbang")
        list.add("艾薇儿")
        list.add("第五期的期望·")
        list.add("harrypotter")
        list.add("huawei")
        list.add("apple")
        list.add("samsung")
        list.add("雪")
        list.add("的")
        list.add("我的")
        list.add("的")
        list.add("我的")
        list.add("的")
        list.add("我的快")
        list.add("开始快死了")
        list.add("我的死了")
        list.add("我的")
        list.add("速度sdsd")
        list.add("死了s")
        list.add("死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了死了")
        list.add("sssssssssssss")
        for (index in 0 until 100) {

            list.add(if (index % 2 == 0) "内容$index" else "----内容$index----")
        }

        useCustom(list)
        useFlexboxLayoutManager(list)
    }

    private fun useFlexboxLayoutManager(list: ArrayList<String>) {
        val manager = FlexboxLayoutManager(ctx)
        //内容行，从左到右依次排列
        manager.justifyContent = JustifyContent.CENTER

        binding.AutoRecycle.adapter = adapter(list)
        binding.AutoRecycle.addItemDecoration(FlexboxItemDecoration(ctx).apply { setDrawable(ctx.resources.getDrawable(R.drawable.listdivider_5dp)) })
        binding.AutoRecycle.layoutManager = manager
    }


    private inner class adapter(list: ArrayList<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

        private val mlist = list

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            val view = LayoutInflater.from(ctx).inflate(R.layout.item_auto_warp, p0, false)
            return viewHolder(view)
        }

        override fun getItemCount() = mlist.size

        override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
//            p0.itemView.setBackgroundColor(Color.GREEN)
            if (p0.itemView is Button) {
                (p0.itemView as Button).text = mlist[p1]
            }
        }


    }

    private class viewHolder(ctx: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(ctx)

    private fun useCustom(list: ArrayList<String>) {
        binding.wrapLayout.setAdapter(TestAutoWrapAdapter(ctx, list) as AutoWrapAdapter<String>)
        binding.wrapLayout.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            if (view is Button)
                ToastUtils.showLongToast("$position -----内容：${view.text}")
        })
    }
}