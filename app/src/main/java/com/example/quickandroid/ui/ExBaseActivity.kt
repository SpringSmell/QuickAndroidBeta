package com.example.quickandroid.ui

import android.view.View
import com.example.quickandroid.R
import com.example.quickandroid.model.KotlinModel
import org.quick.async.Async
import org.quick.core.base.BaseActivity

/**
 * BaseActivity示例
 */
class ExBaseActivity : BaseActivity() {

    override fun onResultLayoutResId(): Int = R.layout.activity_ex_baseactivity

    /*  true: 添加进基础布局，基础布局包含Toolbar标题
        false:不使用基础布局
     */
    override val isUsingBaseLayout: Boolean
        get() = true

    override val isShowTitle: Boolean
        get() = true/*使用基础布局的情况下显示标题*/

    override fun onInit() {
//        var dataList = getParams(DATA, KotlinModel())
        var data=getParcelable<KotlinModel>(DATA)
    }

    override fun onInitLayout() {
//        back(View.OnClickListener {
//            do something
//        })
//        backInvalid()/*屏蔽返回按钮*/
    }

    override fun onBindListener() {
        onClick({

        }, R.id.titleTv)
        /*设置菜单*/
        menu(R.menu.about) { menu ->
            when (menu!!.itemId) {
                R.id.about -> {
                    toast("这是关于")
                }
                R.id.loading -> {
                    loadingDialog.show()
                    Async.delay({
                        loadingDialog.dismiss()
                    }, 2000)
                }
                R.id.help -> {
                    toast("这是帮助")
                }
                R.id.cancel -> {
                    isOkDialog.default("是否取消").show { _, isRight ->
                        toast(if (isRight) "确定取消" else "取消")
                    }
                }
            }
            true
        }
        menu(R.layout.btn_example_baseactivity)
        /*更灵活，也可以设置右View设置*/
        menu(R.layout.btn_example_baseactivity, View.OnClickListener {
            when (it.id) {
                R.id.btn1 -> {
                    snackbar("这是Btn1", View.OnClickListener {

                    })
                }
                R.id.btn2 -> {
                    snackbar("这是Btn2", "确定", View.OnClickListener {

                    })
                }
            }
        }, R.id.btn1, R.id.btn2)
    }

    override fun onAction() {

    }
}