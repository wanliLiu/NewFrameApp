package com.soli.newframeapp.demo

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soli.libcommon.base.BaseFragment
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 *
 * @author Soli
 * @Time 2020/7/24 10:08
 */
class TestFragment2 : BaseFragment() {
    override fun getContentView(): Int = R.layout.fragment_test

    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {
        artDetailList.adapter = object : BaseRecycleAdapter<String>(ctx!!) {

            override fun getItemCount(): Int = 1000

            override fun onCreateView(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(
                    inflater.inflate(
                        R.layout.item_test_fragment,
                        parent,
                        false
                    )
                ) {}
            }

            override fun onBindView(
                mholder: RecyclerView.ViewHolder?,
                itemType: Int,
                originalPosition: Int,
                realPosition: Int,
                payloads: List<Any>
            ) {
                mholder?.itemView?.findViewById<TextView>(R.id.testItem)?.text =
                    "数据开始-->$realPosition"
            }

        }
    }


}