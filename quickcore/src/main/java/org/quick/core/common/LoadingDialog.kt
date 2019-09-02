package org.quick.core.common

import android.app.Activity
import android.view.WindowManager
import org.quick.core.R
import org.quick.dialog.QuickDialog
import org.quick.viewHolder.ViewHolder

/**
 * Created by work on 2017/3/21.
 *
 * @author chris zou
 * @mail chrisSpringSmell@gmail.com
 */

class LoadingDialog(var context: Activity?) {

    var holder: ViewHolder? = null
    /**
     * 弹出框
     *
     * @param isBlock 是否屏蔽返回键
     * @param hint     文字信息
     */
    @JvmOverloads
    fun show(hint: CharSequence = "加载中", isBlock: Boolean = false) {

        QuickDialog.Builder(context, R.layout.app_dialog_loading, R.style.appTheme_Dialog)
            .size(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            .blockBackKey(isBlock)
            .canceledOnTouchOutside(false)
            .show { _, holder ->
                this@LoadingDialog.holder = holder
                holder.setText(R.id.loadingHint, hint)
            }

    }

    fun dismiss() {
        QuickDialog.dismiss()
    }

    fun setLoadingHint(hint: String) {
        holder?.setText(R.id.loadingHint, hint)
    }
}