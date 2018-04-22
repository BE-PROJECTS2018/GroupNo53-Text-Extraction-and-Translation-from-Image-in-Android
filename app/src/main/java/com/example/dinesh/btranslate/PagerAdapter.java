package com.example.dinesh.btranslate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dinesh.btranslate.People.CameraFragment;
import com.example.dinesh.btranslate.People.FilesFragment;
import com.example.dinesh.btranslate.People.TranslationFragment;

/**
 * Created by dinesh on 02-02-2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CameraFragment tab1 = new CameraFragment();
                return tab1;
            case 1:
                FilesFragment tab2 = new FilesFragment();
                return tab2;
            case 2:
                TranslationFragment tab3 = new TranslationFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
