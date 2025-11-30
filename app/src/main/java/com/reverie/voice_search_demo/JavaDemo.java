package com.reverie.voice_search_demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

    Button finishBtn, stopBtn, searchBtnWithoutUi, searchBtnWithUi;
    RevVoiceInput voiceInput;
    TextView outputTv;
    CheckBox useTokenCheckbox;

    private static final String TAG = "JavaDemoTest";


    private static final String STT_TOKEN = "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI1MmVlMTA0NS01MTYyLTQwZDMtODZhZC01YTQ5NjJlOGU4MDciLCJzdWIiOiJyZXYuc3R0X3N0cmVhbV9tb25pdG9yaW5nIiwiaWF0IjoxNzY0NDMzNDQ4LCJleHAiOjE3NjQ0MzUyNDh9.oHUyF-NRZozO7NLDscqysCqIv1TklwHAI-sBYlLbD-IVUU99QftQ93Tphjd0NijjwBZdFK7GKanQs6qibEZICg"; // hardcoded token for testing
//    private static final String STT_TOKEN = "e22yJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI1MmVlMTA0NS01MTYyLTQwZDMtODZhZC01YTQ5NjJlOGU4MDciLCJzdWIiOiJyZXYuc3R0X3N0cmVhbV9tb25pdG9yaW5nIiwiaWF0IjoxNzY0NDMzNDQ4LCJleHAiOjE3NjQ0MzUyNDh9.oHUyF-NRZozO7NLDscqysCqIv1TklwHAI-sBYlLbD-IVUU99QftQ93Tphjd0NijjwBZdFK7GKanQs6qibEZICg"; // hardcoded token for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    100
            );
        }

        finishBtn = findViewById(R.id.searchBtn);
        searchBtnWithUi = findViewById(R.id.searchBtnwithUi);
        stopBtn = findViewById(R.id.stopBtn);
        outputTv = findViewById(R.id.outputTv);
        searchBtnWithoutUi = findViewById(R.id.searchBtnwithoutUi);
        useTokenCheckbox = findViewById(R.id.useTokenCheckbox);

        LOG.Companion.setDEBUG(true); // implement customlogger

        Log.d("VOICE", "onCreate: " + Domain.VOICE_SEARCH);
        Log.d("VOICE", "onCreate: " + Languages.ENGLISH);
        Log.d("VOICE", "BuildConfig.APP_ID: " + BuildConfig.APP_ID);

        finishBtn.setText("Finish Input");
        finishBtn.setOnClickListener(view -> {
            outputTv.setText("");
            if (voiceInput != null) {
                voiceInput.finishInput();
            }
        });

        searchBtnWithUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean useToken = useTokenCheckbox.isChecked();
                voiceInput = buildVoiceInput(useToken);
                voiceInput.startRecognition(
                        getApplicationContext(),
                        true,
                        Domain.VOICE_SEARCH,
                        Languages.ENGLISH
                );
            }
        });

        stopBtn.setText("Cancel");
        stopBtn.setOnClickListener(view -> {
            if (voiceInput != null) {
                voiceInput.cancel();
            }
        });

        searchBtnWithoutUi.setOnClickListener(view -> {
            boolean useToken = useTokenCheckbox.isChecked();
            voiceInput = buildVoiceInput(useToken);
            voiceInput.startRecognition(
                    getApplicationContext(),
                    false,
                    Domain.VOICE_SEARCH,
                    Languages.ENGLISH
            );
        });
    }

    /**
     * Helper to build a RevVoiceInput instance in either:
     *  - legacy apiKey/appId mode
     *  - new token-based mode
     */
    private RevVoiceInput buildVoiceInput(boolean useToken) {
        RevVoiceInput vi;

        if (useToken) {
            // NEW: token constructor – requires you to have added it in RevVoiceInput
            vi = new RevVoiceInput(
                    STT_TOKEN,
                    Domain.INDUS_APP_SEARCH,
                    Languages.ENGLISH,
                    Logging.FALSE
            );
            Log.d(TAG, "Using TOKEN-based auth");
        } else {
            // Legacy apiKey/appId constructor – unchanged
            vi = new RevVoiceInput(
                    BuildConfig.API_KEY,
                    BuildConfig.APP_ID,
                    Domain.INDUS_APP_SEARCH,
                    Languages.ENGLISH,
                    Logging.FALSE
            );
            Log.d(TAG, "Using API_KEY/APP_ID auth");
        }

        vi.setNoInputTimeout(5.0);
        vi.setSilence(0.7);
        vi.setTimeout(45.0);

        vi.setListener(new VoiceInputListener() {
            @Override
            public void onResult(VoiceInputResultData result) {
                Log.d(TAG, "onResult: " + result);
                outputTv.setText(result.toString());
            }

            @Override
            public void onError(VoiceInputErrorResponseData error) {
                Log.e(TAG, "onError full: " + error.toString());
//                outputTv.setText(error.toString());
            }

            @Override
            public void onRecordingStart(boolean isStart) {
                Log.d(TAG, "onRecordingStart: " + isStart);
            }

            @Override
            public void onRecordingEnd(boolean isEnd) {
                Log.d(TAG, "onRecordingEnd: " + isEnd);
            }
        });

        return vi;
    }
}
