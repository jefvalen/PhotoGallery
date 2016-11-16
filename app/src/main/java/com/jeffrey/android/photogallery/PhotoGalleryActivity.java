package com.jeffrey.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){ //ADD NEW INTENT PAGE 481
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    public Fragment createFragment(){
        return PhotoGalleryFragment.newInstance();
    }

}
