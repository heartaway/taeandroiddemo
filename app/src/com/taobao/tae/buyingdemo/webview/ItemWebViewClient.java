package com.taobao.tae.buyingdemo.webview;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;

/**
 * <p>用于展示用户自定义活动页面的WebView</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/20
 * Time: 下午3:01
 */
public class ItemWebViewClient extends android.webkit.WebViewClient {

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d("WebView", "onPageStarted");
        super.onPageStarted(view, url, favicon);
    }

    public void onPageFinished(WebView view, String url) {
        Log.d("WebView", "onPageFinished ");
        super.onPageFinished(view, url);
    }
}
