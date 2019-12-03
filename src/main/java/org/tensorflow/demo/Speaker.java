package org.tensorflow.demo;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class Speaker implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;

    public Speaker(Context context){
        this.tts = new TextToSpeech(context, this);

    }

    public void speakOut(String text) {
        this.tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }
    public void firstSpeak(String text){
        this.tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result;
            String lang=Locale.getDefault().getLanguage();

            result = tts.setLanguage(Locale.US);


            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                firstSpeak("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void close(){
        if(this.tts != null){
            this.tts.stop();
            this.tts.shutdown();
        }
    }

}
