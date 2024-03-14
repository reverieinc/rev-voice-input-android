package com.reverie.voice_search_demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rev.voice_search_demo.BuildConfig
import com.rev.voice_search_demo.R
import com.reverie.voice_input.LOG
import com.reverie.voice_input.utilities.constants.Domain
import com.reverie.voice_input.utilities.constants.Languages
import com.reverie.voice_input.utilities.constants.Logging



class MainActivity : AppCompatActivity() {
    private lateinit var searchBtn: Button
    private lateinit var outputTv: TextView
    private lateinit var stopBtn: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java_demo)
//        LOG.DEBUG=false
//        setContentView(R.layout.activity_java_demo)
//        searchBtn = findViewById(R.id.searchBtn)
//        stopBtn = findViewById(R.id.stopBtn)
//        outputTv = findViewById(R.id.outputTv)
//        var searchBtnWithoutUi = findViewById<Button>(R.id.searchBtnwithoutUi)
//
//        searchBtn.setOnClickListener { view: View? ->
//            outputTv.text = ""
//            voiceSearch.startRecognition(
//                applicationContext,
//                true
//            )
//        }
//        stopBtn.setOnClickListener { view: View? -> voiceSearch.stopRecognition() }
//        searchBtnWithoutUi.setOnClickListener(View.OnClickListener { view: View? ->
//            //  outputTv.setText("");
//            voiceSearch?.startRecognition(
//                applicationContext,
//                false,
//                Domain.VOICE_SEARCH,
//                Languages.ENGLISH
//            )
//        })
//

    }
}
