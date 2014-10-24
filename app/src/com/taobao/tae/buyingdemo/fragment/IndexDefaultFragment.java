package com.taobao.tae.buyingdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.demo.common.AndroidSecretUtil;
import com.alibaba.demo.common.Parameter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.activity.ItemDetailActivity;
import com.taobao.tae.buyingdemo.activity.ItemWebviewActivity;
import com.taobao.tae.buyingdemo.activity.SearchResultSortActivity;
import com.taobao.tae.buyingdemo.adapter.IndexItemListAdapter;
import com.taobao.tae.buyingdemo.app.BuyingDemoApplication;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.model.ItemDataObjectType;
import com.taobao.tae.buyingdemo.model.ItemInfoDO;
import com.taobao.tae.buyingdemo.model.OpenType;
import com.taobao.tae.buyingdemo.util.NetWorkStateUtil;
import com.taobao.tae.buyingdemo.util.StringUtils;
import com.taobao.tae.buyingdemo.widget.autoscrollpager.AutoScrollViewPager;
import com.taobao.tae.buyingdemo.widget.autoscrollpager.ImagePagerAdapter;
import com.taobao.tae.buyingdemo.widget.buttonmenu.ButtonMenu;
import com.taobao.tae.buyingdemo.widget.buttonmenu.animator.ObjectAnimatorFactory;
import com.taobao.tae.buyingdemo.widget.buttonmenu.animator.ScrollAnimator;
import com.taobao.tae.buyingdemo.widget.pagerindicator.CirclePageIndicator;
import com.taobao.tae.buyingdemo.widget.pagerindicator.PageIndicator;
import com.taobao.tae.buyingdemo.widget.pinterest.ActionType;
import com.taobao.tae.buyingdemo.widget.pinterest.MultiColumnPullToRefreshListView;
import com.taobao.tae.buyingdemo.widget.pinterest.PinterestAdapterView;
import com.taobao.tae.buyingdemo.widget.pinterest.PinterestListView;
import com.taobao.tae.buyingdemo.util.VolleySingleton;
import com.taobao.tae.buyingdemo.view.IndexDefaultFragemtView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 首页 第一个PageView 页面展示
 */
public class IndexDefaultFragment extends Fragment implements MultiColumnPullToRefreshListView.IPinterestListViewListener {

    public String TAG = IndexDefaultFragment.class.getName();
    public IndexDefaultFragemtView indexDefaultFragemtView = null;
    public IndexItemListAdapter indexItemListAdapter = null;
    private Context context;
    private RequestQueue requestQueue;
    private View view;
    private int currentPage = 1;
    /**
     * 最后一次发布出来的商品时间
     */
    private Date lastPushItemsTime;
    //图片轮播数据
    private List<ItemDataObject> bannersItemData;

    public static IndexDefaultFragment newInstance() {
        IndexDefaultFragment newFragment = new IndexDefaultFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index_default_fragment, container, false);
        indexDefaultFragemtView = (IndexDefaultFragemtView) view.findViewById(R.id.items);
        initBanner();
        initItemListView();
        return view;
    }

    /**
     * 初始化顶部图片轮播
     */
    public void initBanner() {
        getBanners();
    }

    /**
     * 初始化商品列表
     */
    public void initItemListView() {
        indexDefaultFragemtView.setPullLoadEnable(true);
        indexDefaultFragemtView.setXListViewListener(this);
        indexDefaultFragemtView.setVerticalScrollBarEnabled(false);
        indexDefaultFragemtView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        indexItemListAdapter = new IndexItemListAdapter(context, 3);
        indexDefaultFragemtView.setAdapter(indexItemListAdapter);
        addItemToContainer(currentPage, ActionType.PULL_REFRESH_ACTION);
        itemOnClick();
        initBackground();
        initializeScrollAnimator(indexDefaultFragemtView);
    }


    /**
     * Initialize ScrollAnimator to work with ButtonMenu custom view, the ListView used in this sample and an
     * animation duration of 200 milliseconds.
     */
    public void initializeScrollAnimator(MultiColumnPullToRefreshListView listView) {
        if (AppConfig.isbottomMenuHiddenOnScroll) {
            ButtonMenu buttonMenu = BuyingDemoApplication.getInstance().getButtonMenu();
            ScrollAnimator scrollAnimator = new ScrollAnimator(buttonMenu, new ObjectAnimatorFactory());
            scrollAnimator.configureListView(listView);
            scrollAnimator.setDurationInMillis(300);
        }
    }


    /**
     * 获取商品
     *
     * @param page
     * @param actionType
     */
    protected void addItemToContainer(int page, final int actionType) {
        if (NetWorkStateUtil.isNoConnected(context)) {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            return;
        }
        String url = getItemListRequestUrl(page, actionType);
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            renderItemsView(parseItemsJsonObject((JSONObject) o), actionType);
                        } catch (Exception e) {
                            toast(MsgConfig.GET_ITEMS_FAILURE);
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (actionType == ActionType.PULL_REFRESH_ACTION) {
                    indexDefaultFragemtView.stopRefresh();
                }
                if (actionType == ActionType.VIEW_MORE_ITEMS_ACTION) {
                    indexDefaultFragemtView.stopLoadMore();
                }
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        }
        ));
    }

    /**
     * 获取图片轮播数据
     */
    protected void getBanners() {
        if (NetWorkStateUtil.isNoConnected(context)) {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            return;
        }
        String url = getBannerRequestUrl();
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            renderBannersView(parseBannersJsonObject((JSONObject) o));
                        } catch (Exception e) {
                            toast(MsgConfig.GET_ITEMS_FAILURE);
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        }
        ));
    }

    /**
     * 用户点击商品图片，根据类型判断进入的组件
     * 类型有：商品详情、商品搜索结果页、H5
     */
    private void itemOnClick() {
        indexDefaultFragemtView.setOnItemClickListener(new PinterestAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PinterestAdapterView<?> parent, View view,
                                    int position, long id) {
                PinterestListView gv = (PinterestListView) parent;
                ItemDataObject itemDataObject = (ItemDataObject) gv.getItemAtPosition(position);
                toAnotherActivity(itemDataObject);
            }

        });
        final AutoScrollViewPager autoScrollViewPager = (AutoScrollViewPager) indexDefaultFragemtView.getBannerView().findViewById(R.id.auto_scroll_view_pager);
        autoScrollViewPager.setOnSingleTouchListener(new AutoScrollViewPager.OnSingleTouchListener() {
            @Override
            public void onSingleTouch() {
                int currentItemIndex = autoScrollViewPager.getCurrentItem();
                if (bannersItemData != null && currentItemIndex >= 0 && currentItemIndex < bannersItemData.size()) {
                    ItemDataObject itemDataObject = bannersItemData.get(currentItemIndex);
                    toAnotherActivity(itemDataObject);
                }
            }
        });
    }

    /**
     * 跳转到其它Activity
     *
     * @param itemDataObject
     */
    private void toAnotherActivity(ItemDataObject itemDataObject) {
        if (itemDataObject.getType() == ItemDataObjectType.UPDATE_TIME) {
            return;
        }
        ItemInfoDO itemInfoDO = (ItemInfoDO) itemDataObject.getData();
        Bundle bundle = new Bundle();
        if (OpenType.ITEM.getType().equals(itemInfoDO.getType())) {
            //跳转到Native商品详情页
            bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_index);
            Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
            if (itemInfoDO.getItemId() != null) {
                bundle.putLong(ApiConfig.ITEM_ID, itemInfoDO.getItemId());
            }
            if (itemInfoDO.getTbItemId() == null) {
                toast(MsgConfig.GET_ITEMS_FAILURE);
                return;
            } else {
                bundle.putString(ApiConfig.TAOBAO_ITEM_ID, itemInfoDO.getTbItemId());
            }
            bundle.putString("pic", itemInfoDO.getPicUrl());
            bundle.putString("price", itemInfoDO.getPrice());
            bundle.putString("promotion", itemInfoDO.getPromotionPrice());
            if (StringUtils.isNotEmpty(itemInfoDO.getName())) {
                bundle.putString("title", itemInfoDO.getName());
            } else {
                bundle.putString("title", itemInfoDO.getTitle());
            }
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (OpenType.H5.getType().equals(itemInfoDO.getType())) {
            //跳转的WebView页面
            bundle.putString("url", itemInfoDO.getH5Url());
            bundle.putString("title", itemInfoDO.getName());
            bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_index);
            Intent intent = new Intent(getActivity(), ItemWebviewActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (OpenType.SEARCH.getType().equals(itemInfoDO.getType())) {
            //跳转到搜索结果页
            bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_index);
            bundle.putString(ApiConfig.SEARCH_TITLE, itemInfoDO.getName());
            bundle.putString(ApiConfig.SEARCH_KEYWORD, itemInfoDO.getKeyword());
            if (itemInfoDO.getCategoryId() != null) {
                bundle.putInt(ApiConfig.CATEGORY_ID, itemInfoDO.getCategoryId());
            }
            if (itemInfoDO.getSort() != null) {
                bundle.putInt(ApiConfig.SEARCH_SORT, itemInfoDO.getSort());
            }
            Intent intent = new Intent(getActivity(), SearchResultSortActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    /**
     * 初始化背景
     */
    private void initBackground() {
        if (indexItemListAdapter.getCount() > 0) {
            LinearLayout viewMoreLayout = (LinearLayout) indexDefaultFragemtView.findViewById(R.id.view_more_content);
            viewMoreLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * 当下拉刷新时，重置页面，取最新记录;并同步更新图片轮播内容
     */
    @Override
    public void onRefresh() {
        getBanners();
        addItemToContainer(0, ActionType.PULL_REFRESH_ACTION);
    }

    @Override
    public void onLoadMore() {
        addItemToContainer(++currentPage, ActionType.VIEW_MORE_ITEMS_ACTION);
    }

    /**
     * 渲染商品列表结果页面
     */
    public void renderItemsView(List<ItemDataObject> itemDataObjectList, int actionType) {
        if (actionType == ActionType.PULL_REFRESH_ACTION) {
            indexItemListAdapter.addItemTop(itemDataObjectList, lastPushItemsTime);
            indexItemListAdapter.notifyDataSetChanged();
            indexDefaultFragemtView.stopRefresh();
        }
        if (actionType == ActionType.VIEW_MORE_ITEMS_ACTION) {
            indexDefaultFragemtView.stopLoadMore();
            indexItemListAdapter.addItemLast(itemDataObjectList);
            indexItemListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 渲染商品列表结果页面
     */
    public void renderBannersView(List<ItemDataObject> itemDataObjectList) {
        bannersItemData = itemDataObjectList;
        LinearLayout bannerView = indexDefaultFragemtView.getBannerView();
        bannerView.setVisibility(View.VISIBLE);
        bannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        AutoScrollViewPager autoScrollViewPager = (AutoScrollViewPager) bannerView.findViewById(R.id.auto_scroll_view_pager);
        PageIndicator pageIndicator = (CirclePageIndicator) bannerView.findViewById(R.id.auto_scroll_view_pager_indicator);
        autoScrollViewPager.setAdapter(new ImagePagerAdapter(context, itemDataObjectList));
        autoScrollViewPager.setInterval(AppConfig.AUTO_SCROLL_VIEW_PAGER_INTERVAL);
        autoScrollViewPager.startAutoScroll();
        pageIndicator.setViewPager(autoScrollViewPager);
    }


    /**
     * 获取列表数据的请求地址
     *
     * @param page
     * @return
     */
    private String getItemListRequestUrl(int page, int actionType) {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.PAGE_NAME, String.valueOf(page)));
            parameters.add(new Parameter(ApiConfig.PAGE_SIZE_NAME, String.valueOf(AppConfig.PAGE_SIZE)));
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            if (actionType == ActionType.PULL_REFRESH_ACTION && lastPushItemsTime != null) {
                parameters.add(new Parameter(ApiConfig.TIME_NAME, String.valueOf(lastPushItemsTime.getTime())));
            }
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);

            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_INDEX_ITEMS);
            path.append("?").append(ApiConfig.PAGE_NAME).append("=").append(page);
            path.append("&").append(ApiConfig.PAGE_SIZE_NAME).append("=").append(AppConfig.PAGE_SIZE);
            path.append("&").append(ApiConfig.SERVER_KEY_NAME).append("=").append(AppConfig.SERVER_KEY);
            if (actionType == ActionType.PULL_REFRESH_ACTION && lastPushItemsTime != null) {
                path.append("&").append(ApiConfig.TIME_NAME).append("=").append(lastPushItemsTime.getTime());
            }
            path.append("&").append(ApiConfig.TIME_STAMP_NAME).append("=").append(timstamp);
            path.append("&").append(ApiConfig.SIGN_NAME).append("=").append(token);
        } catch (IOException e) {
            e.printStackTrace();
            toast(MsgConfig.SYSTEM_ERROR);
        }
        return path.toString();
    }

    /**
     * 获取图片轮播的请求地址
     *
     * @return
     */
    private String getBannerRequestUrl() {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);

            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_INDEX_BANNERS);
            path.append("?").append(ApiConfig.SERVER_KEY_NAME).append("=").append(AppConfig.SERVER_KEY);
            path.append("&").append(ApiConfig.TIME_STAMP_NAME).append("=").append(timstamp);
            path.append("&").append(ApiConfig.SIGN_NAME).append("=").append(token);
        } catch (IOException e) {
            e.printStackTrace();
            toast(MsgConfig.SYSTEM_ERROR);
        }
        return path.toString();
    }

    /**
     * 解析商品列表
     *
     * @param jsonObject
     */
    public List<ItemDataObject> parseItemsJsonObject(JSONObject jsonObject) {
        List<ItemDataObject> itemDataObjectList = new ArrayList<ItemDataObject>();
        try {
            if (null != jsonObject && jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                if (dataJson.has("lastTime")) {
                    lastPushItemsTime = new Date(dataJson.getLong("lastTime"));
                }
                if (dataJson.has("items")) {
                    JSONArray jsonArray = dataJson.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemDataObject itemDataObject = new ItemDataObject();
                        JSONObject json = jsonArray.getJSONObject(i);
                        ItemInfoDO itemInfoDO = new ItemInfoDO();
                        if (json.has("picUrl") && !json.isNull("picUrl")) {
                            itemInfoDO.setPicUrl(json.getString("picUrl"));
                        } else {
                            continue;
                        }
                        if (json.has("title") && !json.isNull("title")) {
                            itemInfoDO.setTitle(json.getString("title"));
                        }
                        if (json.has("price") && !json.isNull("price")) {
                            itemInfoDO.setPrice(json.getString("price"));
                        }
                        if (json.has("discountPrice") && !json.isNull("discountPrice")) {
                            itemInfoDO.setPromotionPrice(json.getString("discountPrice"));
                        }
                        if (json.has("favorCount") && !json.isNull("favorCount")) {
                            itemInfoDO.setFavorCount(json.getString("favorCount"));
                        }
                        if (json.has("name") && !json.isNull("name")) {
                            itemInfoDO.setName(json.getString("name"));
                        }
                        if (json.has("keyword") && !json.isNull("keyword")) {
                            itemInfoDO.setKeyword(json.getString("keyword"));
                        }
                        if (json.has("h5Url") && !json.isNull("h5Url")) {
                            itemInfoDO.setH5Url(json.getString("h5Url"));
                        }
                        if (json.has("itemId") && !json.isNull("itemId")) {
                            itemInfoDO.setItemId(json.getLong("itemId"));
                        }
                        if (json.has("tbItemId") && !json.isNull("tbItemId")) {
                            itemInfoDO.setTbItemId(json.getString("tbItemId"));
                        }
                        if (json.has("sort") && !json.isNull("sort")) {
                            itemInfoDO.setSort(json.getInt("sort"));
                        }
                        if (json.has("categortyId") && !json.isNull("categortyId")) {
                            itemInfoDO.setCategoryId(json.getInt("categortyId"));
                        }
                        if (json.has("type") && !json.isNull("type")) {
                            itemInfoDO.setType(json.getInt("type"));
                            if (itemInfoDO.getType() == OpenType.ITEM.getType()) {
                                itemDataObject.setData(itemInfoDO);
                                itemDataObject.setType(ItemDataObjectType.ITEM);
                            }
                            if (itemInfoDO.getType() == OpenType.H5.getType() || itemInfoDO.getType() == OpenType.SEARCH.getType()) {
                                itemDataObject.setData(itemInfoDO);
                                itemDataObject.setType(ItemDataObjectType.H5_OR_SEARCH);
                            }
                        } else {
                            continue;
                        }

                        itemDataObjectList.add(itemDataObject);
                    }
                }
            } else {
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemDataObjectList;
    }

    /**
     * 解析图片轮播数据
     *
     * @param jsonObject
     */
    public List<ItemDataObject> parseBannersJsonObject(JSONObject jsonObject) {
        List<ItemDataObject> itemDataObjectList = new ArrayList<ItemDataObject>();
        try {
            if (null != jsonObject && jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ItemDataObject itemDataObject = new ItemDataObject();
                    JSONObject json = jsonArray.getJSONObject(i);
                    ItemInfoDO itemInfoDO = new ItemInfoDO();
                    if (json.has("picUrl") && !json.isNull("picUrl")) {
                        itemInfoDO.setPicUrl(json.getString("picUrl"));
                    } else {
                        continue;
                    }
                    if (json.has("title") && !json.isNull("title")) {
                        itemInfoDO.setTitle(json.getString("title"));
                    }
                    if (json.has("price") && !json.isNull("price")) {
                        itemInfoDO.setPrice(json.getString("price"));
                    }
                    if (json.has("discountPrice") && !json.isNull("discountPrice")) {
                        itemInfoDO.setPromotionPrice(json.getString("discountPrice"));
                    }
                    if (json.has("favorCount") && !json.isNull("favorCount")) {
                        itemInfoDO.setFavorCount(json.getString("favorCount"));
                    }
                    if (json.has("name") && !json.isNull("name")) {
                        itemInfoDO.setName(json.getString("name"));
                    }
                    if (json.has("keyword") && !json.isNull("keyword")) {
                        itemInfoDO.setKeyword(json.getString("keyword"));
                    }
                    if (json.has("h5Url") && !json.isNull("h5Url")) {
                        itemInfoDO.setH5Url(json.getString("h5Url"));
                    }
                    if (json.has("itemId") && !json.isNull("itemId")) {
                        itemInfoDO.setItemId(json.getLong("itemId"));
                    }
                    if (json.has("tbItemId") && !json.isNull("tbItemId")) {
                        itemInfoDO.setTbItemId(json.getString("tbItemId"));
                    }
                    if (json.has("sort") && !json.isNull("sort")) {
                        itemInfoDO.setSort(json.getInt("sort"));
                    }
                    if (json.has("categortyId") && !json.isNull("categortyId")) {
                        itemInfoDO.setCategoryId(json.getInt("categortyId"));
                    }
                    if (json.has("type") && !json.isNull("type")) {
                        itemInfoDO.setType(json.getInt("type"));
                        if (itemInfoDO.getType() == OpenType.ITEM.getType()) {
                            itemDataObject.setData(itemInfoDO);
                            itemDataObject.setType(ItemDataObjectType.ITEM);
                        }
                        if (itemInfoDO.getType() == OpenType.H5.getType() || itemInfoDO.getType() == OpenType.SEARCH.getType()) {
                            itemDataObject.setData(itemInfoDO);
                            itemDataObject.setType(ItemDataObjectType.H5_OR_SEARCH);
                        }
                    } else {
                        continue;
                    }

                    itemDataObjectList.add(itemDataObject);
                }
            } else {
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemDataObjectList;
    }


    /**
     * 展示一个特定颜色的Toast
     *
     * @param message
     */
    protected void toast(String message) {
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
        Toast toast = new Toast(context);
        toast.setView(toastRoot);
        TextView tv = (TextView) toastRoot.findViewById(R.id.toast_notice);
        tv.setText(message);
        toast.show();
    }

}
