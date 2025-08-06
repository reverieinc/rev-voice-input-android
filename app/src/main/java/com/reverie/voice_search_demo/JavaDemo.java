package com.reverie.voice_search_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    Button FinishBtn, stopBtn, searchBtnWithoutUi,searchBtnWithUi;
    //    RevVoiceInput voiceSearch;
    RevVoiceInput voiceInput;

    TextView outputTv;
    private static final String TAG = "JavaDemoTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }
        FinishBtn = findViewById(R.id.searchBtn);
        searchBtnWithUi=findViewById(R.id.searchBtnwithUi);
        stopBtn = findViewById(R.id.stopBtn);
        outputTv = findViewById(R.id.outputTv);
        searchBtnWithoutUi = findViewById(R.id.searchBtnwithoutUi);
        LOG.Companion.setDEBUG(false);// implement customlogger

        voiceInput = new RevVoiceInput(
                BuildConfig.API_KEY,
                BuildConfig.APP_ID, Domain.INDUS_APP_SEARCH, Languages.ENGLISH, Logging.FALSE);
        voiceInput.setNoInputTimeout(5.0);
        voiceInput.setSilence(0.7);
        voiceInput.setTimeout(45.0);
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
                Log.e(TAG, "onError full: " + error.toString());

            }

            @Override
            public void onRecordingStart(boolean isStart) {
                Log.d(TAG, "onRecordingStart: ");
            }

            @Override
            public void onRecordingEnd(boolean isEnd) {
                Log.d(TAG, "onRecordingEnd1: ");
            }


        });
        FinishBtn.setText("FinishInput");
        FinishBtn.setOnClickListener(view -> {
            outputTv.setText("");


//            voiceInput.startRecognition(
//                    getApplicationContext(),
//                    true
//            );
            voiceInput.finishInput();


        });
        searchBtnWithUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceInput.startRecognition(getApplicationContext(), true, Domain.VOICE_SEARCH, Languages.ENGLISH);
            }
        });
        stopBtn.setText("Cancel");
        stopBtn.setOnClickListener(view -> voiceInput.cancel());
        searchBtnWithoutUi.setOnClickListener(view -> {
            voiceInput.startRecognition(getApplicationContext(), false, Domain.VOICE_SEARCH, Languages.ENGLISH);
        });
    }
}