package org.quick.core.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 跑马灯
 * Created by zoulx on 2017/12/15.
 */

class MarqueeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setSingleLine(true)
        marqueeRepeatLimit = Int.MAX_VALUE
        ellipsize = TextUtils.TruncateAt.MARQUEE
        setSingleLine()
        isFocusableInTouchMode = true
        setHorizontallyScrolling(true)
    }

    override fun isFocused(): Boolean {
        return true
    }
}
