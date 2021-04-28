package com.soli.libcommon.base.common

import android.content.Context
import com.soli.libcommon.R
import com.soli.libcommon.base.BaseMultiFragmentActivity
import com.soli.libcommon.util.openActivity
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment

/**
 *  用这个来承载Fragment
 * @author Soli
 * @Time 2020/5/20 14:23
 */
class CommonActivity : BaseMultiFragmentActivity() {


    companion object {
        private val requestFragment = "requestFragment"
        fun startFragment(ctx: Context, fragment: SupportFragment) {
            val tag = CommonFragmentManager.Instance.addFragment(fragment)
            ctx.openActivity<CommonActivity>(requestFragment to tag)
        }
    }

    private val fragmentTag: Long
        get() = intent.getLongExtra(requestFragment, 0)

    private val fragment by lazy { CommonFragmentManager.Instance[fragmentTag] }

    override fun getContentView() = R.layout.activity_common

    override fun initView() = Unit
    override fun initListener() = Unit

    override fun initData() {

        check(fragment != null) { "CommonActivity 中的fragment不能为空" }

        check(fragment is ISupportFragment) { "打开的fragment必须是ISupportFragment子类" }

        loadRootFragment(R.id.common_container, fragment as ISupportFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonFragmentManager.Instance.popFragment(fragmentTag)
    }
}