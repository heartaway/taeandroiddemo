package com.taobao.tae.buyingdemo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.fragment.SearchResultFragment;
import com.taobao.tae.buyingdemo.view.UnderLineTextIndicator;

/**
 * <p>商品搜索结果的排序页面、二级分类进入页</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 下午4:13
 */
public class SearchResultSortActivity extends FragmentActivity {

    /*首次点击返回时间*/
    private long firstClickBackTime = 0;
    private UnderLineTextIndicator underLineTextIndicator;
    private Fragment newFragment, salesFragment, priceFragment;
    /*记录当前展示真的Fragment*/
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_layout);
        initTitle();
        initSortTabView();
        addBtnListener();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        TextView titleTextView = (TextView) findViewById(R.id.search_header_title_txt);
        String title = getIntent().getStringExtra(ApiConfig.SEARCH_TITLE);
        titleTextView.setText(title);
    }

    /**
     * 初始化 商品的排序方式 Tab
     * 销量默认倒序（从大到小），价格默认正序(从低到高)，最新默认是倒序(从新到老)
     */
    private void initSortTabView() {
        String keyWord = getIntent().getStringExtra(ApiConfig.SEARCH_KEYWORD);
        int sortType = getIntent().getIntExtra(ApiConfig.SEARCH_SORT, 0);
        int categoryId = getIntent().getIntExtra(ApiConfig.CATEGORY_ID, AppConfig.ERROR_CODE);
        LinearLayout underLineTextLinearLayout = (LinearLayout) findViewById(R.id.search_sort_type_group);
        underLineTextIndicator = new UnderLineTextIndicator(this);
        underLineTextIndicator.setLineHeight(getResources().getDimensionPixelSize(R.dimen.search_result_sort_type_title_bottom_line_height));
        underLineTextIndicator.setTitlePaddingBottomLine(getResources().getDimensionPixelSize(R.dimen.search_result_sort_type_title_bottom_line_margin_top));
        underLineTextIndicator.setTextSizeResourceId(R.dimen.search_result_sort_type_title_size);
        underLineTextIndicator.initView(this, AppConfig.ITEM_SEARCH_RESULT_SORT, UnderLineTextIndicator.SPACE_EQUAL_DIVIDE, R.dimen.search_result_sort_type_underline_append_size);
        underLineTextLinearLayout.addView(underLineTextIndicator);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (sortType == AppConfig.SORT_PRICE_ORDER_BY_DASC || sortType == AppConfig.SORT_PRICE_ORDER_BY_ASC) {
            Bundle newTypeBundle = new Bundle();
            newTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_NEW_ORDER_BY_DASC);
            newTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            newTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle salesTypeBundle = new Bundle();
            salesTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_SALES_ORDER_BY_DASC);
            salesTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            salesTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle priceTypeBundle = new Bundle();
            priceTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,sortType);
            priceTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            priceTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            newFragment =  SearchResultFragment.newInstance(newTypeBundle);
            salesFragment = SearchResultFragment.newInstance(salesTypeBundle);
            priceFragment = SearchResultFragment.newInstance(priceTypeBundle);
            fragmentTransaction.add(R.id.search_result_content, priceFragment).commit();
            underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_PRICE_INDEX);
            currentFragment = priceFragment;
        } else if (sortType == AppConfig.SORT_SALES_ORDER_BY_DASC || sortType == AppConfig.SORT_SALES_ORDER_BY_ASC) {
            Bundle newTypeBundle = new Bundle();
            newTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_NEW_ORDER_BY_DASC);
            newTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            newTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle salesTypeBundle = new Bundle();
            salesTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,sortType);
            salesTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            salesTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle priceTypeBundle = new Bundle();
            priceTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_PRICE_ORDER_BY_ASC);
            priceTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            priceTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            newFragment =  SearchResultFragment.newInstance(newTypeBundle);
            salesFragment = SearchResultFragment.newInstance(salesTypeBundle);
            priceFragment = SearchResultFragment.newInstance(priceTypeBundle);
            fragmentTransaction.add(R.id.search_result_content, salesFragment).commit();
            underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_SALES_INDEX);
            currentFragment = salesFragment;
        } else {
            Bundle newTypeBundle = new Bundle();
            newTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,sortType);
            newTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            newTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle salesTypeBundle = new Bundle();
            salesTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_SALES_ORDER_BY_DASC);
            salesTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            salesTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            Bundle priceTypeBundle = new Bundle();
            priceTypeBundle.putInt(SearchResultFragment.SORT_TYPE_TAG,AppConfig.SORT_PRICE_ORDER_BY_ASC);
            priceTypeBundle.putInt(SearchResultFragment.CATEGORY_ID_TAG,categoryId);
            priceTypeBundle.putString(SearchResultFragment.KEY_WORD_TAG,keyWord);

            newFragment =  SearchResultFragment.newInstance(newTypeBundle);
            salesFragment = SearchResultFragment.newInstance(salesTypeBundle);
            priceFragment = SearchResultFragment.newInstance(priceTypeBundle);
            fragmentTransaction.add(R.id.search_result_content, newFragment).commit();
            underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_NEW_INDEX);
            currentFragment = newFragment;
        }

    }

    /**
     * 添加顶部返回按钮的监听器
     * 添加排序方式的按钮监听器
     */
    private void addBtnListener() {
        RelativeLayout backRelativeLayout = (RelativeLayout) findViewById(R.id.search_top_back_btn);
        backRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        View.OnClickListener newListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_NEW_INDEX);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (!newFragment.isAdded()) {
                    fragmentTransaction.hide(currentFragment).add(R.id.search_result_content, newFragment).commit();
                } else {
                    fragmentTransaction.hide(currentFragment).show(newFragment).commit();
                }
                currentFragment = newFragment;
            }
        };
        View.OnClickListener salesListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_SALES_INDEX);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (!salesFragment.isAdded()) {
                    fragmentTransaction.hide(currentFragment).add(R.id.search_result_content, salesFragment).commit();
                } else {
                    fragmentTransaction.hide(currentFragment).show(salesFragment).commit();
                }
                currentFragment = salesFragment;
            }
        };
        View.OnClickListener priceListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underLineTextIndicator.setSelected(AppConfig.ITEM_SEARCH_SORT_BY_PRICE_INDEX);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (!priceFragment.isAdded()) {
                    fragmentTransaction.hide(currentFragment).add(R.id.search_result_content, priceFragment).commit();
                } else {
                    fragmentTransaction.hide(currentFragment).show(priceFragment).commit();
                }
                currentFragment = priceFragment;
            }
        };
        underLineTextIndicator.setOnClickListener(AppConfig.ITEM_SEARCH_SORT_BY_NEW_INDEX, newListener);
        underLineTextIndicator.setOnClickListener(AppConfig.ITEM_SEARCH_SORT_BY_SALES_INDEX, salesListener);
        underLineTextIndicator.setOnClickListener(AppConfig.ITEM_SEARCH_SORT_BY_PRICE_INDEX, priceListener);
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
