package com.example.dinesh.btranslate.Blind;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HindiTranslate extends AppCompatActivity implements TextToSpeech.OnInitListener{
    String EnglishText;
    private TextToSpeech ctts;
    String hindiText;
    public static  final String server_login_url="http://sagar.pythonanywhere.com/postjson";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        EnglishText=intent.getExtras().getString("englishText");
        ctts = new TextToSpeech(this,this);
        makeJsonObjectRequest();
        speakout();
    }

    private void speakout() {
        ctts.speak(hindiText,TextToSpeech.QUEUE_FLUSH,null);
    }

    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            int result = ctts.setLanguage(new Locale("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (ctts != null) {
            ctts.stop();
            ctts.shutdown();
        }
        super.onDestroy();
    }

    private void makeJsonObjectRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap();
        params.put("text", EnglishText);
        params.put("language", "hi");
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, server_login_url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    hindiText=response.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hindiText="क्षमा करें कुछ गलत हो गया होगा";

            }
        });


        Volley.newRequestQueue(this).add(jsonRequest);


    }


}
