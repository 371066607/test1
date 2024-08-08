package com.example.test1.activity;

import android.os.Bundle;
import android.webkit.WebSettings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test1.R;
import com.example.test1.jsbridge.BridgeHandler;
import com.example.test1.jsbridge.BridgeWebView;
import com.example.test1.jsbridge.CallBackFunction;

public class WebAcitivity extends BaseActivity {
private String url;
    private BridgeWebView bridgeWebView;
    @Override
    protected void initView() {
bridgeWebView = findViewById(R.id.bridgeWebView);
    }

    @Override
    protected void initData() {

        Bundle bundle = getIntent().getExtras();
       if (bundle!=null){
           url = bundle.getString("url");
       }
//        registJvaHandler();
       initWebView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_web_acitivity;
    }

    /**
     * 初始化webview，并加载url，并开启js交互
     */
    private void initWebView(){
        WebSettings webSettings = bridgeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        bridgeWebView.loadUrl(url);
    }

    /**
     * 这里注册一个handler，用于处理返回逻辑
     */
//    public void registJvaHandler(){
//        bridgeWebView.registerHandler("goback", new BridgeHandler() {
//            @Override
//            public void handler(String data, CallBackFunction function) {
//                finish();
//            }
//        });
//    }
}