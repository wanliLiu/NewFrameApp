package com.soli.libcommon.view.flexboxlayout

import android.content.Context
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import com.soli.libcommon.base.BaseListAdapter
import com.soli.libcommon.util.RxJavaUtil

/**
 * @author Soli
 * @Time 2018/11/27 14:32
 */
abstract class AutoWrapAdapter<T> : BaseListAdapter<T> {

    private var mLayout: AutoWrapLayout? = null
    private var singlelistener: OnItemClickListener? = null
    private var longListener: OnItemLongClickListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, list: MutableList<T>?) : super(context, list)

//    /**
//     * @param context
//     * @param dpValue
//     * @return
//     */
//    private fun dip2px(context: Context, dpValue: Float): Int {
//        val scale = context.resources.displayMetrics.density
//        return (dpValue * scale + 0.5f).toInt()
//    }

    /**
     * Add all the View controls to the custom SexangleViewList
     * When you use this SexangleViewList should be instantiated first and then call
     * Because here is not intercept and throw such as null pointer exception
     * The name is called mySexangleView View passed in must be empty
     * Of course the ViewGroup transfer time must also be empty
     */
    private fun getAllViewAdd() {
        if (mLayout == null) return

        for (i in 0 until count) {
            val viewItem = getView(i, null, null)
            //            viewItem.setDuplicateParentStateEnabled(true);

            //            FrameLayout layout = new FrameLayout(ctx);
            //            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            //            int _5Marign = dip2px(ctx, 5);
            //            int topBottom = mLayout.getMaxRows() == 1 ? 0 : _5Marign;
            //            params.setMargins(0, topBottom, _5Marign + _5Marign, topBottom);
            //            layout.addView(viewItem, params);

            mLayout!!.addView(viewItem)
        }
    }

    /**
     * The refresh AutoWrapView interface
     * Here is set to True representative will execute reset CustomListView twice
     * This method is called before, please first instantiation mySexangleListView
     * Otherwise, this method in redraw CustomListView abnormal happens
     */
    override fun notifyDataSetChanged() {
        notifyCustomListView(mLayout)
    }

    /**
     * Redraw the Custom controls for the first time, you should invoke this method
     * In order to ensure that each load data do not repeat to get rid of the
     * custom of the ListView all View objects
     * The following will be set up to monitor events as controls
     * First load regardless whether OnItemClickListener and OnItemLongClickListener is NULL,
     * they do not influence events Settings
     *
     * @param formateList
     */
    fun notifyCustomListView(formateList: AutoWrapLayout?) {
        mLayout = formateList
        mLayout?.apply {
            removeAllViews()
            getAllViewAdd()
            setOnItemClickListener(singlelistener)
            setOnItemLongClickListener(longListener)
        }
    }


    /**
     * Set the click event of each View, external can realize the interface for your call
     *
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.singlelistener = listener
        if (singlelistener == null) {
            return
        }
        RxJavaUtil.runOnThread {
            for (i in 0 until mLayout!!.childCount) {
                val view = mLayout!!.getChildAt(i)
                view.setOnClickListener { v ->
                    if (singlelistener != null) {
                        singlelistener!!.onItemClick(null, v, i, count.toLong())
                    }
                }
            }
        }
    }

    /**
     * Set each long press event, the View outside can realize the interface for your call
     *
     * @param listener
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.longListener = listener
        if (longListener == null) {
            return
        }
        RxJavaUtil.runOnThread {
            for (i in 0 until mLayout!!.childCount) {
                val view = mLayout!!.getChildAt(i)
                view.setOnLongClickListener { v ->
                    if (longListener != null) {
                        longListener!!.onItemLongClick(null, v, i, count.toLong())
                    }
                    true
                }
            }
        }
    }
}
