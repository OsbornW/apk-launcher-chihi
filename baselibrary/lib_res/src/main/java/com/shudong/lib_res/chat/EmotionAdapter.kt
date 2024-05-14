package com.shudong.lib_res.chat

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by xiaokai on 2016/12/21.
 */
/**
 * Created by xiaokai on 2017/02/07.
 */
class EmotionAdapter(fm: FragmentManager?, num: Int) : FragmentStatePagerAdapter(
    fm!!
) {
    private val mPages: SparseArray<Fragment>
    private val fragmentNum: Int

    init {
        mPages = SparseArray()
        fragmentNum = num
    }

    override fun getItem(position: Int): Fragment {
        val f: Fragment = EmotionFragment.newInstance(position * 20)
        mPages.put(position, f)
        return f
    }

    override fun getCount(): Int {
        return fragmentNum
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (0 <= mPages.indexOfKey(position)) {
            mPages.remove(position)
        }
        super.destroyItem(container, position, `object`)
    }
}