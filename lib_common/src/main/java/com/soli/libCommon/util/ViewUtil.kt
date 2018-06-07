package com.soli.libCommon.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.soli.libCommon.R
import com.soli.libCommon.view.RecyclerViewEmpty
import com.soli.pullupdownrefresh.PullRefreshLayout

/**
 * @author Soli
 * @Time 18-5-31 下午4:58
 */
object ViewUtil {

    /**
     * dip转成pixels
     */
    fun dip2px(dip: Int, context: Context): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics)
        return px.toInt()
    }

    /**
     * 给recyclerView加一个数据为空时EmptyView recyclerView外层最好用FrameLayout单独包着
     *
     * @param context
     * @param listview
     * @param resourceId
     * @param message
     * @param listener
     */
    fun setNoDataEmptyView(context: Context, listview: RecyclerViewEmpty, resourceId: Int = 0, message: String = "", paddingTop: Int = 0, listener: View.OnClickListener? = null) {
        var parentView = listview.parent as ViewGroup

        if (parentView is PullRefreshLayout && parentView.childCount > 0) {
            var child: View? = null
            for (index in 0..parentView.childCount) {
                if (parentView.getChildAt(index) is RecyclerViewEmpty) {
                    child = parentView.getChildAt(index)
                    break
                }
            }

            if (child != null && child is RecyclerViewEmpty) {
                parentView.removeView(child)

                val container = FrameLayout(context)
                val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                container.addView(child, layoutParams)

                parentView.addView(container, layoutParams)

                parentView.tryToGetChild()
                parentView.requestLayout()

                parentView = container
            }
        }

        removeItem(parentView, listview, R.id.id_recycler_empty)

        val inflate = getEmptyView(context, resourceId, message, listener)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, if (paddingTop > 0) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT)
        if (paddingTop > 0)
            inflate.setPadding(0, paddingTop, 0, 0)
        parentView.addView(inflate, params)
        inflate.bringToFront()
        listview.setEmptyView(inflate)
        listview.setTag(R.id.id_recycler_empty, inflate)
    }

    /**
     * @param context
     * @param listview
     * @param resourceId
     * @param message
     * @param listener
     */
    fun setNoDataEmptyView(context: Context, listview: RecyclerViewEmpty, resourceId: Int = 0, message: String = "", listener: View.OnClickListener? = null) {
        setNoDataEmptyView(context, listview, resourceId, message, 0, listener)
    }


    /**
     * 给listView加一个数据为空时EmptyView listview外层最好用FrameLayout单独包着
     *
     * @param context
     * @param listview
     * @param resourceId
     * @param message
     * @param listener
     */
    fun setNoDataEmptyView(context: Context, listview: AbsListView, resourceId: Int, message: String, listener: View.OnClickListener?) {
        val parentView = listview.parent as ViewGroup

        removeItem(parentView, listview, R.id.id_listView_empty)

        val inflate = getEmptyView(context, resourceId, message, listener)
        parentView.addView(inflate)
        listview.emptyView = inflate
        listview.setTag(R.id.id_listView_empty, inflate)
    }

    /**
     * @param context
     * @param lv
     */
    private fun removeAllItem(listview: AbsListView) {
        val parentView = listview.parent as ViewGroup
        removeItem(parentView, listview, R.id.id_listView_empty)
        listview.emptyView = null
    }

    /**
     * 删除上一个EmptyView
     *
     * @param parentView
     * @param lv
     * @param index
     */
    private fun removeItem(parentView: ViewGroup, listview: View, index: Int) {

        val tag = listview.getTag(index)
        if (tag != null && tag is View) {
            parentView.removeView(tag)
            listview.setTag(index, null)
        }
    }

    /**
     * 得到一个数据为空时的EmptyView
     *
     * @param context
     * @param resourceId
     * @param str
     * @param click
     * @return
     */
    private fun getEmptyView(context: Context, resourceId: Int = 0, str: String = "", click: View.OnClickListener? = null): View {

        val emptyView = View.inflate(context, R.layout.layout_empty, null)
        val txt_emtpy = emptyView.findViewById<TextView>(R.id.txt_emtpy)
        txt_emtpy.text = str

        if (resourceId != 0) {
            val image_empty = emptyView.findViewById<ImageView>(R.id.image_empty)
            image_empty.setImageResource(resourceId)
        }

        if (click != null) {
            emptyView.setOnClickListener(click)
        }

        return emptyView

    }
}
