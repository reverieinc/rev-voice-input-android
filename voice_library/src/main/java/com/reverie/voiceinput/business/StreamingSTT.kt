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
package com.reverie.voiceinput.business

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.reverie.voiceinput.LOG.Companion.customLogger
import com.reverie.voiceinput.utilities.PermissionUtils.Companion.checkManifestPermissions
import com.reverie.voiceinput.utilities.PermissionUtils.Companion.checkPermissionsAudio
import com.reverie.voiceinput.utilities.PermissionUtils.Companion.isInternetAvailable
import com.reverie.voiceinput.utilities.constants.WARNING_MISSING_MANIFEST
import com.reverie.voiceinput.utilities.constants.WARNING_NO_INTERNET
import com.reverie.voiceinput.utilities.constants.WARNING_PERMISSIONS_GRANT_REQUIRED
import java.io.File
import java.util.*


/** The Speech-to-Text accurately converts speech into text using an API powered by Reverie's AI technology. The solution will transcribe the speech in real-time of various Indian languages and audio formats.
 *  @param apikey:  pass the apikey
 *  @param app_id:  pass the app_id
````
>**/
internal class StreamingSTT(
    context: Context?,
    private var apikey: String,
    private var app_id: String
) :
    CustomAudioRecorder.RecordingStateListener {

    private var mContext: Context? = context
    private var customAudioRecorder: CustomAudioRecorder
    lateinit var streamingSTTResultListener: StreamingSTTResultListener
    private lateinit var langCode: String
    private lateinit var domain: String
    private var noInputTimeout = 2
    private var silence = 1
    private var timeout = 15
    val handler = Handler(Looper.getMainLooper())
    private var isFinal = false

    val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO
    )

    interface OnConnectionEstablishedListener {
        fun onSuccess()
    }

    fun setOnConnectionListener(listener: OnConnectionEstablishedListener?) {
        this.listener = listener
        customLogger(TAG, "Listener set")
    }

    fun setOnResultListener(listener: StreamingSTTResultListener) {
        this.streamingSTTResultListener = listener
        customLogger(TAG, "Listener")

    }

    private var listener: OnConnectionEstablishedListener? = null
    private var customSocketListener: CustomSocketListener? = null

    init {
        customSocketListener = CustomSocketListener()
        // TODO: check if folder creation is not necessary
        mFileName = Objects.requireNonNull(
            mContext!!.getExternalFilesDir(null)
        )?.absolutePath + "/RevSttRecord"
        customLogger(TAG, "File name" + mFileName)
        val direct = File(mFileName)
        if (!direct.exists()) {
            direct.mkdir()
        }
        mFileName += "/recorded_audio.wav"
        customAudioRecorder = CustomAudioRecorder.getInstance(this)
        customLogger(TAG, "init: customAudioRecorder=${customAudioRecorder}")
        customAudioRecorder.setOutputFile(mFileName)
        customAudioRecorder.setSpeechClient(customSocketListener)
        customSocketListener!!.setEventCallback(object : CustomSocketListener.EventCallback {
            override fun onEvent(stage: Int) {
                customLogger(TAG, "onEvent: stage= $stage")
                if (stage == CustomSocketListener.SOCKET_OPENED) {
//                    listener!!.onSuccess()

                    streamingSTTResultListener.onConnectionSuccess("Success")
                }
            }
        })
        customSocketListener!!.setOnResultListener(object : InternalResultListener {
            override fun onResult(result: VoiceInputResultData?) {


                handler.post {
                    streamingSTTResultListener!!.onResult(result)
                }



                if (result?.final == true) {
                    StreamingSTT.inProcess = false

                    if(!isEOF)
                    {stopRecognitions()}
                    isFinal = true

                }
            }

            override fun onError(result: VoiceInputErrorResponseData) {
                StreamingSTT.isEOF = false
                if (!isFinal) {
                    handler.post {

                        if (inProcess) {

                            cancelRecognitions()
                        }
                        StreamingSTT.inProcess = false
                        streamingSTTResultListener!!.onError(result)
                    }
                }

            }

            override fun onEvent(stage: Int) {
                customLogger("OnEvent", "onEvent: 1")
                streamingSTTResultListener.onConnectionSuccess("$stage")
            }

        })

    }


    private fun addData(data: ByteArray) {
        customSocketListener?.addData(data)
    }

    /**
     *  @param langCode:pass the language code of for the required
     *  @param domain:  pass the domain as per requirement

     */
    fun setNoInputTimeout(noInputTimeout: Int) {

        this.noInputTimeout = noInputTimeout
    }

    fun setSilence(silence: Int) {

        this.silence = silence
    }

    fun setTimeout(timeout: Int) {
        this.timeout = timeout

    }

    fun startRecognitions(langCode: String, domain: String, logging: String) {
        isEOF=false
        if (StreamingSTT.inProcess) {
            return
        }
        isFinal = false
        if (mContext?.let { checkManifestPermissions(it, *requiredPermissions) } == true) {
            if (mContext?.let { checkPermissionsAudio(it) } == true) {
                if (isInternetAvailable(mContext!!)) {
                    this.langCode = langCode
                    this.domain = domain
                    try {

                        customAudioRecorder = CustomAudioRecorder.getInstance(this)
                        customLogger(
                            TAG,
                            "startRecognitions: customAudioRecorder=${customAudioRecorder}"
                        )

                        customAudioRecorder.setOutputFile(mFileName)
                        customAudioRecorder.setSpeechClient(customSocketListener)
                        StreamingSTT.inProcess = true
                        customLogger("onErrorStop", inProcess.toString())
                        customAudioRecorder.prepare()
                        customSocketListener!!.setSilence(silence)
                        customSocketListener!!.setTimeout(timeout)
                        customSocketListener!!.setNoInputTimeout(noInputTimeout)

                        //customSocketListener!!.startRequest(langCode, domain, apikey, app_id)
                        customSocketListener!!.startRequest(
                            langCode,
                            domain,
                            apikey,
                            app_id,
                            logging
                        )
                        customAudioRecorder.startRecordingProcess()

                        customLogger("TAG", "*********** Recording start ***********")
                        isRecording = true


                    } catch (e: Exception) {
                        customLogger(TAG, "prepare() failed due to: $e")
                        e.message?.let { parseError(it, 0) }
                            ?.let { streamingSTTResultListener.onError(it) }

                    }
                } else {
                    handler.post {
                        StreamingSTT.inProcess = false
                        streamingSTTResultListener.onError(
                            parseError(
                                WARNING_NO_INTERNET,
                                0
                            )
                        )
                    }


                }
            } else {
                handler.post {
                    StreamingSTT.inProcess = false
                    streamingSTTResultListener.onError(
                        parseError(
                            WARNING_PERMISSIONS_GRANT_REQUIRED,
                            0
                        )
                    )
                }
            }
        } else {
            handler.post {
                StreamingSTT.inProcess = false
                streamingSTTResultListener.onError(
                    parseError(
                        WARNING_MISSING_MANIFEST,
                        0
                    )

                )
            }
        }
    }

    fun stopRecognitions() {

//        if (!isRecording) {
//            return
//        }

        if (customAudioRecorder != null&& isRecording) {
            customSocketListener!!.endRequest()

            customAudioRecorder.stop()
            customAudioRecorder.release()
            customAudioRecorder.reset()
        }
        isRecording = false
        StreamingSTT.inProcess = false
    }

    fun cancelRecognitions() {

        if (customAudioRecorder != null&& isRecording) {

            customSocketListener!!.cancelRequest()

            customAudioRecorder.stop()
            customAudioRecorder.release()
            customAudioRecorder.reset()
            isEOF=false
        }
        isRecording = false
        StreamingSTT.inProcess = false
     //   StreamingSTT.inProcess = false
    }

    companion object {
        private const val TAG = "RecordingUtility"
        private var isRecording = false
        private var mFileName: String? = null
        internal var inProcess: Boolean = false
        internal var isEOF: Boolean =false
    }


    private fun parseError(message: String, code: Int): VoiceInputErrorResponseData {

        return VoiceInputErrorResponseData(message, code)
    }

    override fun recordingStart(start: Boolean) {
        streamingSTTResultListener.onRecordingStart(start)
    }

    override fun recordingEnd(stop: Boolean) {
        StreamingSTT.inProcess = false
        handler.post { streamingSTTResultListener.onRecordingEnd(stop) }


    }

    override fun recordingData(data: ByteArray, amplitude: Int) {
        streamingSTTResultListener.onRecordingData(data, amplitude)
    }

}