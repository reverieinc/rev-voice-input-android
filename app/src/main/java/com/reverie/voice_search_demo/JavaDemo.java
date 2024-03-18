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

import com.rev.voice_input_demo.BuildConfig;
import com.rev.voice_input_demo.R;
import com.reverie.voiceinput.LOG;
import com.reverie.voiceinput.RevVoiceInput;
import com.reverie.voiceinput.VoiceInputListener;
import com.reverie.voiceinput.business.VoiceInputErrorResponseData;
import com.reverie.voiceinput.business.VoiceInputResultData;
import com.reverie.voiceinput.utilities.constants.Domain;
import com.reverie.voiceinput.utilities.constants.Languages;
import com.reverie.voiceinput.utilities.constants.Logging;


public class JavaDemo extends AppCompatActivity {
    Button searchBtn, stopBtn, searchBtnWithoutUi;
    //    RevVoiceInput voiceSearch;
    RevVoiceInput voiceInput;

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
        LOG.Companion.setDEBUG(false);// implement customlogger

        voiceInput = new RevVoiceInput(
                BuildConfig.API_KEY,
                BuildConfig.APP_ID, Domain.VOICE_SEARCH, Languages.ENGLISH, Logging.TRUE);
        voiceInput.setNoInputTimeout(5);
        voiceInput.setSilence(2);
        voiceInput.setTimeout(5);
        Log.d("VOICE", "onCreate: " + Domain.VOICE_SEARCH);
        Log.d("VOICE", "onCreate: " + Languages.ENGLISH);
        Log.d("VOICE", "BuildConfig.APP_ID: " + BuildConfig.APP_ID);
        voiceInput.setListener(new VoiceInputListener() {
            @Override
            public void onResult(VoiceInputResultData result) {
                Log.d(TAG, "onResult: " + result);
                outputTv.setText(result.toString());
            }

            @Override
            public void onError(VoiceInputErrorResponseData error) {
                //   outputTv.setText(error.toString() + error.getCode());
                Log.e(TAG, "onError full: "+error.toString());

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


            voiceInput.startRecognition(
                    getApplicationContext(),
                    true
            );


        });
        stopBtn.setOnClickListener(view -> voiceInput.cancel());
        searchBtnWithoutUi.setOnClickListener(view -> {
            voiceInput.startRecognition(getApplicationContext(), false, Domain.VOICE_SEARCH, Languages.ENGLISH);
        });
    }
}