package com.reverie.voice_search_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rev.voice_search_demo.BuildConfig;
import com.rev.voice_search_demo.R;
import com.reverie.voice_input.LOG;
import com.reverie.voice_input.RecordingActivity;
import com.reverie.voice_input.RevVoiceInput;

import com.reverie.voice_input.VoiceInputListener;
import com.reverie.voice_input.business.VoiceInputErrorResponseData;
import com.reverie.voice_input.business.VoiceInputResultData;
import com.reverie.voice_input.utilities.constants.Domain;
import com.reverie.voice_input.utilities.constants.Languages;
import com.reverie.voice_input.utilities.constants.Logging;


public class JavaDemo extends AppCompatActivity {
    Button searchBtn, stopBtn, searchBtnWithoutUi;
//    RevVoiceInput voiceSearch;
    RevVoiceInput  voiceSearch;

    TextView outputTv;
    private static final String TAG = "JavaDemo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }
        searchBtn = findViewById(R.id.searchBtn);
        stopBtn = findViewById(R.id.stopBtn);
        outputTv = findViewById(R.id.outputTv);
        searchBtnWithoutUi = findViewById(R.id.searchBtnwithoutUi);
        LOG.Companion.setDEBUG(true);// implement customlogger

        voiceSearch = new RevVoiceInput(
           BuildConfig.API_KEY,
             BuildConfig.APP_ID, Domain.VOICE_SEARCH, Languages.ENGLISH, Logging.TRUE);
        voiceSearch.setNoInputTimeout(5);
        voiceSearch.setSilence(2);
        voiceSearch.setTimeout(5);
        Log.d("VOICE", "onCreate: " + Domain.VOICE_SEARCH);
        Log.d("VOICE", "onCreate: " + Languages.ENGLISH);
        voiceSearch.setListener(new VoiceInputListener() {
            @Override
            public void onResult(VoiceInputResultData result) {
                Log.d(TAG, "onResult: " + result);
                outputTv.setText(result.toString());
            }

            @Override
            public void onError(VoiceInputErrorResponseData error) {
             //   outputTv.setText(error.toString() + error.getCode());

            }

            @Override
            public void onRecordingStart(boolean isStart) {

            }

            @Override
            public void onRecordingEnd(boolean isEnd) {

            }


        });

        searchBtn.setOnClickListener(view -> {
            outputTv.setText("");


            voiceSearch.startRecognition(
                    getApplicationContext(),
                    true
            );


        });
        stopBtn.setOnClickListener(view -> voiceSearch.cancel());
        searchBtnWithoutUi.setOnClickListener(view -> {
            voiceSearch.startRecognition(getApplicationContext(), false, Domain.VOICE_SEARCH, Languages.ENGLISH);
        });
    }
}