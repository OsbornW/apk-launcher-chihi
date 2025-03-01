package com.chihihx.launcher.ui.dialog

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.chihihx.launcher.R
import com.chihihx.launcher.utils.AndroidSystem
import java.lang.reflect.ParameterizedType

abstract class SingleDialogFragment<VB : ViewBinding> : DialogFragment() {

    companion object {
        const val NO_ANIMATION = -1
    }

    protected lateinit var binding: VB
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 初始化 ViewBinding
        binding = initViewBinding(inflater, container)
        init(binding.root)
        initBefore(binding.root)
        initBind(binding.root)
        return binding.root
    }

    /**
     * 自动初始化 ViewBinding
     */
    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        val bindingClass = (javaClass.genericSuperclass as? ParameterizedType)
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VB>
            ?: throw IllegalStateException("Cannot find ViewBinding class.")
        val method = bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return method.invoke(null, inflater, container, false) as VB
    }

    protected open fun init(view: View) {}

    protected open fun initBefore(view: View) {}

    protected open fun initBind(view: View) {}

    override fun onResume() {
        super.onResume()
        dialog?.window?.let { window ->
            window.setBackgroundDrawable(getBackgroundDrawable())
            window.setGravity(getGravity())
            if (getAnimation() != NO_ANIMATION) {
                window.setWindowAnimations(getAnimation())
            }
            window.setDimAmount(getDimAmount())
            dialog?.setCanceledOnTouchOutside(canOutSide())
            val params = window.attributes
            val (width, height) = getWidthAndHeight()
            params.width = width
            params.height = height
            window.attributes = params
        }
    }

    private fun getBackgroundDrawable(): Drawable? {
        return ResourcesCompat.getDrawable(
            resources,
            if (isMaterial()) R.drawable.dialog_material_radius_white_background
            else R.drawable.dialog_material_transparent_background,
            requireContext().theme
        )
    }

    protected fun blur(root: View, blur: ImageView) {
        activity?.let { activity ->
            val decorView = activity.window.decorView
            AndroidSystem.blur(activity, decorView, root, blur)
        }
    }

    protected fun setVisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(activity, msg, duration).show()
    }

    fun getThis(): Fragment = this

    protected open fun getWidthAndHeight(): Pair<Int, Int> {
        return ViewGroup.LayoutParams.MATCH_PARENT to ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun setDimAmount(dimAmount: Float) {
        dialog?.window?.setDimAmount(dimAmount)
    }

    fun standShow(manager: FragmentManager) {
        show(manager, javaClass.name)
    }

    protected open fun getDimAmount(): Float = 0.5f

    protected open fun getAnimation(): Int = NO_ANIMATION

    protected open fun getGravity(): Int = Gravity.CENTER

    protected open fun canOutSide(): Boolean = false

    override fun show(manager: FragmentManager, tag: String?) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(this, tag)
        transaction.commitAllowingStateLoss()
    }

    protected open fun isMaterial(): Boolean = false
}
