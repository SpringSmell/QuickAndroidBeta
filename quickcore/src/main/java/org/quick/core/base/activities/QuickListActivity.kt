package org.quick.core.base.activities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.Size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajguan.library.EasyRefreshLayout
import com.ajguan.library.LoadModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.app_base_list.*
import kotlinx.android.synthetic.main.app_include_no_msg.*
import org.quick.core.R
import org.quick.core.base.BaseAdapter
import org.quick.core.config.Constant
import org.quick.core.mvp.BaseModel
import org.quick.http.HttpService
import org.quick.http.JsonUtils
import org.quick.http.callback.Callback
import org.quick.http.callback.ClassCallback
import org.quick.utils.check.CheckUtils
import org.quick.viewHolder.callback.OnClickListener2
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by work on 2017/8/10.
 *
 * @author chris zou
 * @mail chrisSpringSmell@gmail.com
 *
 * @param M 数据模型，使用GSON解析JSON
 * @param MC 实际数据列表item
 */

@Suppress("UNCHECKED_CAST")
abstract class QuickListActivity<M, MC> : ThemeActivity(), EasyRefreshLayout.EasyEvent {
    enum class ErrorType(var value: Int = 0) {
        /**
         * 没有数据
         */
        NO_MSG(-0x1),
        /**
         * 网络问题
         */
        NET_WORK(-0x2),
        /**
         * 服务器问题
         */
        SERVICE(-0x3),
        /**
         * 未登录
         */
        NO_LOGIN(-0x4),
        /**
         * 数据异常
         */
        DATA(-0x5),
        /**
         * 其他问题-自定义
         */
        OTHER(-0x3),
        NORMAL(0x1);
    }

    companion object {
        /**
         * 分页关键字
         */
        const val PAGER_NUMBER_KEY = "page"
        const val PAGER_COUNT_KEY = "num"
        const val PAGER_FIRST_NUMBER = 1
        const val PAGER_COUNT = 10
    }

    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null
    private var isDefaultNoMsgLayout = true
    private var tabs: Array<String>? = null
    private val adapter = Adapter()
    private val params = HashMap<String, String>()
    private var onRequestListener: OnRequestListener? = null

    var pageNumber = 1
    var errorType = ErrorType.NORMAL

    open val noMsgLayoutRes: Int
        @LayoutRes
        get() = R.layout.app_include_no_msg

    open val noMsgLayout: View
        get() {
            isDefaultNoMsgLayout = true
            return LayoutInflater.from(activity).inflate(noMsgLayoutRes, null)
        }

    private val isPullRefresh: Boolean
        get() = pageNumber <= 2

    abstract val isPullRefreshEnable: Boolean

    abstract val isLoadMoreEnable: Boolean

    override fun onInit() {

    }

    override fun onInitLayout() {
        recyclerView.layoutManager = onResultLayoutManager()
        recyclerView.adapter = onResultAdapter()
        if (isLoadMoreEnable) refreshLayout.loadMoreModel = LoadModel.COMMON_MODEL
        refreshLayout.isEnablePullToRefresh = isPullRefreshEnable
    }

    override fun onBindListener() {
        if (isDefaultNoMsgLayout)
            refreshBtn.setOnClickListener(object : OnClickListener2() {
                override fun click(view: View) {
                    onRefreshClick(errorType)
                }
            })
    }

    override fun onResultLayoutResId(): Int = R.layout.app_base_list

    fun requestListener(onRequestListener: OnRequestListener) {
        this.onRequestListener = onRequestListener
    }

    fun setupTab(@Size(min = 1) vararg tabs: String) {
        setupTab(null, *tabs)
    }

    fun setupTab(onTabSelectedListener: TabLayout.OnTabSelectedListener?, @Size(min = 1) vararg tabs: String) {
        setupTab(onTabSelectedListener, -1, *tabs)
    }

    /**
     * 安装顶部TabLayout
     *
     * @param tabs
     */
    fun setupTab(
        onTabSelectedListener: TabLayout.OnTabSelectedListener?,
        selectorPosition: Int, @Size(min = 1) vararg tabs: String
    ): TabLayout {
        @Suppress("UNCHECKED_CAST")
        this.tabs = tabs as Array<String>
        tabLayout.visibility = View.VISIBLE
        if (this.onTabSelectedListener != null) tabLayout.removeOnTabSelectedListener(this.onTabSelectedListener!!)
        if (onTabSelectedListener != null) {
            this.onTabSelectedListener = onTabSelectedListener
            tabLayout.addOnTabSelectedListener(this.onTabSelectedListener!!)
        }

        if (tabLayout.tabCount > 0) {
            var i = 0
            while (i < tabLayout.tabCount && i < tabs.size) {
                tabLayout.getTabAt(i)!!.text = tabs[i]
                i++
            }
        } else tabs.forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it))
        }
        if (selectorPosition > 0 && selectorPosition < tabs.size)
            tabLayout.getTabAt(selectorPosition)?.select()
        return tabLayout
    }

    fun setupDividerLine(drawable: Drawable, padding: Float = -1f, defaultSize: Int = 1) {
        recyclerView.addItemDecoration(DividerItemDecoration(drawable, padding, defaultSize))

    }

    fun setupDividerLine(color: Int, padding: Float = -1f, defaultSize: Int = 1) {
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                ColorDrawable(color),
                padding,
                defaultSize
            )
        )
    }

    fun refreshComplete() {
        refreshLayout.refreshComplete()
    }

    fun loadMoreComplete() {
        refreshLayout.loadMoreComplete()
    }

    fun addFooter(view: View) {
        root.addView(view)
    }

    fun addHeader(@Size(min = 1) vararg views: View) {
        getAdapter().addHeader(*views)
    }

    fun addFooter(@Size(min = 1) vararg views: View) {
        getAdapter().addFooter(*views)
    }

    fun action() {
        refreshLayout.autoRefresh(0)
    }

    override fun onRefreshing() {
        if (!CheckUtils.isNetWorkAvailable(this))
            dataHas(false, ErrorType.NET_WORK)
        else if (!TextUtils.isEmpty(onResultUrl())) {
            pageNumber = PAGER_FIRST_NUMBER
            requestData()
        }
    }

    override fun onLoadMore() {
        if (!TextUtils.isEmpty(onResultUrl()))
            requestData()
    }

    fun requestData() {
        params.clear()
        onResultParams(params)
        if (!params.containsKey(QuickListActivity.PAGER_NUMBER_KEY))
            params[QuickListActivity.PAGER_NUMBER_KEY] = pageNumber.toString()
        if (!params.containsKey(QuickListActivity.PAGER_COUNT_KEY))
            params[QuickListActivity.PAGER_COUNT_KEY] = QuickListActivity.PAGER_COUNT.toString()

        HttpService.Builder(onResultUrl()).addParams(params).post()
            .enqueue(object : Callback<String>() {

                override fun onFailure(e: Throwable, isNetworkError: Boolean) {
                    dataHas(false, ErrorType.SERVICE)
                    onRequestListener?.onError("", isPullRefresh, errorType)
                }

                override fun onResponse(value: String?) {
                    checkData(value)
                    pageNumber++
                }

                override fun onEnd() {
                    if (isPullRefresh && isPullRefreshEnable)
                        refreshLayout.refreshComplete()
                    else if (isLoadMoreEnable)
                        refreshLayout.loadMoreComplete()
                    onRequestListener?.onEnd()
                }
            })
    }

    /**
     * 消息统一处理
     */
    private fun checkData(value: String?) {
        val model = JsonUtils.parseFromJson(value, BaseModel::class.java)
        if (model != null) {
            when (model.code) {
                /*成功*/
                Constant.APP_SUCCESS_TAG -> {
                    val realModel =
                        JsonUtils.parseFromJson(
                            value,
                            ClassCallback.getTClass(this@QuickListActivity::class.java)
                        ) as M
                    if (realModel != null) {
//                        recyclerView.isNoMore = false
                        dataHas(true)
                        if (isPullRefresh) onRefreshSuc(realModel) else onLoadMoreSuc(
                            realModel
                        )
                    } else {
                        if (isPullRefresh)
                            dataHas(false, ErrorType.DATA)
                        else
                            toast(getString(R.string.errorDataHint))
                        onRequestListener?.onError(value!!, isPullRefresh, errorType)
                    }
                }
                /*没有消息*/
                Constant.APP_ERROR_MSG_N0 -> {
                    dataHas(false, ErrorType.NO_MSG)
                    toast(model.msg)
                    onRequestListener?.onError(value!!, isPullRefresh, errorType)
                }
                /*没有更多消息*/
                Constant.APP_ERROR_MSG_N0_MORE -> {
//                    recyclerView.isNoMore = true
                    toast("没有更多消息啦")
                    onRequestListener?.onError(value!!, isPullRefresh, errorType)
                }
                /*未登录*/
                Constant.APP_ERROR_NO_LOGIN -> {
                    dataHas(false, ErrorType.NO_LOGIN)
                    onRequestListener?.onError(value!!, isPullRefresh, errorType)
                }
                /*其他异常*/
                else -> {
                    dataHas(false, ErrorType.SERVICE)
                    onRequestListener?.onError(value!!, isPullRefresh, errorType)
                }
            }
        } else {
            dataHas(false, ErrorType.DATA)
            onRequestListener?.onError(value!!, isPullRefresh, errorType)
        }
    }

    fun dataNoMore(isNoMore: Boolean) {
//        recyclerView.isNoMore = isNoMore
    }

    /**
     * 设置是否有数据
     *
     * @param isHasData
     */
    fun dataHas(isHasData: Boolean) {
        dataHas(isHasData, ErrorType.NO_MSG)
    }

    /**
     * 设置是否有数据
     *
     * @param isHasData
     */
    @Synchronized
    fun dataHas(isHasData: Boolean, type: ErrorType) {
        if (isHasData) {//有
            errorType = ErrorType.NORMAL
            if (noMsgContainer.visibility == View.VISIBLE) noMsgContainer.visibility = View.GONE
            if (recyclerView.visibility == View.GONE) recyclerView.visibility = View.VISIBLE
        } else {//无
            if (noMsgContainer.visibility == View.GONE) noMsgContainer.visibility = View.VISIBLE
            if (recyclerView.visibility == View.VISIBLE) recyclerView.visibility = View.GONE
            setHintErrorStyle(type)
        }
    }

    private fun setHintErrorStyle(type: ErrorType) {
        this.errorType = type
        if (isDefaultNoMsgLayout) {
            when (type) {
                ErrorType.NO_MSG -> {
                    hintErrorIv.setImageResource(onResultErrorNoMsgIcon())
                    hintErrorTv.text = onResultErrorNoMsgTxt()
                    refreshBtn.visibility = View.GONE
                    refreshBtn.text = onResultErrorBtnTxt()
                }
                ErrorType.NET_WORK -> {
                    hintErrorIv.setImageResource(onResultErrorNetWorkIcon())
                    hintErrorTv.text = onResultErrorNetWorkTxt()
                    refreshBtn.visibility = View.VISIBLE
                    refreshBtn.text = onResultErrorBtnTxt()
                }
                ErrorType.SERVICE -> {
                    hintErrorIv.setImageResource(onResultErrorServiceIcon())
                    hintErrorTv.text = onResultErrorServiceTxt()
                    refreshBtn.visibility = View.VISIBLE
                    refreshBtn.text = onResultErrorBtnTxt()
                }
                ErrorType.OTHER -> {
                    hintErrorIv.setImageResource(onResultErrorOtherIcon())
                    hintErrorTv.text = onResultErrorOtherTxt()
                    refreshBtn.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    /**
     * 刷新按钮的点击事件
     */
    open fun onRefreshClick(errorType: ErrorType) {
        refreshLayout.autoRefresh(0)
    }


    fun getAdapter(): Adapter = adapter

    fun setDataList(dataList: MutableList<MC>) {
        getAdapter().dataList(dataList)
    }

    fun addData(dataList: MutableList<MC>) {
        getAdapter().add(dataList)
    }

    fun addData(m: MC) {
        getAdapter().add(m)
    }

    fun dataList(): MutableList<MC> = getAdapter().dataList()

    fun getCount() = dataList().size

    fun getItem(position: Int): MC = getAdapter().getItem(position)

    fun remove(position: Int) {
        getAdapter().remove(position)
    }

    fun remove(m: MC) {
        getAdapter().remove(m)
    }

    fun removeAll() {
        getAdapter().removeAll()
    }

    fun onClick(
        onClickListener: ((view: View, viewHolder: BaseAdapter.BaseViewHolder, position: Int, itemData: MC) -> Unit),
        @Size(min = 1) vararg resId: Int
    ) {
        getAdapter().onClick(onClickListener, *resId)
    }

    fun onCheckedChanged(
        onCheckedChangedListener: ((view: View, viewHolder: BaseAdapter.BaseViewHolder, isChecked: Boolean, position: Int, itemData: MC) -> Unit), @Size(
            min = 1
        ) vararg resId: Int
    ) {
        getAdapter().onCheckedChanged(onCheckedChangedListener, *resId)
    }

    fun onItemClick(onItemClickListener: ((view: View, viewHolder: BaseAdapter.BaseViewHolder, position: Int, itemData: MC) -> Unit)) {
        getAdapter().onItemClick(onItemClickListener)
    }

    fun onItemLongClick(onItemLongClickListener: ((view: View, viewHolder: BaseAdapter.BaseViewHolder, position: Int, itemData: MC) -> Boolean)) {
        getAdapter().onItemLongClick(onItemLongClickListener)
    }

    /**
     * 返回网络错误图片
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorNetWorkIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorNetWorkTxt(): String = getString(R.string.errorNetWorkHint)

    /**
     * 返回没有数据的图片
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorNoMsgIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorNoMsgTxt(): String = getString(R.string.errorNoMsgHint)

    /**
     * 返回服务器错误的图片
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorServiceIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorServiceTxt(): String = getString(R.string.errorServiceHint)

    /**
     * 返回服务器错误的图片
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorOtherIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorOtherTxt(): String = getString(R.string.errorOtherHint)

    /**
     * 未登录
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorNoLoginIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorNoLoginTxt(): String = getString(R.string.errorNoLoginHint)

    /**
     * 数据异常
     *
     * @return
     */
    @DrawableRes
    open fun onResultErrorDataIcon(): Int = R.drawable.ic_broken_image_gray_24dp

    open fun onResultErrorDataTxt(): String = getString(R.string.errorDataHint)
    /**
     * 网络出错时的文字
     *
     * @return
     */
    open fun onResultErrorBtnTxt(): String = getString(R.string.refresh)

    open fun onResultLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(activity)

    open fun onResultRefreshColors(): IntArray = intArrayOf(Color.BLACK)

    open fun onResultAdapter(): BaseAdapter<*> = adapter

    abstract fun onResultUrl(): String

    abstract fun onResultParams(params: MutableMap<String, String>)

    /**
     * 数据请求成功
     *
     * @param model 数据model
     * @return
     */
    abstract fun onRefreshSuc(model: M)

    /**
     * 数据请求成功
     *
     * @param model 数据model
     * @return
     */
    abstract fun onLoadMoreSuc(model: M)

    abstract fun onResultItemResId(viewType: Int): Int

    abstract fun onBindData(
        holder: BaseAdapter.BaseViewHolder,
        position: Int,
        itemData: MC,
        viewType: Int
    )

    open fun getItemViewType(position: Int): Int = -1

    open fun onResultItemMargin(position: Int): Float = 0f

    open fun onResultItemMarginLeft(position: Int): Float {
        return if (!getAdapter().isVertically()) {
            when (position) {
                0 -> {
                    onResultItemMargin(position)
                }
                else -> onResultItemMargin(position) / 2
            }
        } else onResultItemMargin(position)
    }

    open fun onResultItemMarginRight(position: Int): Float {
        return if (!getAdapter().isVertically()) {
            when (position) {
                getAdapter().itemCount - 1 -> {
                    onResultItemMargin(position)
                }
                else -> onResultItemMargin(position) / 2
            }
        } else onResultItemMargin(position)
    }

    open fun onResultItemMarginTop(position: Int): Float {
        return if (getAdapter().isVertically()) {
            when (position) {
                0 -> {
                    onResultItemMargin(position)
                }
                else -> onResultItemMargin(position) / 2
            }
        } else onResultItemMargin(position)
    }

    open fun onResultItemMarginBottom(position: Int): Float {
        return if (getAdapter().isVertically()) {
            when (position) {
                getAdapter().itemCount - 1 -> {
                    onResultItemMargin(position)
                }
                else -> onResultItemMargin(position) / 2
            }
        } else onResultItemMargin(position)
    }

    open fun onResultItemPadding(position: Int): Float = 0f

    open fun onResultItemPaddingLeft(position: Int): Float {
        return if (!getAdapter().isVertically()) {
            when (position) {
                0 -> {
                    onResultItemPadding(position)
                }
                else -> onResultItemPadding(position) / 2
            }
        } else onResultItemPadding(position)
    }

    open fun onResultItemPaddingRight(position: Int): Float {
        return if (!getAdapter().isVertically()) {
            when (position) {
                getAdapter().itemCount - 1 -> {
                    onResultItemPadding(position)
                }
                else -> onResultItemPadding(position) / 2
            }
        } else onResultItemPadding(position)
    }

    open fun onResultItemPaddingTop(position: Int): Float {
        return if (getAdapter().isVertically()) {
            when (position) {
                0 -> {
                    onResultItemPadding(position)
                }
                else -> onResultItemPadding(position) / 2
            }
        } else onResultItemPadding(position)
    }

    open fun onResultItemPaddingBottom(position: Int): Float {
        return if (getAdapter().isVertically()) {
            when (position) {
                getAdapter().itemCount - 1 -> {
                    onResultItemPadding(position)
                }
                else -> onResultItemPadding(position) / 2
            }
        } else onResultItemPadding(position)
    }

    inner class Adapter : BaseAdapter<MC>() {
        override fun onResultLayoutResId(viewType: Int): Int =
            this@QuickListActivity.onResultItemResId(viewType)

        override fun onBindData(
            holder: BaseViewHolder,
            position: Int,
            itemData: MC,
            viewType: Int
        ) {
            this@QuickListActivity.onBindData(holder, position, itemData, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return this@QuickListActivity.getItemViewType(super.getItemViewType(position))
        }

        override fun onResultMargin(position: Int): Float =
            this@QuickListActivity.onResultItemMargin(position)

        override fun onResultMarginLeft(position: Int): Float =
            this@QuickListActivity.onResultItemMarginLeft(position)

        override fun onResultMarginRight(position: Int): Float {
            return this@QuickListActivity.onResultItemMarginRight(position)
        }

        override fun onResultMarginTop(position: Int): Float {
            return this@QuickListActivity.onResultItemMarginTop(position)
        }

        override fun onResultMarginBottom(position: Int): Float {
            return this@QuickListActivity.onResultItemMarginBottom(position)
        }

        override fun onResultPadding(position: Int): Float {
            return this@QuickListActivity.onResultItemPadding(position)
        }

        override fun onResultPaddingLeft(position: Int): Float {
            return this@QuickListActivity.onResultItemPaddingLeft(position)
        }

        override fun onResultPaddingRight(position: Int): Float {
            return this@QuickListActivity.onResultItemPaddingRight(position)
        }

        override fun onResultPaddingTop(position: Int): Float {
            return this@QuickListActivity.onResultItemPaddingTop(position)
        }

        override fun onResultPaddingBottom(position: Int): Float {
            return this@QuickListActivity.onResultItemPaddingBottom(position)
        }
    }

    /**
     * @param drawable 分割线样式
     * @param padding 左右或者上下边距，默认分割线与item同等宽度或者高度
     */
    class DividerItemDecoration(
        var drawable: Drawable,
        var padding: Float = -1f,
        var defaultSize: Int = 1
    ) :
        RecyclerView.ItemDecoration() {

        /**
         * 获取分割线的尺寸，也就是每个Item偏移多少
         */
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.layoutManager!!.canScrollVertically()) {
//                    when{
//                        parent.layoutManager is GridLayoutManager->(parent.layoutManager as StaggeredGridLayoutManager).spanCount
//                    }
                outRect.set(
                    0,
                    0,
                    0,
                    if (drawable.intrinsicHeight != -1) drawable.intrinsicHeight else defaultSize
                )
            } else outRect.set(
                0,
                0,
                if (drawable.intrinsicWidth != -1) drawable.intrinsicWidth else defaultSize,
                0
            )
        }

        override fun onDraw(
            c: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.layoutManager!!.canScrollVertically()) drawableVertically(c, parent)
            else drawableHorizontally(c, parent)
        }

        /**
         * 绘制垂直的
         */
        private fun drawableVertically(c: Canvas, parent: RecyclerView) {
            c.save()
            for (index in 0 until parent.childCount) {
                val realIndex = parent.getChildAdapterPosition(parent.getChildAt(index))
                if (realIndex in if (isReverseLayout(parent.layoutManager!!)) 1 until parent.adapter!!.itemCount else 0 until parent.adapter!!.itemCount - 1) {
                    val bound = Rect()
                    parent.getDecoratedBoundsWithMargins(parent.getChildAt(index), bound)
                    val left: Int =
                        if (padding == -1f) parent.getChildAt(index).left else (parent.left + padding).roundToInt()
                    val right: Int =
                        if (padding == -1f) parent.getChildAt(index).right else (parent.right - padding).roundToInt()
                    val top =
                        bound.bottom - if (drawable.intrinsicHeight != -1) drawable.intrinsicHeight else defaultSize
                    val bottom = bound.bottom
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(c)
                }
            }
            c.restore()
        }

        /**
         * 绘制水平的
         */
        private fun drawableHorizontally(c: Canvas, parent: RecyclerView) {
            c.save()
            for (index in 0 until parent.childCount) {
                val realIndex = parent.getChildAdapterPosition(parent.getChildAt(index))
                if (realIndex in if (isReverseLayout(parent.layoutManager!!)) 1 until parent.adapter!!.itemCount else 0 until parent.adapter!!.itemCount - 1) {
                    val bound = Rect()
                    parent.getDecoratedBoundsWithMargins(parent.getChildAt(index), bound)
                    val left: Int =
                        bound.right - if (drawable.intrinsicWidth != -1) drawable.intrinsicWidth else defaultSize
                    val right: Int = bound.right
                    val top: Int =
                        if (padding == -1f) parent.getChildAt(index).top else (parent.top + padding).roundToInt()
                    val bottom: Int =
                        if (padding == -1f) parent.getChildAt(index).bottom else (parent.bottom - padding).roundToInt()
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(c)
                }
            }
            c.restore()
        }

        companion object {
            /**
             * 是否翻转布局
             */
            fun isReverseLayout(layoutManager: RecyclerView.LayoutManager): Boolean =
                (layoutManager as? androidx.recyclerview.widget.StaggeredGridLayoutManager)?.reverseLayout
                    ?: (layoutManager as LinearLayoutManager).reverseLayout
        }
    }


    interface OnRequestListener {
        fun onEnd()
        fun onError(jsonData: String, isPullRefresh: Boolean, errorType: ErrorType)
    }
}
