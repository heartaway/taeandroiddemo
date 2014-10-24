package com.taobao.tae.buyingdemo.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.util.NetWorkStateUtil;
import com.taobao.tae.buyingdemo.webview.ItemWebViewClient;

/**
 * <p>用来渲染H5页面的Activity</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 上午9:35
 */
public class ItemWebviewActivity extends Activity {

    private String url;
    private String title;
    private WebView webView;
    private ItemWebViewClient itemWebViewClient;
    private RelativeLayout noConnectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_itemwebview_layout);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        webView = (WebView) findViewById(R.id.item_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.requestFocus();
        itemWebViewClient = new ItemWebViewClient();
        webView.setWebViewClient(itemWebViewClient);
        noConnectionLayout = (RelativeLayout) findViewById(R.id.no_network_connection);
        TextView titleTextView = (TextView) findViewById(R.id.webview_header_txt);
        titleTextView.setText(title);
        loadUrl();
        addBtnListener();
    }

    public void loadUrl() {
        if (NetWorkStateUtil.isConnected(this)) {
            noConnectionLayout.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
        } else {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            noConnectionLayout.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    }


    public void addBtnListener() {
        RelativeLayout backRelativeLayout = (RelativeLayout) findViewById(R.id.item_wv_top_back_btn);
        backRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (noConnectionLayout.getVisibility() == View.VISIBLE) {
            TextView retryTextView = (TextView) findViewById(R.id.no_network_connection_retry);
            retryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadUrl();
                }
            });
        }
    }


    /**
     * 展示一个特定颜色的Toast
     *
     * @param message
     */
    protected void toast(String message) {
        View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setView(toastRoot);
        TextView tv = (TextView) toastRoot.findViewById(R.id.toast_notice);
        tv.setText(message);
        toast.show();
    }

}
