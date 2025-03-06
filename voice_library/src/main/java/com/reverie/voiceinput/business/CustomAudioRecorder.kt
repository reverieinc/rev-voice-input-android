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

import android.annotation.SuppressLint

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.OnRecordPositionUpdateListener
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import android.media.audiofx.NoiseSuppressor
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.reverie.voiceinput.LOG.Companion.customLogger


import java.io.File
import java.io.IOException
import java.io.RandomAccessFile


internal class CustomAudioRecorder @SuppressLint("MissingPermission")
constructor(
    uncompressed: Boolean,
    audioSource: Int,
    sampleRate: Int,
    channelConfig: Int,
    audioFormat: Int, recordingStateListenerCall: RecordingStateListener
) {
    internal var customSocketListener: CustomSocketListener? = null
    var recordingStateListener1: RecordingStateListener? = recordingStateListenerCall
    private var isSocketOpened: Boolean = false
    internal interface RecordingStateListener {
        fun recordingStart(start: Boolean)
        fun recordingEnd(stop: Boolean)

        fun recordingData(data: ByteArray, amplitude: Int)

    }


    /**
     * INITIALIZING : recorder is initializing;
     * READY : recorder has been initialized, recorder not yet started
     * RECORDING : recording
     * ERROR : reconstruction needed
     * STOPPED: reset needed
     */
    enum class State {
        INITIALIZING, READY, RECORDING, ERROR, STOPPED
    }

    // Toggles uncompressed recording on/off; RECORDING_UNCOMPRESSED / RECORDING_COMPRESSED
    private var rUncompressed = false

    // Recorder used for uncompressed recording
    private var audioRecorder: AudioRecord? = null

    // Recorder used for compressed recording
    private var mediaRecorder: MediaRecorder? = null

    // Stores current amplitude (only in uncompressed mode)
    private var cAmplitude = 0

    // Output file path
    private var filePath: String? = null

    /**
     * @return recorder state
     */
    // Recorder state; see State
    var state: State? = null
        private set

    // File writer (only in uncompressed mode)
    private var randomAccessWriter: RandomAccessFile? = null

    // Number of channels, sample rate, sample size(size in bits), buffer size, audio source, sample size(see AudioFormat)
    private var nChannels: Short = 0
    private var sRate = 0
    private var bSamples: Short = 0
    private var bufferSize = 0
    private var aSource = 0
    private var aFormat = 0

    // Number of frames written to file on each output(only in uncompressed mode)
    private var framePeriod = 0

    // Buffer for output(only in uncompressed mode)
    private lateinit var buffer: ByteArray

    // Number of bytes written to file after header(only in uncompressed mode)
    // after stop() is called, this size is written to the header/data chunk in the wave file
    private var payloadSize = 0
    /*
     *
     * Method used for recording.
     *
     *
     *
     */


    var bufferNotSent: ByteArray = byteArrayOf()
    private val updateListener: OnRecordPositionUpdateListener =
        object : OnRecordPositionUpdateListener {
            override fun onPeriodicNotification(recorder: AudioRecord) {
                val n = recorder.read(buffer, 0, buffer.size)
                try {

                    val handler = Handler(Looper.getMainLooper())

                    bufferNotSent += buffer

                    if (customSocketListener != null && isSocketOpened) {
                        customSocketListener!!.addData(bufferNotSent)

                        bufferNotSent = byteArrayOf()

                    }

                    payloadSize += buffer.size
                    if (bSamples.toInt() == 16) {
                        for (i in 0 until buffer.size / 2) { // 16bit sample size
                            val curSample = getShort(buffer[i * 2], buffer[i * 2 + 1])
                            if (curSample > cAmplitude) { // Check amplitude
                                cAmplitude = curSample.toInt()
                            }
                        }
                    } else { // 8bit sample size
                        for (i in buffer.indices) {
                            if (buffer[i] > cAmplitude) { // Check amplitude
                                cAmplitude = buffer[i].toInt()
                            }
                        }
                    }
                    handler.post {

                        recordingStateListener1!!.recordingData(buffer, cAmplitude)

                    }
                } catch (e: IOException) {
                    customLogger(
                        CustomAudioRecorder::class.java.name,
                        "Error occured in updateListener, recording is aborted$e"
                    )
                    //stop();
                }
            }

            override fun onMarkerReached(recorder: AudioRecord) {
                // NOT USED
            }
        }

    /**fun2(String str, int... a)
     * Default constructor
     * Instantiates a new recorder, in case of compressed recording the parameters can be left as 0.
     * In case of errors, no exception is thrown, but the state is set to ERROR
     */
    init {
        try {
            customLogger("Compressed", uncompressed.toString())
            rUncompressed = uncompressed
            if (rUncompressed) {
                // RECORDING_UNCOMPRESSED
                bSamples = if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                    16
                } else {
                    8
                }
                nChannels = if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
                    1
                } else {
                    2
                }
                aSource = audioSource
                sRate = sampleRate
                aFormat = audioFormat
                framePeriod = sampleRate * TIMER_INTERVAL / 1000
                bufferSize = framePeriod * 2 * bSamples * nChannels / 8
                if (bufferSize < AudioRecord.getMinBufferSize(
                        sampleRate,
                        channelConfig,
                        audioFormat
                    )
                ) { // Check to make sure buffer size is not smaller than the smallest allowed one
                    bufferSize =
                        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                    // Set frame period and timer interval accordingly
                    framePeriod = bufferSize / (2 * bSamples * nChannels / 8)
                    Log.w(
                        CustomAudioRecorder::class.java.name,
                        "Increasing buffer size to $bufferSize"
                    )
                }
                audioRecorder =
                    AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize)
                customLogger("state", audioRecorder!!.recordingState.toString())
                if (NoiseSuppressor.isAvailable()) NoiseSuppressor.create(audioRecorder!!.audioSessionId)
                if (audioRecorder!!.state != AudioRecord.STATE_INITIALIZED) throw Exception("AudioRecord initialization failed")
                audioRecorder!!.setRecordPositionUpdateListener(updateListener)
                audioRecorder!!.positionNotificationPeriod = framePeriod
            }
            cAmplitude = 0
            filePath = null
            state = State.INITIALIZING


        } catch (e: Exception) {
            if (e.message != null) {
                customLogger(CustomAudioRecorder::class.java.name, e.message!!)
            } else {
                customLogger(
                    CustomAudioRecorder::class.java.name,
                    "Unknown error occurred while initializing recording"
                )
            }
            state = State.ERROR
        }
    }

    internal fun setSpeechClient(client: CustomSocketListener?) {
//        this.speechAsyncClient = client;
        customSocketListener = client
    }


    /**
     * Sets output file path, call directly after construction/reset.
     */
    fun setOutputFile(argPath: String?) {
        try {
            if (state == State.INITIALIZING) {
                filePath = argPath
                if (!rUncompressed) {
                    mediaRecorder!!.setOutputFile(filePath)
                }
            }
        } catch (e: Exception) {
            if (e.message != null) {
                customLogger(CustomAudioRecorder::class.java.name, e.message!!)
            } else {
                customLogger(
                    CustomAudioRecorder::class.java.name,
                    "Unknown error occurred while setting output path"
                )
            }
            state = State.ERROR
        }
    }


    /**
     * Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and the file path was not set
     * the recorder is set to the ERROR state, which makes a reconstruction necessary.
     * In case uncompressed recording is toggled, the header of the wave file is written.
     * In case of an exception, the state is changed to ERROR
     */
    fun prepare() {
        try {
            if (state == State.INITIALIZING) {
                customSocketListener!!.setEventCallback(object :
                    CustomSocketListener.EventCallback {
                    override fun onEvent(stage: Int) {
                        customLogger("Stage", "onEvent: stage= $stage")
                        if (stage == CustomSocketListener.SOCKET_OPENED) {
                            isSocketOpened = true
                        }
                    }
                })

                if (rUncompressed) {
                    customLogger("status", audioRecorder?.state.toString() + " " + filePath)
                    if ((audioRecorder!!.state == AudioRecord.STATE_INITIALIZED) and (filePath != null)) {
                        // write file header
//                        randomAccessWriter = RandomAccessFile(filePath, "rw")
//                        randomAccessWriter!!.setLength(0) // Set file length to 0, to prevent unexpected behavior in case the file already existed
//                        randomAccessWriter!!.writeBytes("RIFF")
//                        randomAccessWriter!!.writeInt(0) // Final file size not known yet, write 0
//                        randomAccessWriter!!.writeBytes("WAVE")
//                        randomAccessWriter!!.writeBytes("fmt ")
//                        randomAccessWriter!!.writeInt(Integer.reverseBytes(16)) // Sub-chunk size, 16 for PCM
//                        randomAccessWriter!!.writeShort(
//                            java.lang.Short.reverseBytes(1.toShort()).toInt()
//                        ) // AudioFormat, 1 for PCM
//                        randomAccessWriter!!.writeShort(
//                            java.lang.Short.reverseBytes(nChannels).toInt()
//                        ) // Number of channels, 1 for mono, 2 for stereo
//                        randomAccessWriter!!.writeInt(Integer.reverseBytes(sRate)) // Sample rate
//                        randomAccessWriter!!.writeInt(Integer.reverseBytes(sRate * bSamples * nChannels / 8)) // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
//                        randomAccessWriter!!.writeShort(
//                            java.lang.Short.reverseBytes((nChannels * bSamples / 8).toShort())
//                                .toInt()
//                        ) // Block align, NumberOfChannels*BitsPerSample/8
//                        randomAccessWriter!!.writeShort(
//                            java.lang.Short.reverseBytes(bSamples).toInt()
//                        ) // Bits per sample
//                        randomAccessWriter!!.writeBytes("data")
//                        randomAccessWriter!!.writeInt(0) // Data chunk size not known yet, write 0
                        buffer = ByteArray(framePeriod * bSamples / 8 * nChannels)
                        state = State.READY
                    } else {
                        customLogger(
                            CustomAudioRecorder::class.java.name,
                            "prepare() method called on uninitialized recorder"
                        )
                        state = State.ERROR
                    }
                } else {
                    mediaRecorder!!.prepare()
                    state = State.READY
                }
            } else {
                customLogger(
                    CustomAudioRecorder::class.java.name,
                    "prepare() method called on illegal state"
                )
                release()
                state = State.ERROR
            }
        } catch (e: Exception) {
            if (e.message != null) {
                customLogger(CustomAudioRecorder::class.java.name, e.message!!)
            } else {
                customLogger(CustomAudioRecorder::class.java.name, "Unknown error occured in prepare()")
            }
            state = State.ERROR
        }
    }

    /**
     * Releases the resources associated with this class, and removes the unnecessary files, when necessary
     */
    fun release() {
        if (state == State.RECORDING) {

            stop()
        } else {
            if ((state == State.READY) and rUncompressed) {
                try {
                    randomAccessWriter!!.close() // Remove prepared file
                } catch (e: IOException) {
                    customLogger(
                        CustomAudioRecorder::class.java.name,
                        "I/O exception occurred while closing output file"
                    )
                }
                filePath?.let { File(it).delete() }
            }
        }
        if (rUncompressed) {
            if (audioRecorder != null) {
                audioRecorder!!.release()
            }
        } else {
            if (mediaRecorder != null) {
                mediaRecorder!!.release()
                customLogger(TAG, "release: mediaRecorder")
            }
        }
    }

    /**
     * Resets the recorder to the INITIALIZING state, as if it was just created.
     * In case the class was in RECORDING state, the recording is stopped.
     * In case of exceptions the class is set to the ERROR state.
     */
    @SuppressLint("MissingPermission")
    fun reset() { StreamingSTT.inProcess =false
        try {
            if (state != State.ERROR) {
                release()
                /// Reset file path
                cAmplitude = 0 // Reset amplitude
                if (rUncompressed) {
                    customLogger("AudioRecorder", "reset: audio")
                    audioRecorder = AudioRecord(aSource, sRate, nChannels + 1, aFormat, bufferSize)
                } else {
                    mediaRecorder = MediaRecorder()
                    mediaRecorder!!.setAudioSource(AudioSource.MIC)
                    mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    customLogger(TAG, "reset: mediaRecorder")
                }
                state = State.INITIALIZING
            }
        } catch (e: Exception) {
            customLogger(CustomAudioRecorder::class.java.name, e.message!!)
            state = State.ERROR
        }
    }

    /**
     * Starts the recording, and sets the state to RECORDING.
     * Call after prepare().
     */


    internal fun startRecordingProcess(

    ) {StreamingSTT.inProcess=true
        customLogger(TAG, "startRecordingProcess: $state")
        if (state == State.READY) {
            if (rUncompressed) {

                payloadSize = 0
                audioRecorder!!.startRecording()
//                audioRecorder!!.read(buffer, 0, buffer.size)
                     recordingStateListener1!!.recordingStart(true)


            } else {
                mediaRecorder!!.start()
            }
            state = State.RECORDING

        } else {
            customLogger(CustomAudioRecorder::class.java.name, "start() called on illegal state")
            state = State.ERROR
        }
    }

    /**
     * Stops the recording, and sets the state to STOPPED.
     * In case of further usage, a reset is needed.
     * Also finalizes the wave file in case of uncompressed recording.
     */
    fun stop() {
            StreamingSTT.inProcess =false
            if (rUncompressed) {

                if (audioRecorder != null && audioRecorder!!.state == AudioRecord.STATE_INITIALIZED) {
                    try{
                    audioRecorder!!.stop()}
                    catch (exception:Exception)
                    {
                        customLogger(TAG, "stop: "+exception)
                    }


                    recordingStateListener1!!.recordingEnd(true)
                    isSocketOpened = false
                }

            } else {
                mediaRecorder!!.stop()

            }
            state = State.STOPPED

    }

    private fun getShort(argB1: Byte, argB2: Byte): Short {
        return (argB1.toInt() or (argB2.toInt() shl 8)).toShort()
    }



    companion object {
        private val sampleRates = intArrayOf(8000, 16000)
        private val audioFormat = intArrayOf(
            AudioFormat.ENCODING_PCM_8BIT,
            AudioFormat.ENCODING_PCM_16BIT
        )
        private const val TAG = "CustomAudioRecorder"


        @Volatile
        private var instance: CustomAudioRecorder? = null
        fun getInstance(
            recordingStateListener: RecordingStateListener
        ): CustomAudioRecorder {
            var selectRate = 1
            lateinit var result: CustomAudioRecorder

            do {
                result = CustomAudioRecorder(
                    true,
                    AudioSource.MIC,
                    sampleRates[selectRate],
                    AudioFormat.CHANNEL_IN_MONO,
                    audioFormat[selectRate], recordingStateListener
                )
            } while ((++selectRate < sampleRates.size) and (result.state != State.INITIALIZING))
            instance = result
            return result


        }
        private const val TIMER_INTERVAL = 60 * 4
    }
}