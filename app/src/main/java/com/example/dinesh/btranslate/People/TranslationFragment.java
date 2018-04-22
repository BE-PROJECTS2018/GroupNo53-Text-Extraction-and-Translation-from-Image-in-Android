package com.example.dinesh.btranslate.People;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dinesh.btranslate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dinesh on 02-02-2018.
 */

public class TranslationFragment extends Fragment {
    TextView textView;
    EditText editText;
    Button btranslate,Speech;
    Spinner spinner;
    String Language;
    public static  final String server_login_url="http://sagar.pythonanywhere.com/postjson";

    public TranslationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.translation_fragment, container, false);
        textView=v.findViewById(R.id.textView);
        editText=v.findViewById(R.id.editText);
        btranslate=v.findViewById(R.id.bButton);

        Speech=v.findViewById(R.id.Speech);
        spinner=v.findViewById(R.id.spinner);

        addItemsOnSpinner();
        //addListenerOnSpinnerItemSelection();
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
        return v;
    }
    //<---------------------Translate Text-------------->

    private void makeJsonObjectRequest() {
        String inputText=editText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Map<String, String> params = new HashMap();
        params.put("text", inputText);
        params.put("language", Language);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, server_login_url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView.setText(response.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO: handle success
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                textView.setText(error.getMessage());
                }
        });
        Volley.newRequestQueue(getActivity()).add(jsonRequest);
    }

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

    private void addItemsOnSpinner() {
        List<String> list = new ArrayList<String>();
        list.add("Hindi");
        list.add("Gujrati");
        list.add("Marathi");
        list.add("Arabic");
        list.add("French");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
}
