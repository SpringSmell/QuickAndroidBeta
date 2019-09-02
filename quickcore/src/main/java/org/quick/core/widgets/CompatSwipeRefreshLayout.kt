package org.quick.core.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.ScrollView

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.google.android.material.appbar.AppBarLayout

import org.quick.core.R


/**
 * The class resolve clash for absListView
 *
 * @url http://www.eoeandroid.com/thread-914273-1-1.html?_dsign=bf09d67b
 * Created by chris zou on 2016/8/2.
 */
class CompatSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    internal var mAbsListView: AbsListView? = null
    internal var mScrollView: ScrollView? = null
    internal var type = 0

    var mAppBarStateChangeListener = object : AppBarStateChangeListener() {
        override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
            when (state) {
                //展开
                State.EXPANDED -> isEnabled = true
                //拆起了
                //State.COLLAPSED ->{}
                else -> if (isEnabled) isEnabled = false
            }
        }
    }

    init {
        setColorSchemeResources(
            R.color.colorHoloBlueBright,
            R.color.colorHoloGreenLight,
            R.color.colorHoloOrangeLight,
            R.color.colorHoloRedLight,
            R.color.colorRead
        )
    }


    /**
     * RecyclerView未有冲突，建议使用
     *
     * @param listView
     */
    fun setResolveListView(listView: AbsListView) {
        this.mAbsListView = listView
        type = 1
    }

    fun setResolveScrollView(scrollView: ScrollView) {
        this.mScrollView = scrollView
        type = 2
    }

    fun setupAppBarLayout(appBarLayout: AppBarLayout) {
        appBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener)
    }

    fun removeAppBarLayout(appBarLayout: AppBarLayout) {
        appBarLayout.removeOnOffsetChangedListener(mAppBarStateChangeListener)
    }

    override fun canChildScrollUp(): Boolean {
        when (type) {
            1//ListView
            -> {
                if (mAbsListView != null && mAbsListView is AbsListView && mAbsListView!!.visibility == View.VISIBLE) {
                    return mAbsListView!!.childCount > 0 && (mAbsListView!!.firstVisiblePosition > 0 || mAbsListView!!.getChildAt(
                        0
                    ).top < mAbsListView!!.paddingTop)
                }
                if (mScrollView != null && mScrollView!!.visibility == View.VISIBLE) {
                    return mScrollView!!.scrollY > 0
                }
            }
            2//ScrollView
            -> if (mScrollView != null && mScrollView!!.visibility == View.VISIBLE) {
                return mScrollView!!.scrollY > 0
            }
        }
        return super.canChildScrollUp()
    }


}


abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    private var mCurrentState = State.IDLE

    enum class State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                mCurrentState = State.EXPANDED
            }
            Math.abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                mCurrentState = State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                mCurrentState = State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
}
