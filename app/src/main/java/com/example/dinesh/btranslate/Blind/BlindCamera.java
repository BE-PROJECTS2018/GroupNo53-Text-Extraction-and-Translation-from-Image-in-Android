package com.example.dinesh.btranslate.Blind;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinesh.btranslate.AppConstants;
import com.example.dinesh.btranslate.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BlindCamera extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextToSpeech ctts;
    String mCurrentPhotoPath;
    Bitmap bitmap;
    String response;
    String text;
    TextView textView;
    CharSequence brfText;
    String brffilepath;
    File pdfFile;
    String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_camera);
        ctts = new TextToSpeech(this,this);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            int result = ctts.setLanguage(new Locale("hin"));

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
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (ctts != null) {
            ctts.stop();
            ctts.shutdown();
        }
        super.onDestroy();
    }

//1
    private void speakOut() {

        String Camera_started = "आपका मोबाइल फोन कैमरा शुरू हो गया है";
         final String align_camera = "अपने दस्तावेज़ को कैमरे के नीचे रखें";
         final String click_image="जब भी आप तैयार हों तो मध्य बटन दबाएं";


        ctts.speak(Camera_started, TextToSpeech.QUEUE_FLUSH, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ctts.speak(align_camera, TextToSpeech.QUEUE_FLUSH, null);

            }
        },5000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ctts.speak(click_image, TextToSpeech.QUEUE_FLUSH, null);

            }
        },5000);

        camera_on();


    }
//2
    private void camera_on() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Here we are Capturing image
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(BlindCamera.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

            }
            startActivityForResult(intent, 1100);
        }
    }
//3
    private File createImageFile() throws IOException {
        // Create an image file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("path",mCurrentPhotoPath);
        return image;
    }
    //7
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
    private void promptSpeechBrfinput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Exception",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //4,8
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    response=result.get(0);
                    hindiTranslateDecision();
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                }
                break;
            case 1100:
                        try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(mCurrentPhotoPath));
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mCurrentPhotoPath)));
                        } catch (IOException e) {
                                e.printStackTrace();
                            }
                // Toast.makeText(ocrbActivity, mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                //
                        bitmap= BitmapFactory.decodeFile(mCurrentPhotoPath);
                        getTextfromImage(bitmap);
                        break;
            case 100:       if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                response=result.get(0);
                brfdecisionactivity();
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }
    //5
    public void getTextfromImage(Bitmap bitmap){
        Log.e("ocr","method call");
        Toast.makeText(this, "Bitmap done", Toast.LENGTH_SHORT).show();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(this, "could not get the text", Toast.LENGTH_SHORT).show();
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for(int i=0;i< items.size();i++){
                TextBlock myItem= items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }
            text=sb.toString();
            Log.e("Text Extracted",text);
            AppConstants.DetectedText=text;
           // languageinput();

            try {
                createPdf();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/BtranslatePDFs");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("PDF", "Created a new directory for PDF");
        }


        pdfFile = new File(docsFolder.getAbsolutePath(),"Document_"+timeStamp+".pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        BaseFont urName = null;
        try {
            urName = BaseFont.createFont("assets/fonts/Swell-Braille.ttf", "UTF-8",BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font font2 = new Font(urName, 40);
        // Font font2 = new Font(baseFont, 12);

        //document.add(new Paragraph("Custom Braille Font: ", font2));

        document.add(new Paragraph(AppConstants.DetectedText,font2));

        document.close();
        previewPdf();

    }
    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(this,"Download a PDF Viewer to see the generated PDF",Toast.LENGTH_SHORT).show();
        }
    }

    //6
    public void languageinput(){
        String langoption="क्या आप इसे हिंदी में अनुवाद करना चाहते हैं?";

        ctts.speak(langoption,TextToSpeech.QUEUE_FLUSH,null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                promptSpeechInput();
            }
        },1500);
    }

    private void brfdecisionactivity() {
        if(response.equalsIgnoreCase("yes" )
                || response.equalsIgnoreCase("ha")
                || response.equalsIgnoreCase("haan")
                || response.equalsIgnoreCase("hmm")){
            Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Swell-Braille.ttf");
            textView.setTypeface(typeface);
            textView.setText(text);
            brfText=textView.getText();

            Log.e("Brf Text Extracted",brfText.toString());
            createFile(brfText,this);

        }else if(response.equalsIgnoreCase("no" )
                || response.equalsIgnoreCase("na")
                || response.equalsIgnoreCase("nahi")
                || response.equalsIgnoreCase("nah")){

            brfinput();
        }

    }

    private void hindiTranslateDecision() {
        if(response.equalsIgnoreCase("yes" )
                || response.equalsIgnoreCase("ha")
                || response.equalsIgnoreCase("haan")
                || response.equalsIgnoreCase("hmm")){
            Intent i=new Intent(BlindCamera.this,HindiTranslate.class);
            i.putExtra("englishText",text);
            startActivity(i);
           //hindiTranslation

        }else if(response.equalsIgnoreCase("no" )
                || response.equalsIgnoreCase("na")
                || response.equalsIgnoreCase("nahi")
                || response.equalsIgnoreCase("nah")){

            brfinput();
        }

    }

    private void createFile(CharSequence brfText,Context context) {
        String brftext=brfText.toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BRF_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
           File brffile = File.createTempFile(
                    imageFileName,  // prefix
                    ".brf",         // suffix
                    storageDir      // directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("imageFileName.brf", Context.MODE_PRIVATE));
            outputStreamWriter.write(brftext);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        // Save a file: path for use with ACTION_VIEW intents
        brffilepath = image.getAbsolutePath();

        Log.e("path",mCurrentPhotoPath);

    }

    private void decisionactivity() {
        if(response.equalsIgnoreCase("yes" )
                || response.equalsIgnoreCase("ha")
                || response.equalsIgnoreCase("haan")
                || response.equalsIgnoreCase("hmm")){

        }else if(response.equalsIgnoreCase("no" )
                || response.equalsIgnoreCase("na")
                || response.equalsIgnoreCase("nahi")
                || response.equalsIgnoreCase("nah")){

            brfinput();
        }

    }

    void brfinput(){
        String langoption="क्या आप ब्रेल फ़ाइल डाउनलोड करना चाहते हैं?";

        ctts.speak(langoption,TextToSpeech.QUEUE_FLUSH,null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                promptSpeechBrfinput();
            }
        },1500);
    }

    /**
     * Created by dinesh on 02-03-2018.
     */
}
