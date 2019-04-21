package com.ludoscity.findmybikes.ui.webview;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.ludoscity.findmybikes.R;

public class WebViewActivity extends AppCompatActivity {

    public final static String EXTRA_URL = "webviewactivity.URL";
    public final static String EXTRA_ACTIONBAR_SUBTITLE = "webviewactivity.SUBTITLE";
    public final static String EXTRA_JAVASCRIPT_ENABLED = "webviewactivity.JAVASCRIPT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        setSupportActionBar(findViewById(R.id.toolbar_main));

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        WebView webview = findViewById(R.id.webview);

        if (intent.getBooleanExtra(EXTRA_JAVASCRIPT_ENABLED, false)) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient());
        }

        webview.getSettings().setDefaultTextEncodingName("utf-8");

        webview.loadUrl(url);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();

        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle(intent.getStringExtra(EXTRA_ACTIONBAR_SUBTITLE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}