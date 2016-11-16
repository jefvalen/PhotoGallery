package com.jeffrey.android.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_url";    //SETTING UP WEB BROWSER FRAGMENT

    private Uri mUri;
    private WebView mWebView ;
    private ProgressBar mProgressBar; //USING WEBCHROMECLIENT, PAGE 521

    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);   //Returns the value associated with the given key, or null if no mapping of the desired type exists
                                                        // for the given key or a null value is explicitly associated with the key.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar =
                (ProgressBar)v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100); //WEBCHROMECLIENT REPORTS IN RANGE 0-100

        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView webView, int newProgress){
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            public void onReceivedTitle(WebView webView, String title){
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(title);
            }

        });


        mWebView.setWebViewClient(new WebViewClient(){ //LOADING URL INTO WEBVIEW, PAGE 519
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });

        mWebView.loadUrl(mUri.toString());

        //Challenge 2 moet hier komen...

        return v;

    }

    //CHALLENGE PAGE 524
    public boolean canGoBack() {  //OMDAT DE mWebView hier bekend is moet je de methode hier aanroepen.
        return mWebView.canGoBack();
    }

    public void goBack(){  //OMDAT DE mWebView hier bekend is moet je de methode hier aanroepen.
        mWebView.goBack();  // IS VOID dus behoeft geen return.
    }

}
