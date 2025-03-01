package com.chihihx.launcher.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chihihx.launcher.ui.fragment.GuideLanguageFragment

/**
 * 替换当前 Fragment，并可选地添加到返回栈。
 */
fun FragmentManager.navigateTo(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    tag: String? = null
) {
    // 查找当前容器中是否已经有该类型的 Fragment
    val existingFragment = findFragmentById(containerId)

    // 如果已经有相同的 Fragment 实例，不需要重新初始化
    if (existingFragment != null && existingFragment::class == fragment::class) {
        return
    }

    val transaction = beginTransaction()
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    transaction.replace(containerId, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}

/**
 * 返回到上一页面。
 * @return true 表示成功回退，false 表示无返回栈可用。
 */
fun FragmentManager.navigateBack(): Boolean {
    return if (backStackEntryCount > 1) {
        popBackStack()
        true
    } else {
        false
    }
}

/**
 * 返回到指定标签的 Fragment。
 * @return true 表示成功回退，false 表示指定标签的 Fragment 不存在。
 */
fun FragmentManager.navigateBackTo(tag: String): Boolean {
    return if (findFragmentByTag(tag) != null) {
        popBackStack(tag, 0)
        true
    } else {
        false
    }
}

/**
 * 清空返回栈并显示新的 Fragment。
 */
fun FragmentManager.clearAndNavigate(
    containerId: Int,
    fragment: Fragment,
    tag: String? = null
) {
    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    navigateTo(containerId, fragment, addToBackStack = false, tag = tag)
}

/**
 * 清空返回栈并显示新的 Fragment。
 */
fun FragmentManager.clearStack(
    tag:String? = GuideLanguageFragment::class.simpleName
) {
    //popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
   /* if(tag.isNullOrEmpty())popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    else popBackStack(tag, 0)*/

    for (i in backStackEntryCount - 1 downTo 0) {
        val entry = getBackStackEntryAt(i)
        if (entry.name == GuideLanguageFragment::class.simpleName) {
            //popBackStack(entry.id, 0) // 不清除语言页面本身
            break
        }/*else{
            removeFragmentAt(i)
        }*/
    }

}

fun FragmentManager.removeFragmentAt(index: Int) {
    if (index in 0..<backStackEntryCount) {
        val entry = getBackStackEntryAt(index)
        val fragment = findFragmentById(entry.id)
        if (fragment != null) {
            beginTransaction()
                .remove(fragment)
                .commit()
        }
    } else {
        throw IndexOutOfBoundsException("Invalid index: $index")
    }
}

/**
 * 清除从指定的 Fragment（通过其 Tag 或 Id）开始的回退栈内容
 * 包括指定的 Fragment 本身及其后的所有回退栈条目。
 */
fun FragmentManager.clearStackByTag(
    tag:String? = GuideLanguageFragment::class.simpleName
) {
    popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}


