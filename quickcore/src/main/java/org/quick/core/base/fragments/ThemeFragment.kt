package org.quick.core.base.fragments

import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.*
import org.quick.base.Toast
import org.quick.core.R
import org.quick.utils.ViewUtils
import org.quick.viewHolder.callback.OnClickListener2


/**
 * 请填写方法内容
 *
 * @author Chris zou
 * @Date 16/10/11
 * @modifyInfo1 chriszou-16/10/11
 * @modifyContent
 */
abstract class ThemeFragment : androidx.fragment.app.Fragment() {

    var appRoot: View? = null/*根布局，内容*/
    var isInit: Boolean = false//是否初始化

    lateinit var appContent: FrameLayout
    var toolbar: Toolbar? = null
    private var isDefaultToolbar = false
    private var onMenuItemClickListener: ((menu: MenuItem?) -> Boolean)? = null
    private var resMenu = -1

    /**
     * 是否引用基本布局
     *
     * @return
     */
    open val isUsingBaseLayout get() = true
    open val isShowTitle get() = false
    open val isFitsSystemWindows get() = true

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
    abstract fun start()

    open fun onResultToolbar(): Toolbar? {
        isDefaultToolbar = true
        if (isUsingBaseLayout && toolbar == null)
            toolbar = getView(R.id.appToolbar) as Toolbar
        return toolbar
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (appRoot == null) {
            if (isUsingBaseLayout) {
                appRoot = inflater.inflate(R.layout.app_content, container, false)
                appContent = appRoot?.findViewById(R.id.appContent)!!
                inflater.inflate(onResultLayoutResId(), appContent)
            } else appRoot =
                inflater.inflate(onResultLayoutResId(), container, false)
            setHasOptionsMenu(true)//允许包含菜单
            setupTitle()
        } else {
            if (appRoot!!.parent != null) {
                val parent = appRoot!!.parent as ViewGroup
                parent.removeView(appRoot)
            }
        }

        onInit()
        onInitLayout()
        onBindListener()
        start()
        return appRoot
    }

    private fun setupTitle() {
        toolbar = onResultToolbar()
        if (toolbar != null) {
            toolbar?.visibility = if (isShowTitle) View.VISIBLE else View.GONE
            toolbar?.fitsSystemWindows = isShowTitle

            if (isFitsSystemWindows) {
//                toolbar?.setPadding(0, CommonUtils.getStatusHeight(activity), 0, 0)
//                toolbar?.layoutParams?.height = (CommonUtils.getSystemAttrValue(activity, R.attr.actionBarSize) + CommonUtils.getStatusHeight(activity)).toInt()
                ViewUtils.setupFitsSystemWindowsFromToolbar(activity!!, toolbar!!)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (resMenu != -1)
            inflater.inflate(resMenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (onMenuItemClickListener != null) onMenuItemClickListener!!.invoke(item) else super.onOptionsItemSelected(
            item
        )
    }

    fun setTitle(title: String) {
        toolbar?.title = title
    }

    fun menu(@MenuRes resMenu: Int, onMenuItemClickListener: ((menu: MenuItem?) -> Boolean)) {
        this.resMenu = resMenu
        this.onMenuItemClickListener = onMenuItemClickListener
    }

    fun menu(
        @LayoutRes resId: Int, onClickListener: View.OnClickListener? = null,
        vararg ids: Int
    ): View {
        val view = LayoutInflater.from(activity).inflate(resId, null)
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
                onClickListener ?: View.OnClickListener { activity?.finish() })
        } else toolbar?.navigationIcon = null
    }


    /**
     * 获取View
     *
     * @param resId
     * @param <T>
     * @return
    </T> */
    fun <T : View> getView(@IdRes resId: Int): T = getView(resId, appRoot!!)

    /**
     * 获取View
     *
     * @param parent
     * @param resId
     * @param <T>
     * @return
    </T> */
    fun <T : View> getView(@IdRes resId: Int, parent: View): T = parent.findViewById(resId)

    fun setVisibility(visibility: Int, @Size(min = 1) vararg resIds: Int) {
        for (resId in resIds)
            setVisibility(visibility, getView<View>(resId))
    }

    fun setVisibility(visibility: Int, @Size(min = 1) vararg views: View) {
        for (view in views)
            setVisibility(visibility, view)
    }

    fun setVisibility(visibility: Int, view: View) {
        view.visibility = visibility
    }

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

    /**
     * 获取常规类型数值
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
    </T> */
    fun <T> getValue(key: String, defaultValue: T): T? {
        return ViewUtils.getIntentValue(activity!!.intent, key, defaultValue)
    }

    fun <T : Parcelable> getParcelable(key: String): T {
        return activity!!.intent.getParcelableExtra(key)
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
        Snackbar.make(view ?: appRoot!!, msg, Snackbar.LENGTH_SHORT)
            .setAction(actionTxt, onClickListener)
            .setActionTextColor(ContextCompat.getColor(activity!!, R.color.blueShallow))
            .show()
    }
}
