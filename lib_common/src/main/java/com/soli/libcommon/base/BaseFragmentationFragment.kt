package com.soli.libcommon.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.FragmentActivity
import me.yokeyword.fragmentation.ExtraTransaction
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.ISupportFragment.LaunchMode
import me.yokeyword.fragmentation.SupportFragmentDelegate
import me.yokeyword.fragmentation.SupportHelper
import me.yokeyword.fragmentation.anim.FragmentAnimator

/**
 *展示自定制的MySupportFragment，不继承SupportFragment
 * @author Soli
 * @Time 2020/4/22 15:05
 */
abstract class BaseFragmentationFragment : BaseFragment(), ISupportFragment {

    private val mDelegate = SupportFragmentDelegate(this)
    protected var _mActivity: FragmentActivity? = null

    override fun getSupportDelegate(): SupportFragmentDelegate {
        return mDelegate
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    override fun extraTransaction(): ExtraTransaction {
        return mDelegate.extraTransaction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDelegate.onAttach(activity)
        _mActivity = mDelegate.activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
    }

    override fun onCreateAnimation(
        transit: Int,
        enter: Boolean,
        nextAnim: Int
    ): Animation? {
        return mDelegate.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDelegate.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mDelegate.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mDelegate.onPause()
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mDelegate.setUserVisibleHint(isVisibleToUser)
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     *
     *
     * The runnable will be run after all the previous action has been run.
     *
     *
     * 前面的事务全部执行后 执行该Action
     *
     */
    @Deprecated("Use {@link #post(Runnable)} instead.")
    override fun enqueueAction(runnable: Runnable) {
        mDelegate.enqueueAction(runnable)
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     *
     *
     * The runnable will be run after all the previous action has been run.
     *
     *
     * 前面的事务全部执行后 执行该Action
     */
    override fun post(runnable: Runnable) {
        mDelegate.post(runnable)
    }

    /**
     * Called when the enter-animation end.
     * 入栈动画 结束时,回调
     */
    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        mDelegate.onEnterAnimationEnd(savedInstanceState)
    }

    /**
     * Lazy initial，Called when fragment is first called.
     *
     *
     * 同级下的 懒加载 ＋ ViewPager下的懒加载  的结合回调方法
     */
    override fun onLazyInitView(savedInstanceState: Bundle?) {
        mDelegate.onLazyInitView(savedInstanceState)
    }

    /**
     * Called when the fragment is visible.
     * 当Fragment对用户可见时回调
     *
     *
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    override fun onSupportVisible() {
        mDelegate.onSupportVisible()
    }

    /**
     * Called when the fragment is invivible.
     *
     *
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    override fun onSupportInvisible() {
        mDelegate.onSupportInvisible()
    }

    /**
     * Return true if the fragment has been supportVisible.
     */
    override fun isSupportVisible(): Boolean {
        return mDelegate.isSupportVisible
    }

    /**
     * Set fragment animation with a higher priority than the ISupportActivity
     * 设定当前Fragmemt动画,优先级比在SupportActivity里高
     */
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return mDelegate.onCreateFragmentAnimator()
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    override fun getFragmentAnimator(): FragmentAnimator {
        return mDelegate.fragmentAnimator
    }

    /**
     * 设置Fragment内的全局动画
     */
    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator) {
        mDelegate.fragmentAnimator = fragmentAnimator
    }

    /**
     * 按返回键触发,前提是SupportActivity的onBackPressed()方法能被调用
     *
     * @return false则继续向上传递, true则消费掉该事件
     */
    override fun onBackPressedSupport(): Boolean {
        return mDelegate.onBackPressedSupport()
    }

    /**
     * 类似 [Activity.setResult]
     *
     *
     * Similar to [Activity.setResult]
     *
     * @see .startForResult
     */
    override fun setFragmentResult(resultCode: Int, bundle: Bundle) {
        mDelegate.setFragmentResult(resultCode, bundle)
    }

    /**
     * 类似  [Activity.onActivityResult]
     *
     *
     * Similar to [Activity.onActivityResult]
     *
     * @see .startForResult
     */
    override fun onFragmentResult(
        requestCode: Int,
        resultCode: Int,
        data: Bundle
    ) {
        mDelegate.onFragmentResult(requestCode, resultCode, data)
    }

    /**
     * 在start(TargetFragment,LaunchMode)时,启动模式为SingleTask/SingleTop, 回调TargetFragment的该方法
     * 类似 [Activity.onNewIntent]
     *
     *
     * Similar to [Activity.onNewIntent]
     *
     * @param args putNewBundle(Bundle newBundle)
     * @see .start
     */
    override fun onNewBundle(args: Bundle) {
        mDelegate.onNewBundle(args)
    }

    /**
     * 添加NewBundle,用于启动模式为SingleTask/SingleTop时
     *
     * @see .start
     */
    override fun putNewBundle(newBundle: Bundle) {
        mDelegate.putNewBundle(newBundle)
    }
    /****************************************以下为可选方法(Optional methods) */ // 自定制Support时，可移除不必要的方法
    /**
     * 隐藏软键盘
     */
    protected fun hideSoftInput() {
        mDelegate.hideSoftInput()
    }

    /**
     * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
     */
    protected fun showSoftInput(view: View?) {
        mDelegate.showSoftInput(view)
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment?) {
        mDelegate.loadRootFragment(containerId, toFragment)
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    open fun showHideFragment(
        showFragment: ISupportFragment,
        hideFragment: ISupportFragment
    ) {
        mDelegate.showHideFragment(showFragment, hideFragment)
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    fun loadMultipleRootFragment(
        containerId: Int,
        showPosition: Int,
        vararg toFragments: ISupportFragment?
    ) {
        mDelegate.loadMultipleRootFragment(containerId, showPosition, *toFragments)
    }

    fun loadRootFragment(
        containerId: Int,
        toFragment: ISupportFragment?,
        addToBackStack: Boolean,
        allowAnim: Boolean
    ) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim)
    }

    open fun start(toFragment: ISupportFragment?) {
        mDelegate.start(toFragment)
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    open fun start(toFragment: ISupportFragment?, @LaunchMode launchMode: Int) {
        mDelegate.start(toFragment, launchMode)
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    open fun startForResult(toFragment: ISupportFragment?, requestCode: Int) {
        mDelegate.startForResult(toFragment, requestCode)
    }

    /**
     * Start the target Fragment and pop itself
     */
    open fun startWithPop(toFragment: ISupportFragment?) {
        mDelegate.startWithPop(toFragment)
    }

    /**
     * @see .popTo
     * @see .start
     */
    open fun startWithPopTo(
        toFragment: ISupportFragment?,
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean
    ) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment)
    }

    open fun replaceFragment(
        toFragment: ISupportFragment?,
        addToBackStack: Boolean
    ) {
        mDelegate.replaceFragment(toFragment, addToBackStack)
    }

    open fun pop() {
        mDelegate.pop()
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     *
     *
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean
    ) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment)
    }

    /**
     * 获取栈内的fragment对象
     */
    fun <T : ISupportFragment?> findChildFragment(fragmentClass: Class<T>?): T {
        return SupportHelper.findFragment(childFragmentManager, fragmentClass)
    }
}