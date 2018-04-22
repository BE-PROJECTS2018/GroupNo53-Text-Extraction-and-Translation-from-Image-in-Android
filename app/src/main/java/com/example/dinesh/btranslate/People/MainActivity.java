package com.example.dinesh.btranslate.People;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dinesh.btranslate.AppConstants;
import com.example.dinesh.btranslate.Blind.BlindCamera;
import com.example.dinesh.btranslate.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.vlk.multimager.activities.MultiCameraActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button Camera, Gallery;
    public static final int Gallery_code = 888;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    String mCurrentPhotoPath;
    Bitmap bitmap;
    private String text="";
    Bitmap[] bitmapimages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Optic Translator");
        setSupportActionBar(toolbar);
        //<---------For android 5+ permissions to acces camera--->
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        final com.example.dinesh.btranslate.PagerAdapter pagerAdapter=new com.example.dinesh.btranslate.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1,true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){

                     camera_on();
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(1);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
//<------------------To open Camera using Multimager Library to capture multiple images---->
    private void camera_on() {
        Intent intent = new Intent(MainActivity.this, MultiCameraActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
    }
    //<----------Get Results-------->

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //<-----Try Catch to handle unexpected Error------------>
        try {
            switch (requestCode) {
                case Constants.TYPE_MULTI_CAPTURE:
                    ArrayList<Image> imagesList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                    bitmapimages = new Bitmap[imagesList.size()];
                    for (int i = 0; i < imagesList.size(); i++) {
                        Log.e("Image path", imagesList.get(i).imagePath);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:" + imagesList.get(i).imagePath));
                            bitmapimages[i] = bitmap;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < imagesList.size(); i++) {
                        String result = getTextfromImage(bitmapimages[i]);
                        text = text + "/n" + result;
                    }
                    Log.e("Total text", text);
                    AppConstants.DetectedText = text;
                    Intent intent = new Intent(MainActivity.this, PTranslate.class);
                    startActivity(intent);
                    break;
            }
        }catch (Exception e){
            Toast.makeText(this, "Something happen wrong!!!", Toast.LENGTH_SHORT).show();
        }
    }
//<-----------Run OCR on each Image--------------->

    public String getTextfromImage(Bitmap bitmap){
        Log.e("ocr","method call");
        Toast.makeText(this, "Bitmap done", Toast.LENGTH_SHORT).show();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(this, "could not get the text", Toast.LENGTH_SHORT).show();
            return "could not get the text";
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder sb = new StringBuilder();
            for(int i=0;i< items.size();i++){
                TextBlock myItem= items.valueAt(i);
                sb.append(myItem.getValue());
            }
            text=sb.toString();

            return  text;
        }
    }
}
