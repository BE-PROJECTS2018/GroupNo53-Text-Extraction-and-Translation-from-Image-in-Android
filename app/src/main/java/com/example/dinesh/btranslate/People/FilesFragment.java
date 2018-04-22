package com.example.dinesh.btranslate.People;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinesh.btranslate.R;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by dinesh on 02-02-2018.
 */

public class FilesFragment extends Fragment {
    View v;
    public static final int Gallery_code = 888;
    String stringPath;
    String text,timeStamp;
    private File pdfFile;
    private MenuItem GalleryButton;
    private List<PDF> PDFList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PDFAdaptor pdfAdaptor;
    String [] pdfname;
    String [] pdfpath;




     File path;

     static ArrayList<String> pdf_paths=new ArrayList<String>();
     static ArrayList<String> pdf_names=new ArrayList<String>();
    private String pdfurl;
    private SensorManager sensorManager;
    private String copiedText="";
    private Dialog dialog;
    TextView copyText;
    Button Translate,Cancel;

    public FilesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.files_fragment, container, false);
        setHasOptionsMenu(true);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        pdfAdaptor = new PDFAdaptor(PDFList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

// set the adapter
        recyclerView.setAdapter(pdfAdaptor);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PDF pdf = PDFList.get(position);

                previewPdf(pdf.getPDFname());

                //Toast.makeText(getContext(), pdf.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        prepareMovieData();

        return v;
        }


    private void prepareMovieData() {
        pdf_paths.clear();
        pdf_names.clear();
        path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +"/BtranslatePDFs");
        searchFolderRecursive1(path);
        pdfname = pdf_names.toArray(new String[pdf_names.size()]);

        pdfpath = pdf_paths.toArray(new String[pdf_paths.size()]);
        for(int i=0;i<pdfname.length;i++){
            PDF pdf = new PDF(pdfname[i]);
            PDFList.add(pdf);
        }

        pdfAdaptor.notifyDataSetChanged();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_files, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.galleryAdd:
                Intent i = new Intent(getContext(),ImagePick.class);
                startActivity(i);


               /*Intent intent = new Intent(getContext(), GalleryActivity.class);
                Params params = new Params();
                params.setCaptureLimit(10);
                params.setPickerLimit(10);
                intent.putExtra(Constants.KEY_PARAMS, params);
                super.startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
                */

                return true;

            case R.id.action_file:

                Intent intent=new Intent(getContext(),FilesPick.class);
                startActivity(intent);



                return false;
            default:
                break;
        }

        return false;
    }
    /* public void getTextfromImage(Bitmap bitmap) {
            Log.e("ocr", "method call");
            //Toast.makeText(this, "Bitmap done", Toast.LENGTH_SHORT).show();
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();

            if (!textRecognizer.isOperational()) {
                Toast.makeText(getActivity(), "could not get the text", Toast.LENGTH_SHORT).show();
            } else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    sb.append(myItem.getValue());
                    sb.append("\n");
                }
                text = sb.toString();
                Log.e("Text Extracted", text);
                AppConstants.DetectedText = text;
                Toast.makeText(getActivity(), AppConstants.DetectedText, Toast.LENGTH_SHORT).show();

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

        }*/
    private void previewPdf(String pdfurl) {

        PackageManager packageManager = getActivity().getPackageManager();
        File docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/BtranslatePDFs");

        pdfFile = new File(docsFolder.getAbsolutePath(),pdfurl);
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
            Toast.makeText(getActivity(),"Download a PDF Viewer to see the generated PDF",Toast.LENGTH_SHORT).show();
        }
    }


      private static void searchFolderRecursive1(File folder)
        {
            if (folder != null)
            {
                if (folder.listFiles() != null)
                {
                    for (File file : folder.listFiles())
                    {
                        if (file.isFile())
                        {
                            //.pdf files
                            if(file.getName().contains(".pdf"))
                            {
                                Log.e("ooooooooooooo", "path__="+file.getName());
                                file.getPath();
                                pdf_names.add(file.getName());
                                pdf_paths.add(file.getPath());
                                Log.e("pdf_paths", ""+pdf_names);
                            }
                        }
                        else
                        {
                            searchFolderRecursive1(file);
                        }
                    }
                }
            }
        }}



