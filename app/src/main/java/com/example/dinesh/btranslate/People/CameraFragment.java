package com.example.dinesh.btranslate.People;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dinesh.btranslate.R;

/**
 * Created by dinesh on 02-02-2018.
 */

public class CameraFragment extends Fragment {
    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.camera_fragment, container, false);
    }
}
