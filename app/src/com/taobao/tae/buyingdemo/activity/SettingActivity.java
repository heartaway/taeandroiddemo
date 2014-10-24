package com.taobao.tae.buyingdemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.sdk.TaeSDK;
import com.taobao.tae.sdk.callback.LoginCallback;
import com.taobao.tae.sdk.callback.LogoutCallback;
import com.taobao.tae.sdk.model.Session;

/**
 * <p>设置页面</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 下午4:13
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting_layout);
        addBtnListener();
    }

    public void addBtnListener() {
        Button loggoutButton = (Button) findViewById(R.id.setting_loggout_btn);
        loggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        RelativeLayout backRelativeLayout = (RelativeLayout)findViewById(R.id.setting_top_back_btn);
        backRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout(){
        TaeSDK.logout(this, getLogoutCallBack());
    }

    public LogoutCallback getLogoutCallBack() {
        LogoutCallback logoutCallback = new LogoutCallback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_setting);
                bundle.putInt(AppConfig.ACTIVITY_JUMP_TAG,R.string.activity_name_of_my);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, String s) {
                toast(MsgConfig.LOGOUT_FAILURE);
            }
        };
        return logoutCallback;
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
