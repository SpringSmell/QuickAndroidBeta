package com.example.quickandroid.ui

import com.example.quickandroid.R
import com.example.quickandroid.model.KotlinModel
import org.jetbrains.anko.dip
import org.quick.async.Async
import org.quick.core.base.BaseAdapter
import org.quick.core.base.activities.QuickListActivity

/**
 * 列表
 */
class ExQuickListActivity : QuickListActivity<KotlinModel, KotlinModel.ResultModel>() {
    override val isPullRefreshEnable: Boolean
        get() = true
    override val isLoadMoreEnable: Boolean
        get() = true

    override fun onResultUrl(): String = ""

    /**
     * 上传参数
     */
    override fun onResultParams(params: MutableMap<String, String>) {
        params["key1"] = "data1"
        params["key2"] = "data2"
    }

    /**
     * 刷新成功
     */
    override fun onRefreshSuc(model: KotlinModel) {
        setDataList(model.dataList)
        Async.delay({ refreshComplete() }, 2000)
    }

    /**
     * 加载成功
     */
    override fun onLoadMoreSuc(model: KotlinModel) {
        addData(model.dataList)
        Async.delay({ loadMoreComplete() }, 2000)
    }

    override fun onResultItemResId(viewType: Int): Int = R.layout.item_quick_list_activity

    override fun onBindData(
        holder: BaseAdapter.BaseViewHolder,
        position: Int,
        itemData: KotlinModel.ResultModel,
        viewType: Int
    ) {
        holder.setText(R.id.contentTv, position.toString())
    }

    override fun onResultItemMargin(position: Int): Float = dip(20).toFloat()
    override fun onAction() {
        action()
        Async.delay({
            for (index in 0..10)
                addData(KotlinModel.ResultModel())
            refreshComplete()
        }, 2000)
        onItemClick { view, viewHolder, position, itemData ->
            toast(position.toString())
        }
        onClick({ view, viewHolder, position, itemData ->
            toast("添加好友$position")
        }, R.id.addTv)

        requestListener(object : OnRequestListener {
            override fun onEnd() {

            }

            override fun onError(jsonData: String, isPullRefresh: Boolean, errorType: ErrorType) {

            }

        })
    }
}