package org.quick.core.config

import org.quick.base.SPHelper

/**
 * 接口地址
 */
object UrlPath {

    private const val baseUrlOnLine = "https://www.maotanlvxing.com/"
    private const val baseUrlOffLine = "http://csapi.maotanlvxing.com/"

    val baseUrl: String
        get() = if (SPHelper.getValue("isDebug",false)) baseUrlOffLine else baseUrlOnLine

    const val joinGroup = "index.php/v1/group/setJoinGroup"
}