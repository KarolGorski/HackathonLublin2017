package com.mojzesze.hackathonlublin2017;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Karol on 2017-10-21.
 */

public class WebActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpk_web);

        String URLmpk = "http://www.mpk.lublin.pl/index.php?s=rozklady";

        WebView web = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);


        web.loadUrl(URLmpk);

    }
}
