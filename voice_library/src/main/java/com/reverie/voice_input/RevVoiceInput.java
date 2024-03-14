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
package com.reverie.voice_input;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reverie.voice_input.utilities.constants.ConstantsKt;
import com.reverie.voice_input.utilities.constants.Domain;
import com.reverie.voice_input.utilities.constants.Languages;


/**
 * This the Class for Initiating Voice Search
 */
public class RevVoiceInput {

    protected static VoiceInputListener listener; /*static Listener*/

    boolean isUiNeeded = false;

    private final String apiKey;
    private final String appId;

    private String domain = Domain.VOICE_SEARCH;
    private String lang = Languages.ENGLISH;

    private String logging;

    private RecordingWithoutUi recordingWithoutUi;

    /**
     * Constructor call for VoiceSearch where first parameter is the api_key and the second parameter is app_id
     *
     * @param apiKey: pass the apikey
     * @param appId:  pass the app_id
     */

    public RevVoiceInput(String apiKey, String appId, String logging) {

        this.apiKey = apiKey;
        this.appId = appId;
        this.logging = logging;
    }

    /**
     * Constructor call for VoiceSearch where first parameter is the api_key , second parameter is app_id ,third parameter is domain , fourth parameter is the language
     *
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
    public RevVoiceInput(String apiKey, String appId, String domain, String lang, String logging) {
        this.apiKey = apiKey;
        this.appId = appId;
        this.domain = domain;
        this.lang = lang;
        this.logging = logging;
    }


    /**
     * Method to initialise the callbacks of the voice search
     */

    public void setListener(VoiceInputListener listener) {
        RevVoiceInput.listener = listener;
    }


    /**
     * Method to start the audio recognition where the first parameter is the context of the activity and second parameter is a boolean defining if Voice Search is needed with or
     * without UI by-default here domain is 'voice_search' and language is 'english'
     *
     * @param context     :Context of the application
     * @param isUINeeded: is Ui required
     */

    public void startRecognition(Context context, Boolean isUINeeded) {
        if(RevVoiceInput.listener == null)
        {
            Log.e("StartRecognition", "startRecognition: listener should be set first" );
            return;
        }

        isUiNeeded = isUINeeded;

        if (isUINeeded) {
            context.startActivity(new Intent(context, RecordingActivity.class).
                    putExtra(ConstantsKt.INTENT_API_KEY, apiKey).
                    putExtra(ConstantsKt.INTENT_APP_ID, appId).
                    putExtra(ConstantsKt.INTENT_DOMAIN, domain).
                    putExtra(ConstantsKt.INTENT_LANG, lang).
                    putExtra(ConstantsKt.INTENT_LOGGING, logging).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } else {
            recordingWithoutUi = new RecordingWithoutUi(context, apiKey, appId, domain, lang, logging);
            recordingWithoutUi.startRecognitions();
        }
    }


    /**
     * Method to finish the recording
     */
    public void finishInput() {
        if (!isUiNeeded && recordingWithoutUi != null)
            recordingWithoutUi.stopRecognitions();
    }
    /**
     * Method to abort stop the recording
     */
    public void cancel() {
        if (!isUiNeeded && recordingWithoutUi != null)
            recordingWithoutUi.cancelRecognitions();

    }

    /**
     * Method to start the audio recognition where the first parameter is the context of the activity and second parameter is a boolean defining if Voice Search is needed with or
     * without UI ,third parameter is domain , fourth parameter is the language
     */
    public void startRecognition(Context context, Boolean isUINeeded, String domain, String lang) {
        if(RevVoiceInput.listener == null)
        {
            Log.e("StartRecognition", "startRecognition: listener should be set first" );
            return;
        }
        isUiNeeded = isUINeeded;
        this.domain = domain;
        this.lang = lang;
        if (isUINeeded) {
            context.startActivity(new Intent(context, RecordingActivity.class).
                    putExtra(ConstantsKt.INTENT_API_KEY, apiKey).
                    putExtra(ConstantsKt.INTENT_APP_ID, appId).
                    putExtra(ConstantsKt.INTENT_DOMAIN, domain).
                    putExtra(ConstantsKt.INTENT_LANG, lang).
                    putExtra(ConstantsKt.INTENT_LOGGING, logging).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } else {
            recordingWithoutUi = new RecordingWithoutUi(context, apiKey, appId, domain, lang, logging);
            recordingWithoutUi.startRecognitions();
        }
    }


}
