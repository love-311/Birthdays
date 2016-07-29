package com.love311.www.birthdays.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.webview.BackHandledFragment;

/**
 * Created by Administrator on 2016/5/20.
 */
public class GiftFragment extends BackHandledFragment {

    private View view;
    private WebView webView;
    private WebSettings mWebSettings;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gift_frag, container, false);
        webView = (WebView) view.findViewById(R.id.web_view);
        mWebSettings = webView.getSettings();
        mWebSettings.setDefaultFixedFontSize(13);
        mWebSettings.setDefaultTextEncodingName("UTF-8");
        if (isNetworkAvailable(getActivity())==true){
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }else {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        //webview中即能打开http又能打开https
        if (Build.VERSION.SDK_INT >= 21) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
        mWebSettings.setSaveFormData(false);
        mWebSettings.setJavaScriptEnabled(true);    //允许加载javascript
        mWebSettings.setSupportZoom(true);          //允许缩放
        mWebSettings.setBuiltInZoomControls(true);  //原网页基础上缩放
        mWebSettings.setUseWideViewPort(true);      //任意比例缩放
        webView.loadUrl("https://m.shengri.cn/shop/present?cityId=510100&r=main.entry_index..0-81-0..2-621-0");
       webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        /*****************************************************************
         * 在点击请求的是链接时才会调用，重写此方法返回true表明点击网页里
         * 面的链接还是在当前的WebView里跳转，不会跳到浏览器上运行。
         *****************************************************************/
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        return view;

    }
    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
    /***********************************************************
     * 监听返回键
     * 注：这里的返回键是指在加载的网页中进入了另一个连接，点击返回
     ***********************************************************/
    @Override
    public boolean onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
            return true;
        }else{
            return false;
        }
    }
}
