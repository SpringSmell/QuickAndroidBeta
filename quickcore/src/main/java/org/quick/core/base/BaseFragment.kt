package org.quick.core.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import android.view.View
import org.quick.core.base.fragments.ThemeFragment
import org.quick.core.common.IsOkDialog
import org.quick.core.common.LoadingDialog
import org.quick.core.config.Constant

/**
 * Created by chris Zou on 2016/6/12.
 *
 * @author chris Zou
 * @date 2016/6/12
 */
abstract class BaseFragment : ThemeFragment() {

    companion object {
        val SUCCESS = Constant.APP_SUCCESS_TAG
    }

    val isOkDialog: IsOkDialog by lazy { return@lazy IsOkDialog(activity) }
    val loadingDialog: LoadingDialog by lazy { return@lazy LoadingDialog(activity) }

    private var onInitListener: (() -> Unit)? = null


    fun setOnInitListener(onInitListener: () -> Unit) {
        this.onInitListener = onInitListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isInit) {
            isInit = true
            onInitListener?.invoke()
        }
    }
}
