package com.example.quickandroid.ui

import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_quick_dialog.*
import org.quick.core.base.BaseActivity
import org.quick.dialog.QuickDialog

/**
 * 弹框示例
 */
class ExQuickDialogActivity :BaseActivity(){
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_quick_dialog

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        normalTv.setOnClickListener {
            QuickDialog.Builder(this@ExQuickDialogActivity, R.layout.dialog_normal, R.style.appTheme_Dialog)
                .animStyle(R.style.dialogAnimBottom2Up)
                .blockBackKey(true)/*阻塞返回键*/
                .canceledOnTouchOutside(false)
                .gravity(Gravity.BOTTOM)
                .size(WindowManager.LayoutParams.MATCH_PARENT, (1920 * 0.4).toInt())
                .onInit { dialog, holder ->
                    /*初始化，只调用一次*/
                    holder.setOnClick({ view, vh ->
                        QuickDialog.dismiss()
                    }, R.id.closeActionTv)
                }
                .onDismiss { dialog, iDialog, holder ->
                    Log.e("QuickDialog", "弹窗消失")
                }
                .show { dialog, holder ->
                    holder.itemView
                }
        }
    }

    override fun onAction() {

    }
}