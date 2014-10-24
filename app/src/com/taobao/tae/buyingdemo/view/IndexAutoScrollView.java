/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.taobao.tae.buyingdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.taobao.tae.buyingdemo.R;

/**
 * 首页图片轮播
 */
public class IndexAutoScrollView extends LinearLayout {

    private Context mContext;
    private LinearLayout autoScrollView;

    public IndexAutoScrollView(Context context) {
        super(context);
        initView(context);
    }

    public IndexAutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 初始化图片轮播
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        autoScrollView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.index_auto_scroll_view,null);
        addView(autoScrollView);
    }

    public LinearLayout getAutoScrollView() {
        return autoScrollView;
    }
}
