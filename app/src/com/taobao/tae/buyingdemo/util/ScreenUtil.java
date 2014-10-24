package com.taobao.tae.buyingdemo.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by xinyuan on 14/8/29.
 */
public class ScreenUtil {

    /**
     * 获取设备屏幕宽度
     * @param context
     * @return
     */
    public static  int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       return windowManager.getDefaultDisplay().getWidth();
    }
}
