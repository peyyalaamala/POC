package com.example.stopmindlessscrolling.views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;


/**
 * To show UFL website
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends AppCompatActivity {


    private WebView webView;
    private ProgressDialog progressDialog = null;
    private WebClientClass webViewClient;
    private Intent i;
    private String[] mailToString;
    private WebChromeClient webChromeClient;
    private TextView titleTxt;

    @SuppressLint({"ObsoleteSdkInt", "MissingInflatedId"})
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity_webview);
        //Side menu navigation
        ImageView settingsMenuImg = findViewById(R.id.settingsMenuImg);
        settingsMenuImg.setOnClickListener(v -> {

            finish();

        });

        webView = findViewById(R.id.webView);
        titleTxt = findViewById(R.id.titleTxt);
        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage(AppConstants.LOADING);
        webView.getSettings().setJavaScriptEnabled(true);


        String uRL = "";
        String title = "";

        try {
            if (getIntent().getExtras() != null ) {
                uRL = getIntent().getExtras().getString(AppConstants.WEBURL);
                title = getIntent().getExtras().getString(AppConstants.WEBTITLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        if(title!=null){
            titleTxt.setText(title);
        }


        webView.loadUrl(uRL);


        webViewClient = new WebClientClass();
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webChromeClient = new WebChromeClient();
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl(uRL);
        webView.setOnKeyListener(new OnKeyListener() {

            /**
             * To work "Back Button" functionality in AboutUs HTML page
             */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return true;
                        }
                    }
                }

                return false;
            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

    }

    /**
     * To call web page
     */
    public class WebClientClass extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!progressDialog.isShowing())
                progressDialog.show();
        }

        /**
         * close progress dialog after finishing page loading
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        /**
         * In AboutUs page "mailto" hyperlink functionality
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(AppConstants.MAILTO)) {
                MailTo mt = MailTo.parse(url);
                i = new Intent(Intent.ACTION_SEND);
                i.setType(AppConstants.TEXTPLAIN);
                mailToString = new String[]{mt.getTo()};
                i.putExtra(Intent.EXTRA_EMAIL, mailToString);
                i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                i.putExtra(Intent.EXTRA_CC, mt.getCc());
                i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                startActivity(i);
                view.reload();
                return true;

            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * The final call you receive before your activity is destroyed. This can happen either because
     * the activity is finishing or because the system is temporarily destroying this instance of
     * the activity to save space.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        {
            if (webView != null) {
                webView = null;
            }
            if (progressDialog != null) {
                progressDialog = null;
            }
            if (webViewClient != null) {
                webViewClient = null;
            }
            if (i != null) {
                i = null;
            }
            if (mailToString != null) {
                mailToString = null;
            }
            if (webChromeClient != null) {
                webChromeClient = null;
            }
        }
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}