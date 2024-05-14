package com.shudong.lib_base.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import java.lang.reflect.Field

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/1 10:39
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

/**
 * 给ViewPager2绑定Fragment
 */
fun ViewPager2.bindFragment(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    fragments: List<Fragment>
): ViewPager2 {
    //默认offscreenPageLimit = 0，需要懒加载无需设置即可
    adapter = object : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
    return this
}

fun ViewPager.bindFragment(
    fm: FragmentManager,
    fragments: List<Fragment>
): ViewPager {
    adapter = object : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragments.size

        override fun getItem(position: Int) = fragments[position]

    }
    return this
}

var pageListener: OnPageChangeCallback? = null
fun ViewPager2.pageChange(listener: (position: Int) -> Unit) {
    pageListener = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            listener.invoke(position)
        }
    }
    this.registerOnPageChangeCallback(pageListener as OnPageChangeCallback)
}

var vpListener: OnPageChangeListener? = null
fun ViewPager.pageChange(listener: (position: Int) -> Unit){
    vpListener = object :OnPageChangeListener{
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            listener.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }
    this.addOnPageChangeListener(vpListener!!)
}

fun ViewPager.destroyPageChange() {
    vpListener?.let { this.removeOnPageChangeListener(it) }
    vpListener = null
}

fun ViewPager2.destroyPageChange() {
    pageListener?.let { this.unregisterOnPageChangeCallback(it) }
    pageListener = null
}


fun ViewPager2.adjustTouchSlop() {
    try {
        val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)
    } catch (ignore: java.lang.Exception) {
    }
}