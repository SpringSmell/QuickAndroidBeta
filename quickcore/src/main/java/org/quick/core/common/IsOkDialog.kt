package org.quick.core.common

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.quick.core.R
import org.quick.dialog.QuickDialog

class IsOkDialog(var context: Context?) {

    private var title = ""
    private var content = ""
    private var leftTxt = ""
    private var rightTxt = ""
    /**
     * 是否阻塞返回键
     */
    private var isBlockBack = false
    private var customView: View? = null
    private var isCancelTouchOutSide: Boolean = true

    private var listener: ((v: View, isRight: Boolean) -> Unit)? = null

    fun setTitle(title: String): IsOkDialog {
        this.title = title
        return this
    }

    fun setContent(content: String): IsOkDialog {
        this.content = content
        return this
    }

    fun setBtnLeft(value: String): IsOkDialog {
        this.leftTxt = value
        return this
    }

    fun setBtnRight(value: String): IsOkDialog {
        this.rightTxt = value
        return this
    }

    fun setCustomView(value: View): IsOkDialog {
        this.customView = value
        return this
    }

    /**
     * 是否阻塞返回键
     */
    fun setBlockBack(isBlockBack: Boolean): IsOkDialog {
        this.isBlockBack = isBlockBack
        return this
    }

    fun setCancelTouchOutSide(isCancelTouchOutSide: Boolean): IsOkDialog {
        this.isCancelTouchOutSide = isCancelTouchOutSide
        return this
    }

    /**
     * 常规问答，不带标题
     */
    fun default(content: String): IsOkDialog {
        this.content = content
        this.leftTxt = "取消"
        this.rightTxt = "确定"
        return this
    }

    fun show(listener: ((v: View, isRight: Boolean) -> Unit)?=null) {
        this.listener = listener
        QuickDialog.Builder(context, R.layout.app_dialog_is_ok)
            .blockBackKey(isBlockBack)
            .canceledOnTouchOutside(isCancelTouchOutSide)
            .show { _, holder ->
                holder.setVisibility(
                    if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
                    , R.id.titleTv
                )

                holder.setVisibility(
                    if (TextUtils.isEmpty(leftTxt)) View.GONE else View.VISIBLE
                    , R.id.leftBtn
                )

                holder.setVisibility(
                    if (TextUtils.isEmpty(rightTxt)) View.GONE else View.VISIBLE
                    , R.id.rightBtn
                )

                if (customView != null) {
                    holder.getView<ConstraintLayout>(R.id.contentContainer)?.removeAllViews()
                    holder.getView<ConstraintLayout>(R.id.contentContainer)?.addView(customView)
                }

                holder.setText(R.id.leftBtn, leftTxt) { view, _ ->
                    QuickDialog.dismiss()
                    listener?.invoke(view, false)
                }
                    .setText(R.id.rightBtn, rightTxt) { view, _ ->
                        QuickDialog.dismiss()
                        listener?.invoke(view, true)
                    }
                    .setText(R.id.contentTv, content)
                    .setText(R.id.titleTv, title)
            }

        resetInternal()
    }

    private fun resetInternal() {
        leftTxt = ""
        rightTxt = ""
        content = ""
        customView = null
        title = ""
        isBlockBack = false
        isCancelTouchOutSide = false
    }
}