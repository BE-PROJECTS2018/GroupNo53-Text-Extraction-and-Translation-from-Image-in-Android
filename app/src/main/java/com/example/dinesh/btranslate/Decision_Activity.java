package com.example.dinesh.btranslate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dinesh.btranslate.Blind.BlindCamera;
import com.example.dinesh.btranslate.People.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Decision_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {


    private TextToSpeech tts;
    static String response="";
    int flag=1;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this,this);
        sharedPreferences = getSharedPreferences("usertype", Context.MODE_PRIVATE);



    }
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void decisionactivity() {
        if(response.equalsIgnoreCase("yes" )
                || response.equalsIgnoreCase("ha")
                || response.equalsIgnoreCase("haan")
                || response.equalsIgnoreCase("hmm")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("type","1");
            editor.apply();
            Intent intent = new Intent(Decision_Activity.this,BlindCamera.class);
            Toast.makeText(this, "Blind Activity", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }else if(response.equalsIgnoreCase("no" )
                || response.equalsIgnoreCase("na")
                || response.equalsIgnoreCase("nahi")
                || response.equalsIgnoreCase("nah")){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("type","2");
            editor.apply();
            Intent intent = new Intent(Decision_Activity.this,MainActivity.class);
            Toast.makeText(this, "People Activity", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }else{
            promptSpeechInput();
            decisionactivity();
        }
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, 10);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Exception",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    response=result.get(0);
                    decisionactivity();
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    private void speakOut() {
        String text = "क्या आप शारीरिक रूप से विकलांग हैं";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               promptSpeechInput();
           }
       },8000);

    }


}
