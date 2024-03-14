package com.reverie.voice_input

import android.util.Log

public class LOG {
    companion object{
       public var DEBUG=false
      internal  fun customLogger(tag:String, s:String)
        {
            if(DEBUG)
            {
                Log.d(tag,s);

            }

        }

    }
}

