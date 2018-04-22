package com.example.dinesh.btranslate.People;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.dinesh.btranslate.AppConstants;
import com.example.dinesh.btranslate.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.io.IOException;
import java.util.ArrayList;

public class ImagePick extends AppCompatActivity {

    private Bitmap[] bitmapimages;
    Bitmap bitmap;
    String text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pick);
        Intent intent = new Intent(ImagePick.this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setPickerLimit(10);
        intent.putExtra(Constants.KEY_PARAMS, params);
        super.startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.TYPE_MULTI_PICKER:
                ArrayList<Image> imagesList = data.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
                bitmapimages=new Bitmap[imagesList.size()];
                for(int i=0;i<imagesList.size();i++){
                    Log.e("Image path",imagesList.get(i).imagePath);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file:"+imagesList.get(i).imagePath));
                        bitmapimages[i]=bitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (int i=0;i<imagesList.size();i++){
                    String result =getTextfromImage(bitmapimages[i]);
                    text=text+"/n"+result;
                }
                Log.e("Total text",text);
                AppConstants.DetectedText=text;
                Intent intent=new Intent(ImagePick.this,PTranslate.class);
                startActivity(intent);
                break;
        }
    }
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
