package org.quick.core.base

import android.content.Context
import android.view.View
import android.widget.ImageView
import org.quick.adapter.QuickAdapter
import org.quick.core.img.ImageManager

abstract class BaseAdapter<M> : QuickAdapter<M, BaseAdapter.BaseViewHolder>() {

    override fun onResultViewHolder(itemView: View): BaseViewHolder {
        return BaseViewHolder(itemView)
    }

    class BaseViewHolder(itemView: View) : QuickAdapter.ViewHolder(itemView) {

        override fun bindImg(context: Context, url: String, imageView: ImageView?):BaseViewHolder {
            //在这里绑定普通图片
            if (imageView != null)
                ImageManager.loadImage(context, url, imageView)
            return this
        }

        override fun bindImgRoundRect(context: Context, url: String, radius: Float, imageView: ImageView?): BaseViewHolder {
            //在这里绑定圆角图片
            if (imageView != null)
                ImageManager.loadRoundImage(context, url, radius.toInt(), imageView)
            return this
        }

        override fun bindImgCircle(context: Context, url: String, imageView: ImageView?): BaseViewHolder {
            //在这里绑定圆形图片
            if (imageView != null)
                ImageManager.loadCircleImage(context, url, imageView)
            return this
        }
    }
}