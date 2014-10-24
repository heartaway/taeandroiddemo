package com.taobao.tae.buyingdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkStateUtil {
    public static final int GPRS = 0;
    public static final int WIFI = 1;

    /**
     * 获取网络类型
     *
     * @param paramContext
     * @return
     */
    public static int getConnectedType(Context paramContext) {
        if (paramContext != null) {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
                return localNetworkInfo.getType();
        }
        return -1;
    }

    /**
     * 检测网络是否以某种方式连接
     *
     * @param paramContext
     * @param paramInt
     * @return
     */
    public static boolean isByTypeConnected(Context paramContext, int paramInt) {
        if (paramContext != null) {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(paramInt);
            if (localNetworkInfo != null)
                return localNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 检测是否有网络存在
     *
     * @param paramContext
     * @return
     */
    public static boolean isConnected(Context paramContext) {
        if (paramContext != null) {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (localNetworkInfo != null)
                return localNetworkInfo.isAvailable();
        }
        return false;
    }


    public static boolean isNoConnected(Context paramContext) {
        return !isConnected(paramContext);
    }
}