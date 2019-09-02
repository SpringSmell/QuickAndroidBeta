package org.quick.core.base.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.setCompatVectorFromResourcesEnabled
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.quick.base.Toast
import org.quick.core.R
import org.quick.core.base.application.ThemeApp
import org.quick.http.HttpService
import org.quick.utils.ViewUtils
import org.quick.viewHolder.callback.OnClickListener2
import java.io.Serializable


/**
 * 对外开放的类，请继承该类
 * 使用该类须隐藏title，主题城需使用兼容的风格，详情请查看Demo的mainifests
 * Created by chris on 2016/6/8.
 *
 * @author chris Zou
 * @date 2016/6/8.
 */
@Suppress("UNCHECKED_CAST")
abstract class ThemeActivity : AppCompatActivity() {

    var isInit = false//是否初始化

    lateinit var appRoot: View/*主布局*/
    var toolbar: Toolbar? = null/*标题栏*/
    lateinit var appContent: FrameLayout/*根布局，内容*/

    private var onMenuItemClickListener: ((menu: MenuItem?) -> Boolean)? = null
    private var resMenu = -1
    private var isDefaultToolbar = false

    val activity: Activity get() = this
    /**
     * 是否引用基本布局
     *
     * @return
     */
    open val isUsingBaseLayout get() = true
    /**
     * 是否显示标题
     */
    open val isShowTitle get() = true

    /**
     * 返回资源文件ID
     *
     * @return
     */
    @LayoutRes
    protected abstract fun onResultLayoutResId(): Int


    /**
     * 初始化操作
     */
    abstract fun onInit()

    /**
     * 初始化布局
     */
    abstract fun onInitLayout()

    /**
     * 绑定监听
     */
    abstract fun onBindListener()

    /**
     * 绑定数据
     */
    abstract fun onAction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeApp.instance.addActivity(this)
        initView()
        onInit()
        onInitLayout()
        onBindListener()
        onAction()
        isInit = true
    }

    private fun initView() {
        if (isUsingBaseLayout) {
            setContentView(R.layout.app_content)
            appContent = findViewById(R.id.appContent)
            appRoot = findViewById(R.id.root)
//            if (onResultLayoutResId() == -1)
//                appContent.addView(onResultView())
//            else
                LayoutInflater.from(this).inflate(onResultLayoutResId(), appContent)
        } else {
            appRoot = LayoutInflater.from(this).inflate(onResultLayoutResId(), null)
            setContentView(appRoot)
        }

        setupTitle()

        if (isUsingBaseLayout && isShowTitle) {
            back()
        }
    }

    private fun setupTitle() {
        toolbar = onResultToolbar()
        if (toolbar != null) {
            toolbar?.visibility = if (isShowTitle) View.VISIBLE else View.GONE

            if (isShowTitle) {//有标题
                setSupportActionBar(toolbar)
                title = getParams(TITLE, "QuickAndroid")
                toolbar?.fitsSystemWindows = isShowTitle
                val actionBar = supportActionBar
                actionBar?.setDisplayShowHomeEnabled(true)
            }

            if (!isDefaultToolbar && isUsingBaseLayout) {//不是默认的布局并且引用父布局
                val viewGroup = appRoot as ViewGroup
                for (i in 0 until viewGroup.childCount) {
                    if (viewGroup.getChildAt(i) is Toolbar) {
                        viewGroup.removeViewAt(i)
                        break
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (onMenuItemClickListener != null)
            onMenuItemClickListener!!.invoke(item)
        else
            super.onOptionsItemSelected(item)
    }


    open fun onResultToolbar(): Toolbar? {
        isDefaultToolbar = true
        if (isUsingBaseLayout && toolbar == null)
            toolbar = findViewById<View>(R.id.appToolbar) as Toolbar
        return toolbar
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (resMenu != -1) menuInflater.inflate(resMenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun menu(@MenuRes resMenu: Int, onMenuItemClickListener: ((menu: MenuItem?) -> Boolean)) {
        this.resMenu = resMenu
        this.onMenuItemClickListener = onMenuItemClickListener
    }

    fun menu(
        @LayoutRes resId: Int, onClickListener: View.OnClickListener? = null,
        vararg ids: Int
    ): View {
        val view = LayoutInflater.from(this).inflate(resId, null)
        val layoutParams =
            Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        layoutParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL

        for (id in ids) {
            getView<View>(id, view).setOnClickListener(onClickListener)
        }
        toolbar!!.addView(view, layoutParams)
        return view
    }

    fun backInvalid() {
        back(-1, false, null)
    }

    fun back(onClickListener: View.OnClickListener) {
        back(-1, true, onClickListener)
    }

    fun back(backIcon: Int = -1, onClickListener: View.OnClickListener? = null) {
        back(backIcon, true, onClickListener)
    }

    /**
     * @param backIcon        -1:默认按钮   其他为自定义按钮
     * @param isValid         单击按钮是否有效
     * @param onClickListener
     */
    private fun back(
        backIcon: Int = -1,
        isValid: Boolean = true,
        onClickListener: View.OnClickListener? = null
    ) {
        if (isValid) {
            if (backIcon == -1)
                toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            else
                toolbar?.setNavigationIcon(backIcon)

            toolbar?.setNavigationOnClickListener(
                onClickListener ?: View.OnClickListener { onBackPressed() })
        } else toolbar?.navigationIcon = null
    }

    /**
     * 获取View
     *
     * @param resId
     * @param <T>
     * @return
    </T> */
    fun <T : View> getView(@IdRes resId: Int): T = appRoot.findViewById(resId)

    /**
     * 获取View
     *
     * @param resId
     * @param <T>
     * @return
    </T> */
    fun <T : View> getView(@IdRes resId: Int, view: View): T = view.findViewById(resId)

    fun onClick(onClickListener: (view: View) -> Unit, @Size(min = 1) @IdRes vararg resIds: Int) {
        for (resId in resIds) {
            onClick(onClickListener, getView<View>(resId))
        }
    }


    fun onClick(onClickListener: (view: View) -> Unit, @Size(min = 1) vararg views: View) {
        for (view in views)
            view.setOnClickListener(object : OnClickListener2() {
                override fun click(view: View) {
                    onClickListener.invoke(view)
                }
            })
    }

    fun setVisibility(visibility: Int, @Size(min = 1) vararg resIds: Int) {
        for (resId in resIds) setVisibility(visibility, getView<View>(resId))
    }

    fun setVisibility(visibility: Int, @Size(min = 1) vararg views: View) {
        for (view in views) setVisibility(visibility, view)
    }

    fun setVisibility(visibility: Int, view: View) {
        view.visibility = visibility
    }

    /**
     * 获取常规类型数值
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
    </T> */
    fun <T> getParams(key: String, defaultValue: T): T {
        return ViewUtils.getIntentValue(intent, key, defaultValue)!!
    }

    /**
     * 获取常规类型数值
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
    </T> */
    fun <T> getParams(intent: Intent, key: String, defaultValue: T): T {
        return ViewUtils.getIntentValue(intent, key, defaultValue)!!
    }

    fun <T : Serializable> getSerializable(key: String): T {
        return intent.getSerializableExtra(key) as T
    }

    fun <T : Parcelable> getParcelable(key: String): T {
        return intent.getParcelableExtra(key)
    }

    fun toast(msg: CharSequence) {
        Toast.show(msg)
    }

    fun toast(@StringRes msg: Int) {
        Toast.show(msg)
    }

    fun toastLong(msg: CharSequence) {
        Toast.showLong(msg)
    }

    fun toastLong(@StringRes msg: Int) {
        Toast.showLong(msg)
    }

    protected fun snackbar(msg: CharSequence, onClickListener: View.OnClickListener) {
        snackbar(msg, getString(R.string.sure), onClickListener)
    }

    @JvmOverloads
    protected fun snackbar(
        msg: CharSequence,
        actionTxt: CharSequence? = null,
        onClickListener: View.OnClickListener? = null
    ) {
        snackbar(null, msg, actionTxt, onClickListener)
    }

    @JvmOverloads
    protected fun snackbar(
        view: View?,
        msg: CharSequence,
        actionTxt: CharSequence? = null,
        onClickListener: View.OnClickListener? = null
    ) {
        Snackbar.make(view ?: window.decorView, msg, Snackbar.LENGTH_SHORT)
            .setAction(actionTxt, onClickListener)
            .setActionTextColor(ContextCompat.getColor(activity, R.color.blueShallow))
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.e(
            "ThemeActivity",
            String.format("----------onResume--------------%s", javaClass.simpleName)
        )
    }

    override fun onDestroy() {
        ThemeApp.instance.removeActivity(this)
        HttpService.cancelTask(this)
        super.onDestroy()
        Log.e(
            "ThemeActivity",
            String.format("----------onDestroy--------------%s", javaClass.simpleName)
        )
    }

    companion object {
        init {//兼容vector
            setCompatVectorFromResourcesEnabled(true)
        }

        const val TITLE = "title"
        const val ID = "id"
        const val DATA = "dataList"
        const val TYPE = "type"
    }
}
