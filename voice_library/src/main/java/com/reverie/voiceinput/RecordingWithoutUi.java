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

package com.reverie.voiceinput;

import android.content.Context;


import androidx.annotation.NonNull;

import com.reverie.voiceinput.business.StreamingSTT;
import com.reverie.voiceinput.business.VoiceInputErrorResponseData;
import com.reverie.voiceinput.business.VoiceInputResultData;
import com.reverie.voiceinput.business.StreamingSTTResultListener;


/**
 * Voice Search Without the Dialog UI
 */

class RecordingWithoutUi {
    private StreamingSTT streamingSTT;
    private Context context;
    private String apiKey;
    private String appId;
    private String domain;
    private String lang;

    private String logging;

    /**
     * Constructor
     *
     * @param context  :Context of the application
     * @param apiKey:  pass the apikey
     * @param appId:   pass the app_id
     * @param domain:  pass the domain
     * @param lang:    language for voice_search
     * @param logging: If user wants to store the logging data in server Default value=true
     *                 Possible values are :
     *                 1. true - stores client’s audio and keeps transcript in logs.
     *                 2. no_audio -  does not store client’s audio but keeps transcript in logs.
     *                 3. no_transcript - does not keep transcript in logs but stores client’s audio.
     *                 4. false - does not keep neither client’s audio nor transcript in log
     */
    RecordingWithoutUi(Context context, String apiKey, String appId, String domain, String lang, String logging,int noInputTimeout,int silence,int timeout) {
        this.context = context;
        this.apiKey = apiKey;
        this.appId = appId;
        this.domain = domain;
        this.lang = lang;
        this.logging = logging;


        streamingSTT = new StreamingSTT(context, apiKey, appId);
        streamingSTT.setNoInputTimeout(noInputTimeout);
        streamingSTT.setTimeout(timeout);
        streamingSTT.setSilence(silence);
    }


    /**
     * Method to start the Voice Search
     */

    void startRecognitions() {

        streamingSTT.setStreamingSTTResultListener(new StreamingSTTResultListener() {
            @Override
            public void onResult(VoiceInputResultData result) {
                RevVoiceInput.listener.onResult(result);
            }

            @Override
            public void onError(@NonNull VoiceInputErrorResponseData result) {
                RevVoiceInput.listener.onError(result);
            }

            @Override
            public void onConnectionSuccess(@NonNull String result) {
                //CustomLogger("Success", String.valueOf(2));
            }

            @Override
            public void onRecordingStart(boolean isTrue) {
                RevVoiceInput.listener.onRecordingStart(isTrue);
            }

            @Override
            public void onRecordingEnd(boolean isTrue) {
                RevVoiceInput.listener.onRecordingEnd(isTrue);
            }

            @Override
            public void onRecordingData(@NonNull byte[] data, int amplitude) {

            }
        });
        streamingSTT.startRecognitions(lang, domain, logging);

    }

    /**
     * Method to stop the Voice Search
     */

    void stopRecognitions() {
        if (streamingSTT != null)
            streamingSTT.stopRecognitions();

    }

    void cancelRecognitions() {
        if (streamingSTT != null)
            streamingSTT.cancelRecognitions();


    }

}
