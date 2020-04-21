package com.soli.libcommon.util

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewConfiguration

/**
 * Created by Soli on 2016/7/28.
 */
object ViewListener {

    /**
     * 一般视图添加 单击和双击事件，运用场景广泛
     *
     * 注册一个双击事件
     */
    fun registerDoubleClickListener(view: View, callListener: ((view: View, isDoubleClick: Boolean) -> Unit)?) {
        view.setOnClickListener(object : View.OnClickListener {
            private var waitDouble = true
            private val handler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    callListener?.invoke(msg.obj as View, false)
                }
            }

            //等待双击
            override fun onClick(v: View) {
                if (waitDouble) {
                    waitDouble = false        //与执行双击事件
                    object : Thread() {
                        override fun run() {
                            try {
                                sleep(ViewConfiguration.getDoubleTapTimeout().toLong())
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            //等待双击时间，否则执行单击事件
                            if (!waitDouble) {
                                //如果过了等待事件还是预执行双击状态，则视为单击
                                waitDouble = true
                                val msg = handler.obtainMessage()
                                msg.obj = v
                                handler.sendMessage(msg)
                            }
                        }
                    }.start()
                } else {
                    waitDouble = true
                    callListener?.invoke(v, true)    //执行双击
                }
            }
        })
    }

    /**
     *
     * 运用场景单一,主要是针对那种已经选择了的，常用于比如首页几个tab切换的时候，只有选中了的，才可以双击，其他情况都是单击
     *
     * 注册一个双击事件
     */
    fun registerSelectDoubleClickListener(
        view: View,
        signelistener: ((view: View, isInCheckDoubleClick: Boolean) -> Unit)?,
        doublelistener: ((view: View) -> Unit)?
    ) {
        view.setOnClickListener(object : View.OnClickListener {
            private var waitDouble = true
            private val handler = @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    signelistener?.invoke(msg.obj as View, msg.arg1 == 1)
                }
            }

            //等待双击
            override fun onClick(v: View) {
                if (view.isSelected) {
                    if (waitDouble) {
                        waitDouble = false        //与执行双击事件
                        object : Thread() {
                            override fun run() {
                                try {
                                    sleep(ViewConfiguration.getDoubleTapTimeout().toLong())
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                                //等待双击时间，否则执行单击事件
                                if (!waitDouble) {
                                    //如果过了等待事件还是预执行双击状态，则视为单击
                                    waitDouble = true
                                    val msg = handler.obtainMessage()
                                    msg.obj = v
                                    msg.arg1 = 1
                                    handler.sendMessage(msg)
                                }
                            }
                        }.start()
                    } else {
                        waitDouble = true
                        doublelistener?.invoke(v)    //执行双击
                    }
                } else {
                    val msg = handler.obtainMessage()
                    msg.obj = v
                    msg.arg1 = 0
                    handler.sendMessage(msg)
                }
            }
        })
    }
}
