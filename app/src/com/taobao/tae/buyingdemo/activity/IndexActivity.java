package com.taobao.tae.buyingdemo.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.adapter.CustomFragmentPagerAdapter;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.fragment.IndexDefaultFragment;
import com.taobao.tae.buyingdemo.util.ViewServer;

import java.util.ArrayList;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 下午4:13
 */
public class IndexActivity extends FragmentActivity {

    /*首次点击返回时间*/
    private long firstClickBackTime = 0;

    private static int TOTAL_COUNT = 1;
    /* 用户选择的viewpage下标*/
    public static final String EXTRA_SELECTED_POSITION = "selected_position_index";

    static final int DEFAULT_INDEX = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_index_layout);
        initView();
    }

    /**
     * 初始化页面，采用ViewPager实现，方便后面扩展
     */
    public void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.index_view_pager);
        ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
        Fragment fragment = IndexDefaultFragment.newInstance();
        fragmentsList.add(fragment);
        viewPager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(DEFAULT_INDEX);
    }

    /**
     * 用户触发返回按钮时的操作
     *
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

    /**
     * send broadcast that selected fragment have changed, to tell child ViewPager to start or stop auto scroll
     *
     * @param position
     */
    private void sendSelectedBroadcast(int position) {
        Intent i = new Intent();
        i.putExtra(EXTRA_SELECTED_POSITION, position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            sendSelectedBroadcast(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
