package com.yin.trip.activity;

import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yin.trip.R;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    //开发环境
 //   private String url = "http://172.30.81.248:8080/trip-admin/";

    //正式环境
    private String url = "http://119.29.70.147:8080/trip-admin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tbs);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //初始化页面
        initView();
        loadUrl(url);

    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
    }

    private void loadUrl(String url) {
        WebSettings webSettings = webView.getSettings();
        //支持javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //启动缓存
        webSettings.setAppCacheEnabled(true);
        //设置最大缓存
        webSettings.setAppCacheMaxSize(1024 * 10);

        //设置成拖动放大缩小
        webSettings.setBuiltInZoomControls(true);

        //设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(url);

        //设置自适应网页
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                Log.i("打印日志", "网页加载失败");
            }

        });

        //进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.i("打印日志", "加载完成");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}


