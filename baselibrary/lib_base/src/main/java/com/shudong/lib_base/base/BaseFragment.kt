package com.shudong.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.shudong.lib_base.ext.loading.showLoadingView
import com.shudong.lib_base.ext.loading.showLoadingViewDismiss
import java.lang.reflect.ParameterizedType

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 17:31
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
abstract class BaseFragment<VDB : ViewDataBinding>:Fragment(),BaseVMView {

    protected val mBind: VDB by lazy {
        var type = javaClass.genericSuperclass
        var vbClass: Class<VDB> = (type as ParameterizedType).actualTypeArguments[0] as Class<VDB>
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(this, layoutInflater) as VDB
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initdata()
        initClick()
        initObserver()
    }

    override fun loadingView(msg: String) {
        showLoadingView(msg)
    }

    override fun hideLoading() {
        showLoadingViewDismiss()
    }
}