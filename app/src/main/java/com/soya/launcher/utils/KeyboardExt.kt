package com.soya.launcher.utils

import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.soya.launcher.ui.dialog.CusKeyboard
import com.soya.launcher.ui.dialog.KeyboardDialog

fun showKeyboard(setup: (CusKeyboard.() -> Unit)? = null) =

      CusKeyboard.newInstance {
          setup?.invoke(this)


}