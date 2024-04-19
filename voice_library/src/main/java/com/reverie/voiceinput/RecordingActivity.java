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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.reverie.voiceinput.business.StreamingSTT;
import com.reverie.voiceinput.business.StreamingSTTResultListener;
import com.reverie.voiceinput.business.VoiceInputErrorResponseData;
import com.reverie.voiceinput.business.VoiceInputResultData;
import com.reverie.voiceinput.utilities.constants.ConstantsKt;

/**
 * Recording Activity of the dialog of the UI
 */

public class RecordingActivity extends AppCompatActivity {
    ImageView gifImageView, cancel;
    TextView outputTv;
    StreamingSTT streamingSTT;
    ProgressBar progressBar;
    VoiceInputListener listener;
    RelativeLayout relativeLayout;


    boolean isCancel = false;
    boolean isDown = true;
    boolean isRecordingGoing = false;

    boolean isMicPress = false;

    private String appId = "";
    private String apiKey = "";
    private String domain = "";
    private String lang = "";

    private String logging = "";

    private double silence = 1;
    private double noInputTimeout = 2;
    private TextView listeningTextView;
    private double timeout = 15;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_voice);
        Intent i = getIntent();
        apiKey = i.getStringExtra(ConstantsKt.INTENT_API_KEY);
        appId = i.getStringExtra(ConstantsKt.INTENT_APP_ID);
        lang = i.getStringExtra(ConstantsKt.INTENT_LANG);
        domain = i.getStringExtra(ConstantsKt.INTENT_DOMAIN);
        logging = i.getStringExtra(ConstantsKt.INTENT_LOGGING);
        silence = i.getDoubleExtra(ConstantsKt.SILENCE, 1);
        noInputTimeout = i.getDoubleExtra(ConstantsKt.NO_INPUT_TIMEOUT, 2);
        timeout = i.getDoubleExtra(ConstantsKt.TIMEOUT, 15);

        isCancel = false;
        listeningTextView = findViewById(R.id.listening_text);
        listeningTextView.setVisibility(View.GONE);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        gifImageView = findViewById(R.id.mic_gif);
        cancel = findViewById(R.id.x_button);
        outputTv = findViewById(R.id.display_tv);
        relativeLayout = findViewById(R.id.bottom_layout);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.INVISIBLE);

        outputTv.setText("");
        gifImageView.setOnClickListener(view -> {
            if (isRecordingGoing) {
                isMicPress = true;
                streamingSTT.stopRecognitions();
                outputTv.setText("");
            } else {
                streamingSTT.startRecognitions(lang, domain, logging);
            }
        });


        cancel.setOnClickListener(v -> {
            isCancel = true;
            slideDownCancel(relativeLayout);
        });
        streamingSTT = new StreamingSTT(getApplicationContext(), apiKey, appId);
        streamingSTT.setSilence(silence);
        streamingSTT.setNoInputTimeout(noInputTimeout);
        streamingSTT.setTimeout(timeout);
        streamingSTT.setStreamingSTTResultListener(new StreamingSTTResultListener() {
            @Override
            public void onResult(VoiceInputResultData voiceInputResultData) {
                if (voiceInputResultData != null) {
                    if (voiceInputResultData.getFinal() && !isCancel) {
//                    listener.onResult(streamingSTTResultData.getDisplay_text());


                        slideDown(relativeLayout);

                        RevVoiceInput.listener.onResult(voiceInputResultData);

                    }
                    outputTv.setText(voiceInputResultData.getDisplayText()); //Partial result
                }
            }

            @Override
            public void onError(VoiceInputErrorResponseData voiceInputErrorResponseData) {
                RevVoiceInput.listener.onError(voiceInputErrorResponseData);
                //outputTv.setText(voiceInputErrorResponseData.getError());
                listeningTextView.setVisibility(View.GONE);
                slideDownCancel(relativeLayout);
            }

            @Override
            public void onConnectionSuccess(String s) {


            }

            @Override
            public void onRecordingStart(boolean b) {
                isRecordingGoing = true;
                //output_tv.setText("Listening..");
                isMicPress = false;
                isCancel = false;
                progressBar.setVisibility(View.VISIBLE);
                listeningTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRecordingEnd(boolean b) {
                isRecordingGoing = false;
                progressBar.setVisibility(View.GONE);
                listeningTextView.setVisibility(View.GONE);

            }

            @Override
            public void onRecordingData(@NonNull byte[] bytes, int i) {

            }
        });

        startVoiceSearch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (streamingSTT != null)
            streamingSTT.cancelRecognitions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (streamingSTT != null)
            streamingSTT.cancelRecognitions();
    }

    /**
     * Method to start the voice search in the UI
     */
    private void startVoiceSearch() {
        relativeLayout = findViewById(R.id.bottom_layout);
        slideUp(relativeLayout);
    }

    /**
     * Method for Slide UP animation
     */
    private void slideUp(View view) {
        isDown = false;
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                streamingSTT.startRecognitions(lang, domain, logging);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * Method for Slide Down animation
     */
    private void slideDown(View view) {
        if (isDown) {
            return;
        }
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // DoXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        streamingSTT.stopRecognitions();
        isDown = true;
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }


            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void slideDownCancel(View view) {
        if (isDown) {
            return;
        }
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // DoXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        streamingSTT.cancelRecognitions();
        isDown = true;

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }


            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


}