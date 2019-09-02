package com.example.quickandroid.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.ajguan.library.EasyRefreshLayout
import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_adapter.*
import org.quick.async.Async
import org.quick.core.base.BaseActivity
import org.quick.core.base.BaseAdapter
import org.quick.utils.FormatUtils

/**
 * 适配器示例
 */
class ExAdapterActivity : BaseActivity() {

    lateinit var adapter: Adapter
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_adapter

    override fun onInit() {
        adapter = Adapter()
    }

    override fun onInitLayout() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.onItemClick { view, viewHolder, position, itemData ->
            toast(position.toString())
        }
        adapter.onClick({ view, viewHolder, position, itemData ->
            toast("关注:$position")
        }, R.id.actionTv)
    }

    override fun onBindListener() {
        refreshLayout.addEasyEvent(object : EasyRefreshLayout.EasyEvent {
            override fun onLoadMore() {
                Async.delay({
                    for (index in adapter.dataList().size..adapter.itemCount + 10)
                        adapter.add(index.toString())
                    refreshLayout.loadMoreComplete()
                }, 2000)
            }

            override fun onRefreshing() {
                Async.delay({
                    val dataList = mutableListOf<String>()
                    for (index in 0..10) dataList.add(index.toString())
                    adapter.dataList(dataList)
                    refreshLayout.refreshComplete()
                }, 2000)
            }
        })
    }

    override fun onAction() {
        for (index in 0..10)
            adapter.add(index.toString())
    }

    class Adapter : BaseAdapter<String>() {
        override fun onBindData(
            holder: BaseViewHolder,
            position: Int,
            itemData: String,
            viewType: Int
        ) {
            holder.setText(R.id.titleTv, position.toString())
                .setText(R.id.actionTv, "关注$position")
        }

        override fun onResultLayoutResId(viewType: Int): Int = R.layout.item_adapter

        override fun onResultMargin(position: Int): Float = FormatUtils.dip2px(context, 20f)
    }
}