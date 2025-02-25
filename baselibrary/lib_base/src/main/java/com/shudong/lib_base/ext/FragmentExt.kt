package com.shudong.lib_base.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/29 10:53
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun FragmentActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun FragmentActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}

fun String.reflectFragment() =  Class.forName(this).newInstance() as Fragment

// 扩展函数，Fragment 中用于替换 Fragment
fun Fragment.replaceChildFragment(fragment: Fragment, frameId: Int) {
    childFragmentManager.inTransactionChild {
        replace(frameId, fragment)
    }
}

// 内联函数，简化 FragmentTransaction 的使用
inline fun FragmentManager.inTransactionChild(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action(this) // 执行传入的操作
    }.commitAllowingStateLoss() // 提交事务
}