package com.yin.trip.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yin.trip.R;
import com.yin.trip.util.LocationSendData;


public class MainActivity extends AppCompatActivity{

    private WebView webView;
    private ProgressDialog progressDialog;

    //开发环境
    private String url = "http://192.168.191.1:8080/trip-admin/login";

    private Button button;

    private ImageView error_show;

    private ImageView location_view;

    //正式环境
//    private String url = "http://119.29.70.147:8111/trip-admin/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toast.makeText(MainActivity.this, "tips:点击左下方图标退出登录重新定位", Toast.LENGTH_SHORT).show();

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        //获取数据

        Intent intent = this.getIntent();
        LocationSendData locationSendData = (LocationSendData)intent.getSerializableExtra("location");
        Log.i("主类", locationSendData.getLongtitude() + " " + locationSendData.getLatitude());

        //初始化页面
        initView();
        String sendUrl = url + "?lon=" + locationSendData.getLongtitude() + "&lat=" + locationSendData.getLatitude();

        Log.i("服务器链接" , sendUrl);
        loadUrl(sendUrl);

        error_show = (ImageView)findViewById(R.id.errorView);

        button = (Button)findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearCache(true);
                webView.reload();
            }
        });

        location_view = (ImageView) findViewById(R.id.img_float);

        location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

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
                webView.setVisibility(View.GONE);
                error_show.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                Log.i("打印日志", "网页加载失败");
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webView.setVisibility(View.VISIBLE);
                error_show.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

                super.onPageStarted(view, url, favicon);
                showProgress("页面加载中");//开始加载动画
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                removeProgress();//当加载结束时移除动画
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

    //-----显示ProgressDialog
    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);//设置点击不消失
        }
        if (progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        } else {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }
    //------取消ProgressDialog
    public void removeProgress(){
        if (progressDialog == null){
            return;
        }
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }

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


