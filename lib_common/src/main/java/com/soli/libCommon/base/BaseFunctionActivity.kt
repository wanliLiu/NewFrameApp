package com.soli.libCommon.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.r0adkll.slidr.Slidr
import com.soli.libCommon.R
import com.soli.libCommon.util.KeyBoardUtils
import com.soli.libCommon.util.StatusBarUtil
import com.soli.libCommon.view.root.LoadingDialog

/**
 * @author Soli
 * @Time 18-5-15 下午3:07
 */
abstract class BaseFunctionActivity : BaseFixOTranslucentActivity(), BaseInterface {

    /**
     * 默认竖屏，不支持横竖自定转换
     */
    protected val isScreenOnlyPORTRAIT = true

    /**
     * 上下午context
     */
    protected val ctx by lazy { this }

    private var dialog: LoadingDialog? = null

    @JvmField
    protected var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    /**
     * 是否需要右滑动删除
     */
    private fun needRightSild() = !needDealKeyBoard() && needSliderActivity()

    /**
     *  只要当前Activity需要进行输入，那么就不允许滑动，键盘的优先级最高，其次才是滑动开关
     */
    protected fun setStatusBarColor() {
        val needSilder = needRightSild()
        if (needSilder) {
            Slidr.attach(this)
        }

        if (needActioinStatusBarColor()) {
            val color = resources.getColor(R.color.B2)
            val islight = setStatusBarMode()
            if (!dealCusotomStatus(color, if (islight) 0 else 38)) {
                if (needSilder) {
                    StatusBarUtil.setColorForSwipe(this, color, if (islight) 0 else 38)
                } else
                    StatusBarUtil.setColor(this, color, if (islight) 0 else 38)
            }
        }
    }

    /**
     *是否需要状态栏是白色字体
     */
    fun setStatusBarMode(needWhite: Boolean = false) =
        if (needWhite) {
            StatusBarUtil.setLightMode(ctx)
            false
        } else
            StatusBarUtil.setDarkMode(this)

    /**
     * 在设置状态栏原色之前，是否要进行特殊处理，进行了特殊处理返回true,比如顶部都是图片 透明之类的
     */
    open fun dealCusotomStatus(color: Int, statusBarAlpha: Int) = false

    open fun needSliderActivity() = true

    open fun needActioinStatusBarColor() = true

    /**
     * 当前页面是否会键盘弹出，或是当前页面是否有输入的，默认为不需要输入
     */
    open fun needDealKeyBoard() = false

    /**
     *
     */
    fun showProgressDialog() {
        if (dialog == null) {
            dialog = LoadingDialog(ctx)
        }

        if (!dialog!!.isShowing)
            dialog!!.show()
    }

    /**
     *
     */
    fun dissProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    override fun onPause() {
        super.onPause()
        dissProgressDialog()
    }


//    /**
//     * 加载系统默认设置，字体不随用户设置变化
//     * FIXME 注意下这个加上了，ProgressDialog就显示不出来了
//     */
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        if (newConfig.fontScale != 1f)//非默认值
//            resources
//        super.onConfigurationChanged(newConfig)
//    }
//
//    override fun getResources(): Resources {
//        val res = super.getResources()
//        if (res.configuration.fontScale != 1f) {//非默认值
//            val newConfig = res.configuration
//            newConfig.setToDefaults()//设置默认
//            res.updateConfiguration(newConfig, res.displayMetrics)
//        }
//        return res
//    }


    /**
     * fragment management
     */
    fun addFragment(fragment: Fragment, containerId: Int): Fragment {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        transaction.commitAllowingStateLoss()
        return fragment
    }

    /**
     * 如果键盘没有关闭，就关闭键盘
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            if (needDealKeyBoard()) {
                if (null != this.currentFocus && KeyBoardUtils.isShouldHideInput(currentFocus, event)) {
                    val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (mInputMethodManager != null && this.currentFocus != null)
                        return mInputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return super.onTouchEvent(event)
    }

}