package com.cartoon.fam.cartoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class Final extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        Intent intent = getIntent();
        String url = intent.getStringExtra(ShowActivity.lastUrl);

        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(url);
    }
}
