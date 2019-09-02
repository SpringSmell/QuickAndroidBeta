package org.quick.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import org.quick.async.Async
import org.quick.viewHolder.ViewHolder

/**
 * @describe
 * @author ChrisZou
 * @date 2018/7/6-10:18
 * @from https://github.com/SpringSmell/quick.library
 * @email chrisSpringSmell@gmail.com
 */
class Toast private constructor() {
    private lateinit var builder: Builder
    private var toast: Toast? = null
    private var holder: ViewHolder? = null

    private fun setupToast(builder: Builder): org.quick.base.Toast {
        this.builder = builder
        return this
    }

    fun show(msg: CharSequence): ViewHolder {
        Async.runOnUiThread {
            val toast = config(msg)
            toast.show()
        }
        return createViewHolder()
    }

    private fun config(msg: CharSequence): Toast {
        if (toast == null || toast?.view?.id != builder.resId) {/*布局发生变化将重新初始化*/
            toast = Toast(QuickAndroid.applicationContext)
            toast?.view = createViewHolder().itemView
        }
        holder?.setText(R.id.msgTv, msg)/*自定义View将无法设置msg*/
        toast?.duration = builder.duration
        toast?.setGravity(builder.gravity, builder.xOffset, builder.yOffset)
        return toast!!
    }

    @SuppressLint("ResourceType")
    private fun createViewHolder(): ViewHolder {
        if (holder?.itemView?.id != builder.resId) {
            val tempView =
                (QuickAndroid.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    builder.resId,
                    null
                )
            tempView.id = builder.resId
            holder = ViewHolder(tempView)
        }
        return holder!!
    }

    private class ClassHolder {
        companion object {
            val INSTANCE = Toast()
        }
    }

    companion object {
        fun show(msg: CharSequence) {
            Builder().show(msg)
        }

        fun show(@StringRes msg: Int) {
            Builder().show(QuickAndroid.applicationContext.getString(msg))
        }

        fun showLong(msg: CharSequence) {
            Builder().duration(1).show(msg)
        }

        fun showLong(@StringRes msg: Int) {
            Builder().duration(1).show(QuickAndroid.applicationContext.getString(msg))
        }
    }

    class Builder(@LayoutRes var resId: Int = R.layout.app_toast) {
        internal var gravity = Gravity.BOTTOM
        internal var duration: Int = Toast.LENGTH_SHORT
        internal var xOffset = 0
        internal var yOffset = 200

        fun gravity(gravity: Int, xOffset: Int = 0, yOffset: Int = 0): Builder {
            this.gravity = gravity
            this.xOffset = xOffset
            this.yOffset = yOffset
            return this
        }

        /**
         * Set how long to show the view for.
         * @see Toast.LENGTH_SHORT
         * @see Toast.LENGTH_LONG
         */
        fun duration(duration: Int): Builder {
            this.duration = duration
            return this
        }

        fun build() = ClassHolder.INSTANCE.setupToast(this)

        fun create(msg: String) = build().config(msg)

        fun show(msg: CharSequence) = build().show(msg)
    }
}