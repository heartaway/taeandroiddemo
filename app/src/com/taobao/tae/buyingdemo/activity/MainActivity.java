package com.taobao.tae.buyingdemo.activity;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.app.BuyingDemoApplication;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.widget.buttonmenu.ButtonMenu;
import com.taobao.tae.buyingdemo.widget.buttonmenu.CustomButtonMenu;
import com.taobao.tae.sdk.TaeSDK;
import com.taobao.tae.sdk.callback.CallbackContext;
import com.taobao.tae.sdk.callback.LoginCallback;
import com.taobao.tae.sdk.model.Session;

/**
 * <p>底部菜单</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/12
 * Time: 下午10:05
 */
public class MainActivity extends TabActivity {

    /*首次点击返回时间*/
    private long firstClickBackTime = 0;
    private final CustomButtonMenu buttonMenuVM = new CustomButtonMenu();
    private TabHost tabHost;
    private ButtonMenu buttonMenu;
    private final String indexTabTag = "IndexActivity";
    private final String categoryTabTag = "categoryActivity";
    private final String myTabTag = "myActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_layout);
        initializeButtonMenu();
        addBottomMenuListener();
        indexMenuSelected();
        BuyingDemoApplication.getInstance().setButtonMenu(buttonMenu);
    }


    /**
     * Set the ButtonMenuVM implementation to the ButtonMenu custom view and initialize it.
     */
    private void initializeButtonMenu() {
        buttonMenu = (ButtonMenu) findViewById(R.id.button_menu);
        buttonMenu.setButtonMenuVM(buttonMenuVM);
        buttonMenu.initialize();
        tabHost = this.getTabHost();
        tabHost.addTab(tabHost.newTabSpec(indexTabTag).setIndicator(indexTabTag)
                .setContent(new Intent(this, IndexActivity.class)));
        tabHost.addTab(tabHost.newTabSpec(categoryTabTag).setIndicator(categoryTabTag)
                .setContent(new Intent(this, CategoryActivity.class)));
        tabHost.addTab(tabHost.newTabSpec(myTabTag).setIndicator(myTabTag)
                .setContent(new Intent(this, MyActivity.class)));
    }


    /**
     * 跳转到指定的Activity
     */
    private void toDestination() {
        int toActivity = getIntent().getIntExtra(AppConfig.ACTIVITY_JUMP_TAG, 0);
        if (toActivity == R.string.activity_name_of_my) {
            tabHost.setCurrentTabByTag(myTabTag);
        }
    }

    public void addBottomMenuListener() {
        Button indexButton = (Button) findViewById(buttonMenuVM.index.getClickableResId());
        final Button categoryButton = (Button) findViewById(buttonMenuVM.category.getClickableResId());
        Button myButton = (Button) findViewById(buttonMenuVM.my.getClickableResId());
        indexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tabHost.getCurrentTabTag().equals(indexTabTag)) {
                    tabHost.setCurrentTabByTag(indexTabTag);
                    indexMenuSelected();
                }
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tabHost.getCurrentTabTag().equals(categoryTabTag)) {
                    tabHost.setCurrentTabByTag(categoryTabTag);
                    categoryMenuSelected();
                }

            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tabHost.getCurrentTabTag().equals(myTabTag)) {
                    if (!TaeSDK.getSession().isLogin()) {
                        clickMyToLogin();
                    } else {
                        tabHost.setCurrentTabByTag(myTabTag);
                        myMenuSelected();
                    }
                }

            }
        });
    }

    /**
     * 首页选中状态
     */
    public void indexMenuSelected() {
        buttonMenu.onImageResourceChanged(R.drawable.menu_index_icon_s, buttonMenuVM.index);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_selected), buttonMenuVM.index);
        buttonMenu.onImageResourceChanged(R.drawable.menu_category_icon, buttonMenuVM.category);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.category);
        buttonMenu.onImageResourceChanged(R.drawable.menu_my_icon, buttonMenuVM.my);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.my);
    }

    /**
     * 分类 选中状态
     */
    public void categoryMenuSelected() {
        buttonMenu.onImageResourceChanged(R.drawable.menu_index_icon, buttonMenuVM.index);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.index);
        buttonMenu.onImageResourceChanged(R.drawable.menu_category_icon_s, buttonMenuVM.category);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_selected), buttonMenuVM.category);
        buttonMenu.onImageResourceChanged(R.drawable.menu_my_icon, buttonMenuVM.my);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.my);

    }

    /**
     * 我的选中状态
     */
    public void myMenuSelected() {
        buttonMenu.onImageResourceChanged(R.drawable.menu_index_icon, buttonMenuVM.index);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.index);
        buttonMenu.onImageResourceChanged(R.drawable.menu_category_icon, buttonMenuVM.category);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_unselect), buttonMenuVM.category);
        buttonMenu.onImageResourceChanged(R.drawable.menu_my_icon_s, buttonMenuVM.my);
        buttonMenu.onSubjectColorChanged(getResources().getColor(R.color.menu_btn_selected), buttonMenuVM.my);
    }

    /**
     * 点击“我的”，未登录时，跳转到登录界面，登录成功后跳转的“我的”界面
     */
    public void clickMyToLogin() {
        LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onSuccess(Session session) {
                tabHost.setCurrentTabByTag(myTabTag);
                myMenuSelected();
            }

            @Override
            public void onFailure(int i, String s) {
            }
        };
        TaeSDK.showLogin(this, loginCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
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


    @Override
    protected void onResume() {
        super.onResume();
        toDestination();
    }
}
