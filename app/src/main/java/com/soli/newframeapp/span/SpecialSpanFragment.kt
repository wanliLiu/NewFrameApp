package com.soli.newframeapp.span

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.fragment_span.*

/**
 *
 * @author Soli
 * @Time 2019-08-08 18:04
 */
class SpecialSpanFragment : BaseToolbarFragment() {

    override fun getContentView() = R.layout.fragment_span

    override fun initView() {

        setTitle("富文本测试")
        testSelect.anotherTest()

        demoList.visibility = View.GONE
//        demoList.adapter = DemoAdapte()
//        demoList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
//            setDrawable(
//                ContextCompat.getDrawable(this@MainActivity, R.drawable.divider_drawable)!!
//            )
//        })
    }

    override fun initListener()  = Unit

    override fun initData()  = Unit

    private class DemoAdapte : RecyclerView.Adapter<DemoAdapte.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_demo_list, parent, false))
        }

        override fun getItemCount() = 20

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder.itemView is SpecialTextView)
                holder.itemView.anotherTest()
        }


        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}