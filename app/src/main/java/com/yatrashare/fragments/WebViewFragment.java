package com.yatrashare.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yatrashare.R;
import com.yatrashare.utils.UtilsLog;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {


    private static final String TAG = WebViewFragment.class.getSimpleName();
    private Context mContext;

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        mContext = getActivity();
        WebView webView = (WebView) view.findViewById(R.id.moreWebView);
        String stringURL = getArguments().getString("URL");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.appCompatDialog);

        final ProgressDialog progressBar = ProgressDialog.show(mContext, "", "Loading...");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                UtilsLog.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                UtilsLog.i(TAG, "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                UtilsLog.e(TAG, "Error: " + description);
                Toast.makeText(mContext, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                builder.setTitle("Error");
                builder.setMessage(description);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        webView.loadUrl(stringURL);
        return view;
    }

}
