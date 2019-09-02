package org.quick.core.common

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import org.quick.core.R
import org.quick.core.widgets.RollView
import org.quick.dialog.QuickDialog

object SelectorDataDialog {

    /**
     * 选择数据
     */
    fun show(context: Context, title: String, listener: (item: String) -> Unit, vararg datas: String) {
        QuickDialog.Builder(context, R.layout.app_dialog_selector_data)
            .canceledOnTouchOutside(true)
            .size(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            .gravity(Gravity.BOTTOM)
            .animStyle(R.style.dialogAnimBottom2Up)
            .onInit { _, holder ->
                holder.getView<RollView>(R.id.contentRv)?.setData(datas.toMutableList())
                holder.setText(R.id.titleTv, title)
                holder.setOnClick({ view, _ ->
                    QuickDialog.dismiss()
                    if (view.id == R.id.confirmTv)
                        listener.invoke(holder.getView<RollView>(R.id.contentRv)!!.selected)
                }, R.id.cancelTv, R.id.confirmTv)
            }
            .show()
    }
}