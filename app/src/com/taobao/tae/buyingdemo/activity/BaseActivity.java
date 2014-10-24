package com.taobao.tae.buyingdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;

/**
 * <p>Activity的基类</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 上午9:35
 */
public class BaseActivity extends Activity {

    /*首次点击返回时间*/
    private long firstClickBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 用户触发返回按钮时的操作
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                long secondClickBackTime = System.currentTimeMillis();
                if (secondClickBackTime - firstClickBackTime > AppConfig.EXIT_CLICK_INTERVAL_TIME) {
                    toast(MsgConfig.CLICK_TO_EXIT_APP);
                    firstClickBackTime = secondClickBackTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
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
