package com.example.dinesh.btranslate.People;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dinesh.btranslate.AppConstants;
import com.example.dinesh.btranslate.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PTranslate extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextView textView;
    TextView editText;
    EditText PDFName;
    Button Okclick,CancelClick;
    Button btranslate,pdfTranslate,braille,voice;
    Spinner spinner;
    String Language,hindiText;
    public static  final String server_login_url="http://sagar.pythonanywhere.com/postjson";
    private File pdfFile;
    public Dialog dialog;
    Toolbar toolbar;
    Boolean flag=true;
    private TextToSpeech ctts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptranslate);
        //<---------Text to Speech------->
        ctts = new TextToSpeech(this,this);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Translate Text");
        setSupportActionBar(toolbar);

        textView=(TextView)findViewById(R.id.textView);
        editText=(TextView)findViewById(R.id.editText);
        btranslate=(Button)findViewById(R.id.bButton);
        spinner=(Spinner)findViewById(R.id.spinner);
        pdfTranslate=(Button)findViewById(R.id.pdfbutton);
        braille=(Button)findViewById(R.id.braillebutton);
        voice=(Button)findViewById(R.id.voice);

        editText.setText(AppConstants.DetectedText);

        addItemsOnSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String items=spinner.getSelectedItem().toString();
                DetectCode(items);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btranslate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                makeJsonObjectRequest();
            }
        });
    }
//<-------------------------------English To Hindi from Server---------->
    private void makeJsonObjectRequest() {
        final ProgressDialog pd = new ProgressDialog(PTranslate.this);
        pd.setMessage("loading");
        pd.show();
        String inputText=editText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap();
        params.put("text", inputText);
        params.put("language", Language);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, server_login_url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    hindiText=response.getString("name");
                    textView.setText(response.getString("name"));
                    pdfTranslate.setVisibility(View.VISIBLE);
                    braille.setVisibility(View.VISIBLE);
                    voice.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                textView.setText(error.getMessage());
                pd.dismiss();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }
    //<--------Create Braille PDF using Flag variable---->
    public void createbraille(View v){
        flag=true;
        dialog= new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("PDF Name");
        dialog.show();

        PDFName=(EditText)dialog.findViewById(R.id.pdfName);
        Okclick=(Button)dialog.findViewById(R.id.Okclick);
        CancelClick=(Button)findViewById(R.id.Cancelclick);
    }
//<---------------Dectect Language from Spinner and get Language Code--->
    private void DetectCode(String item){
        switch (item){
            case "Hindi":Language="hi";
                break;
            case "Gujrati":Language="gu";
                break;
            case "Marathi":Language="mr";
                break;
            case "Arabic":Language="ar";
                break;
            case "French":Language="fr";
                break;
        }
    }
            //<---get Item from Spinner--->
    private void addItemsOnSpinner() {
        List<String> list = new ArrayList<String>();
        list.add("Hindi");
        list.add("Gujrati");
        list.add("Marathi");
        list.add("Arabic");
        list.add("French");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    //<------------Create Hindi PDF using Flag------>

    public void createPdf(View v) {
        flag=false;
       dialog= new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("PDF Name");
        dialog.show();

        PDFName=(EditText)dialog.findViewById(R.id.pdfName);
        Okclick=(Button)dialog.findViewById(R.id.Okclick);
        CancelClick=(Button)findViewById(R.id.Cancelclick);
    }
    //<--Dialog Box OK button Click Listener----->

    public void OKclick_onclick(View v) throws FileNotFoundException, DocumentException{
        String pdfName=PDFName.getText().toString();
        File docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/BtranslatePDFs");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }

        pdfFile = new File(docsFolder.getAbsolutePath(),pdfName+".pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        if(flag){
            BaseFont urName = null;
            try {
                urName = BaseFont.createFont("assets/fonts/Swell-Braille.ttf", "UTF-8",BaseFont.EMBEDDED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Font font2 = new Font(urName, 40);
            document.add(new Paragraph(AppConstants.DetectedText,font2));
        }else {
            try{
                File file = new File(getFilesDir(), "FreeSans.ttf");
                if (file.length() == 0) {
                    InputStream fs = getAssets().open("FreeSans.ttf");
                    FileOutputStream os = new FileOutputStream(file);
                    int i;
                    while ((i = fs.read()) != -1) {
                        os.write(i);
                    }
                    os.flush();
                    os.close();
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Font f = FontFactory.getFont(getFilesDir() + "/" + "FreeSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            document.add(new Paragraph(hindiText,f));
        }
        document.close();
        dialog.dismiss();
        Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void Cancelclick_onclick(View v){
        dialog.dismiss();
    }
    //<------Text To Speech INIT method----->
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            int result = ctts.setLanguage(new Locale("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakout();
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
    //<------ Text to be Spoken----------->
    private void speakout(){
        float ptch=0.6f;
        ctts.speak(hindiText,TextToSpeech.QUEUE_FLUSH,null);
        ctts.setPitch(ptch);
        ctts.setSpeechRate(0.5f);
    }
    //<---------Onclick for Button hindiSpeech----------->
    public void hindispeech(View v){
       speakout();
    }
}
