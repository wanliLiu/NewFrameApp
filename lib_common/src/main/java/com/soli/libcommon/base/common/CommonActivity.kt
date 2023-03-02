package com.soli.libcommon.base.common

import android.content.Context
import android.os.Bundle
import com.soli.libcommon.R
import com.soli.libcommon.base.BaseMultiFragmentActivity
import com.soli.libcommon.databinding.ActivityCommonBinding
import com.soli.libcommon.util.openActivity
import me.yokeyword.fragmentation.SupportFragment

/**
 *  用这个来承载Fragment
 * @author Soli
 * @Time 2020/5/20 14:23
 */
class CommonActivity : BaseMultiFragmentActivity<ActivityCommonBinding>() {


    companion object {

        private val FlagName = "fragment_class_name"
        private val FlagParams = "fragment_params"

        fun startFragment(ctx: Context, fragmentClassName: String, params: Bundle? = null) {
            ctx.openActivity<CommonActivity>(FlagName to fragmentClassName, FlagParams to params)
        }
    }

    private val fragmentClass: String
        get() = intent.getStringExtra(FlagName) ?: ""

    private val params: Bundle?
        get() = intent.getBundleExtra(FlagParams)

    private val fragment by lazy {
        supportFragmentManager.fragmentFactory.instantiate(classLoader, fragmentClass).apply {
            arguments = params
        } as SupportFragment
    }

    override fun onBackPressedSupport() {
        if (fragment.onBackPressedSupport()) {
            //do nothing
        } else
            super.onBackPressedSupport()
    }


    override fun initView() {

        check(fragment != null) { "CommonActivity 中的fragment不能为空" }

        check(fragment is SupportFragment) { "打开的fragment必须是BaseToolbarFragment子类" }

        supportFragmentManager.beginTransaction()
            .add(R.id.common_container, fragment, fragmentClass).commit()
    }

    override fun initListener() = Unit

    override fun initData() = Unit
}