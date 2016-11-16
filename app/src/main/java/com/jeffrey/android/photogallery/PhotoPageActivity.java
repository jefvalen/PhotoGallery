package com.jeffrey.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

public class PhotoPageActivity extends SingleFragmentActivity { //CREATING WEB ACTIVITY, PAGE 517

    PhotoPageFragment fragment;


    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        fragment = PhotoPageFragment.newInstance(getIntent().getData());
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override   //HIER WORDT DE BACKBUTTON INGEDRUKT DUS HIER MOET HET IN WERKING GESTELD WORDEN
    public void onBackPressed() {
        if (fragment.canGoBack()) { //DEZE VERWIJST NAAR HET JUISTE FRAGMENT -> PHOTOPAGEFRAGMENT EN ROEPT DAAR DIE METHODE AAN
            fragment.goBack();
            return;
        }

        super.onBackPressed();
    }


}
