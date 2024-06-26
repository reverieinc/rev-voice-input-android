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

import com.reverie.voiceinput.business.VoiceInputErrorResponseData;
import com.reverie.voiceinput.business.VoiceInputResultData;

/**
 * Interface to implement the callbacks of the Voice SDK
 */
public interface VoiceInputListener {


    /**
     * Callback to get the Result
     */

    public void onResult(VoiceInputResultData result);

    /**
     * Callback to get the Error
     */
    public void onError(VoiceInputErrorResponseData error);

    /**
     *
     */
    public void onRecordingStart(boolean isStart);

    /**
     *
     */
    public void onRecordingEnd(boolean isEnd);

}