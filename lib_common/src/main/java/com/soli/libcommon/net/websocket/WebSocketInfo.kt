package com.soli.libcommon.net.websocket

import okhttp3.WebSocket

/**
 * @author Soli
 * @Time 2018/10/8 17:02
 */
data class WebSocketInfo(
    var webSocket: WebSocket?,
    var string: String? = null
)
