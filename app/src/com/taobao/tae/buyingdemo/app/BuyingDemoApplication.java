package com.taobao.tae.buyingdemo.app;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.widget.buttonmenu.ButtonMenu;
import com.taobao.tae.sdk.TaeSDK;
import com.taobao.tae.sdk.callback.InitResultCallback;
import com.taobao.wireless.security.sdk.SecurityGuardManager;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by xinyuan on 14/7/4.
 */

@ReportsCrashes(
        formKey = "",
        mailTo = AppConfig.APP_CRASH_REPORT_MAIL,
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = { ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA,ReportField.BRAND, ReportField.STACK_TRACE, ReportField.LOGCAT,ReportField.USER_COMMENT },
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title
)
public class BuyingDemoApplication extends Application {

    private static BuyingDemoApplication instance;

    /*底部菜单*/
    private ButtonMenu buttonMenu;

    private static Context appContext;

    public static BuyingDemoApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.setAppContext(getApplicationContext());
        initTaeSDK();
        initPushClient();
        initCrashReport();
    }

    /**
     * 初始化TaeSDK
     */
    public void initTaeSDK() {
        TaeSDK.asyncInit(this, new InitResultCallback() {
            @Override
            public void onSuccess() {
                String internalSessionJson =SecurityGuardManager.getInstance(getApplicationContext()).getDynamicDataStoreComp().getString("internal_session");
                System.out.println("获取SID："+internalSessionJson);
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }

    /**
     * 初始化云推送客户端
     */
    public void initPushClient() {
        String count = "";
        if (TaeSDK.getSession() != null && TaeSDK.getSession().getUser() != null) {
            count = TaeSDK.getSession().getUser().nick;
        }
//        CloudPush.getInstance().register(this, AppConfig.CPUSH_APP_ID, AppConfig.CPUSH_APP_KEY, count);
    }


    /**
     * 初始化应用Crash报告
     */
    public void initCrashReport(){
        ACRA.init(this);
    }


    public AssetManager getAssetsss() {
        AssetManager assetManager = getAssets();
        return assetManager;
    }


    public ButtonMenu getButtonMenu() {
        return buttonMenu;
    }

    public void setButtonMenu(ButtonMenu buttonMenu) {
        this.buttonMenu = buttonMenu;
    }

    public static void setAppContext(Context appContext) {
        BuyingDemoApplication.appContext = appContext;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
