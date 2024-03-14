/*
 * All Rights Reserved. Copyright 2023. Reverie Language Technologies Limited.(https://reverieinc.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reverie.voice_input.business

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.voice_input.LOG.Companion.customLogger
import com.reverie.voice_input.utilities.constants.REV_STT_STREAM_URL
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit


internal class CustomSocketListener : WebSocketListener() {
    private var listener: InternalResultListener? = null
    private var eventCallback: EventCallback? = null
    private var socket: WebSocket? = null
    private val handler = Handler(Looper.getMainLooper())
    private var context: Context? = null

    interface EventCallback {
        fun onEvent(stage: Int)
    }

    /**
     * Setting the listener for getting the text results
     *
     * @param listener OnResultListener
     */
    fun setOnResultListener(listener: InternalResultListener?) {
        this.listener = listener
        customLogger(TAG, "Listener set")
    }

    /**
     * setting event callback for getting different socket related events
     *
     * @param callback EventCallback
     */
    fun setEventCallback(callback: EventCallback?) {
        eventCallback = callback
    }

    /**
     * Adds ByteString to the socket
     *
     * @param data the array byte data to be added
     */
    fun addData(data: ByteArray) {

        if (socket != null) {
            socket!!.send(ByteString.of(*data))
        }
    }

    /**
     * For starting the connection
     */
    fun startRequest(
        langCode: String,
        domain: String,
        apikey: String,
        appid: String,
        logging: String
    ) {
        customLogger(TAG, "Request started")
        run(langCode, domain, apikey, appid, logging)
    }

    /**
     * Stop listening to audio, end the streaming cycle
     */
    fun endRequest() {
        StreamingSTT.inProcess =false
        customLogger(TAG, "Ending request")

//        if (socket != null) socket!!.send("--EOF--".encodeUtf8())
        val byteArray = "--EOF--".toByteArray(charset("UTF-8"))
        val byteString = ByteString.of(*byteArray)
        if (socket != null) socket!!.send(byteString)


    }

    fun cancelRequest() {
        StreamingSTT.inProcess =false
        if (socket != null)
            socket!!.cancel()
    }


    private fun sendBackEvent(state: Int) {
        if (eventCallback != null) {
//            Log.d(TAG, "sendBackEvent: "+state)

            handler.post { eventCallback!!.onEvent(state) }
        }
    }

    private fun run(
        langCode: String,
        domain: String,
        apikey: String,
        appid: String,
        logging: String
    ) {
        val client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val streamUrl =
            "${REV_STT_STREAM_URL}apikey=$apikey&appid=$appid&appname=stt_stream&src_lang=$langCode&domain=$domain&logging=$logging&debug=true&timeout=30&silence=2"

        customLogger(TAG, "rev url= $streamUrl")
        val request = Request.Builder()
            .url(streamUrl)
            .build()
        client.newWebSocket(request, this)
        client.dispatcher.executorService.shutdown()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        customLogger(TAG, "onOpen: response = " + response.message)
        socket = webSocket
        handler.post {
            customLogger(TAG, "onOpen: ")
            listener!!.onEvent(SOCKET_OPENED)
            sendBackEvent(SOCKET_OPENED)
        }

    }

    private fun parseError(message: String, code: Int): VoiceInputErrorResponseData {
        StreamingSTT.inProcess =false
        return VoiceInputErrorResponseData(message, code)
    }

    private fun parseJson(jsonData: String): VoiceInputResultData {
        val jsonObject = JSONObject(jsonData)
        customLogger(TAG, "parseJson: json $jsonData")
        return VoiceInputResultData(
            jsonObject.getString("id"),
            jsonObject.getBoolean("success"),
            jsonObject.getBoolean("final"),
            jsonObject.getString("text"),
            jsonObject.getString("cause"),
            jsonObject.getDouble("confidence"),
            jsonObject.getString("display_text")
        )
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        text.let { super.onMessage(webSocket, it) }
        val responseData = parseJson(text)
        handler.post {
            listener?.onResult(responseData)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(1000, null)
        StreamingSTT.inProcess =false
        Log.d(TAG, "onClosing: ")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        customLogger(TAG, "onFailure: ")
        StreamingSTT.inProcess =false

        if (response != null && response!!.code != null && response!!.message != null) {
            var sttErrorResponseData = t.message?.let { parseError(it, response!!.code) }
            sttErrorResponseData?.let { listener!!.onError(it) }
        } else {
            t.message?.let { parseError(it, 0) }?.let { listener!!.onError(it) }
        }
    }

  override  fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
      Log.d(TAG, "onClosed: ")
    }
    companion object {
        private const val TAG = "CustomSocketListener"
        const val SOCKET_OPENED = 1
    }
}
