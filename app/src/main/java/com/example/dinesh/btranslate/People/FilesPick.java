package com.example.dinesh.btranslate.People;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class FilesPick extends AppCompatActivity {
    File pdfFile;
    private ArrayList<Object> docPaths;
    String parsedText="";
    public static  final String server_login_url="http://sagar.pythonanywhere.com/postjson";
    private String hindiText;
    public Dialog dialog;
    EditText PDFName;
    Button Okclick,CancelClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_pick);
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case FilePickerConst.REQUEST_CODE_DOC:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    readPDF(docPaths.get(0).toString());
                }
                break;
        }
    }
    public void readPDF(String pdfurl){
        try{
            PdfReader reader = new PdfReader(pdfurl);
            int n = reader.getNumberOfPages();
            for(int i=0;i<n;i++){
                parsedText = parsedText+ PdfTextExtractor.getTextFromPage(reader,i+1).replaceAll("/\\r?\\n|\\r/g","\n")+" ";
            }
            Log.e("pdfcontents",parsedText);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TranslatePDF(parsedText);
    }
    //<--------------------Divide PDF into parts---------->
    public void TranslatePDF(String parsedText){
        String[] text=parsedText.split("\n");
        String hindi="";
        for(int i =0;i<text.length;i++) {
            makeJsonObjectRequest(text[i]);
        }
        createPDF();
    }

    private void makeJsonObjectRequest(String parsedText) {
        final ProgressDialog pd = new ProgressDialog(FilesPick.this);
        pd.setMessage("loading");
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap();
        params.put("text", parsedText);
        params.put("language", "hi");
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, server_login_url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    hindiText=response.getString("name");
                    AppConstants.PDFTranslate+=hindiText;
        } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pd.dismiss();
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);
    }


    public void createPDF(){
        dialog= new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("PDF Name");
        dialog.show();

        PDFName=(EditText)dialog.findViewById(R.id.pdfName);
        Okclick=(Button)dialog.findViewById(R.id.Okclick);
        CancelClick=(Button)findViewById(R.id.Cancelclick);
    }

    public void OKclick_onclick(View v) throws FileNotFoundException, DocumentException {
        String pdfName=PDFName.getText().toString();
        File docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/BtranslatePDFs");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("PDF", "Created a new directory for PDF");
        }
        pdfFile = new File(docsFolder.getAbsolutePath(),pdfName+".pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
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
        document.add(new Paragraph(AppConstants.PDFTranslate,f));
        document.close();
        dialog.dismiss();
    }
    public void Cancelclick_onclick(View v){
        dialog.dismiss();
    }

}
