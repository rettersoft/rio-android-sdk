package com.rettermobile.rbs

import android.text.TextUtils
import android.util.Log
import com.rettermobile.rbs.util.Logger
import com.rettermobile.rbs.util.RBSRegion
import okhttp3.*
import java.net.SocketException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Created by semihozkoroglu on 24.07.2021.
 */
class RBSWebSocket constructor(
    val region: RBSRegion = RBSRegion.EU_WEST_1,
    val webSocketListener: WebSocketListener,
    val logger: Logger
) {
    private var webSocket: WebSocket? = null
    private var okHttpClient: OkHttpClient? = null
    private var listener: WebSocketListener? = null
    private val requestBuilder = Request.Builder()

    private val availableSocket = Semaphore(1, true)

    private var token: String? = ""

    var isConnected = false
    var isConnectionPaused = false

    fun connect(token: String?): Boolean {
        availableSocket.acquire()

        if (TextUtils.equals(this.token, token) && isConnected) {
            logger.log(
                "RBSWebSocket token same: ${
                    TextUtils.equals(
                        this.token,
                        token
                    )
                } isConnected: $isConnected returned!"
            )

            logger.log("RBSWebSocket semaphore released")
            availableSocket.release()

            return false
        }

        if (isConnectionPaused) {
            logger.log("RBSWebSocket connection paused!")

            logger.log("RBSWebSocket semaphore released")
            availableSocket.release()

            return false
        }

        val url = "${region.socketUrl}?auth=$token"
        logger.log("RBSWebSocket connect called with url: $url")

        disconnect()

        listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.e(
                    "RBSService",
                    "RBSWebSocket onOpen instance equal ${this@RBSWebSocket.webSocket == webSocket}"
                )

                if (this@RBSWebSocket.webSocket == webSocket) {
                    isConnected = true
                    logger.log("RBSWebSocket onOpen OK")

                    webSocketListener.onOpen(webSocket, response)
                    this@RBSWebSocket.token = token

                    logger.log("RBSWebSocket semaphore released")
                    availableSocket.release()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.e(
                    "RBSService",
                    "RBSWebSocket onClosed instance equal ${this@RBSWebSocket.webSocket == webSocket}"
                )

                if (this@RBSWebSocket.webSocket == webSocket) {
                    isConnected = false

                    logger.log("RBSWebSocket onClosed code: $code reason: $reason")

                    webSocketListener.onClosed(webSocket, code, reason)

                    logger.log("RBSWebSocket semaphore released")
                    availableSocket.release()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(
                    "RBSService",
                    "RBSWebSocket onFailure instance equal ${this@RBSWebSocket.webSocket == webSocket}"
                )

                if (this@RBSWebSocket.webSocket == webSocket) {
                    isConnected = false

                    logger.log("RBSWebSocket onFailure $t")

                    if ((t is SocketException) && TextUtils.equals(t.message, "Socket closed")) {
                        logger.log("RBSWebSocket Socket Closed!!!")
                    } else {
                        webSocketListener.onFailure(webSocket, t, response)

                        Thread.sleep(3000)
                    }

                    logger.log("RBSWebSocket semaphore released")
                    availableSocket.release()
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.e(
                    "RBSService",
                    "RBSWebSocket onClosing instance equal ${this@RBSWebSocket.webSocket == webSocket}"
                )

                if (this@RBSWebSocket.webSocket == webSocket) {
                    logger.log("RBSWebSocket onClosing code: $code reason: $reason")

                    webSocketListener.onClosing(webSocket, code, reason)

                    logger.log("RBSWebSocket semaphore released")
                    availableSocket.release()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.e(
                    "RBSService",
                    "RBSWebSocket onMessage instance equal ${this@RBSWebSocket.webSocket == webSocket}"
                )

                if (this@RBSWebSocket.webSocket == webSocket) {
                    logger.log("RBSWebSocket onMessage $text")

                    if (this@RBSWebSocket.webSocket == webSocket) {
                        webSocketListener.onMessage(webSocket, text)
                    }
                }
            }
        }

        okHttpClient = OkHttpClient.Builder()
            .pingInterval(5, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        webSocket = okHttpClient!!.newWebSocket(requestBuilder.url(url).build(), listener!!)
        logger.log("RBSWebSocket new connection created!")

        return true
    }

    fun disconnect(paused: Boolean = false) {
        isConnectionPaused = paused

        logger.log("RBSWebSocket disconnect connectionPaused: $isConnectionPaused")

        listener = null

        webSocket?.close(1001, "disconnect called")

        logger.log("RBSWebSocket disconnect called")
        webSocket?.cancel()

        webSocket = null

        try {
            Log.e(
                "RBSService",
                "RBSWebSocket connection pool count: ${okHttpClient?.connectionPool?.connectionCount()}"
            )

            okHttpClient?.dispatcher?.executorService?.shutdown()
            okHttpClient?.connectionPool?.evictAll()

            logger.log("RBSWebSocket connection pool shutdown & evictAll")
        } catch (e: Exception) {
            logger.log("RBSWebSocket connection pool count exception: ${e.message}")
        }

        okHttpClient = null

        isConnected = false
    }
}