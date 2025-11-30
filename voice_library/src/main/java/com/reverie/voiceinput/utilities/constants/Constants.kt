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
package com.reverie.voiceinput.utilities.constants


const val REV_STT_STREAM_URL = "wss://revapi.reverieinc.com/stream?"



const val INTENT_API_KEY = "api_key"
const val INTENT_APP_ID = "app_id"
const val INTENT_DOMAIN = "domain"
const val INTENT_LANG = "lang"
const val NO_INPUT_TIMEOUT="noInputTimeout"
const val SILENCE="silence"
const val TIMEOUT="timeout"
const val INTENT_LOGGING = "logging"

const val INTENT_TOKEN = "token"
const val INTENT_TOKEN_BASED = "token_based"
//Warning messages
const val WARNING_MISSING_MANIFEST =
    "Please ensure the following permissions are declared in the manifest:\n"
const val WARNING_PERMISSIONS_GRANT_REQUIRED =
    "Please grant all the required permissions for the application."
const val WARNING_NO_INTERNET = "No Internet Connection."

 class Languages() {
    companion object {
        const val HINDI = "hi"
        const val ASSAMESE = "as"
        const val BENGALI = "bn"
        const val GUJARATI = "gu"
        const val KANNADA = "kn"
        const val MALAYALAM = "ml"
        const val MARATHI = "mr"
        const val ODIA = "or"
        const val PUNJABI = "pa"
        const val TAMIL = "ta"
        const val TELUGU = "te"
        const val ENGLISH = "en"
    }
}


 class Domain() {

    companion object {
        const val VOICE_SEARCH = "voice_search"
        const val GENERIC="generic"
        const val BFSI="bfsi"
        const val APP_SEARCH="app_search"
        const val INDUS_APP_SEARCH="indusapp"
    }

}
/**
 * Default value=true
 * Possible values are :
 * 1. true - stores client’s audio and keeps transcript in logs.
 * 2. no_audio -  does not store client’s audio but keeps transcript in logs.
 * 3. no_transcript - does not keep transcript in logs but stores client’s audio.
 * 4. false - does not keep neither client’s audio nor transcript in log.

 */

class Logging()
{
    companion object{
        const val TRUE="true"
        const val FALSE="false"
        const val NO_AUDIO="no_audio"
        const val NO_TRANSCRIPT="no_transcript"

    }

}

