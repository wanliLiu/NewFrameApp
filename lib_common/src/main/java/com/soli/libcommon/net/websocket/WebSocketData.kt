package com.soli.libcommon.net.websocket

import com.soli.libcommon.net.ApiCallBack
import com.soli.libcommon.net.ApiHelper


/**
 *
 * @author Soli
 * @Time 2019/2/26 14:05
 */
data class WebSocketData(val builer: ApiHelper.Builder, val callback: ApiCallBack<Any>?)