package com.taobao.tae.buyingdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.taobao.tae.buyingdemo.adapter.SearchStaggeredAdapter;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.model.ItemInfoDO;
import com.taobao.tae.buyingdemo.util.NetWorkStateUtil;
import com.taobao.tae.buyingdemo.widget.pinterest.ActionType;
import com.taobao.tae.buyingdemo.widget.pinterest.MultiColumnPullToRefreshListView;
import com.taobao.tae.buyingdemo.widget.pinterest.PinterestAdapterView;
import com.taobao.tae.buyingdemo.widget.pinterest.PinterestListView;
import com.taobao.tae.buyingdemo.util.StringUtils;
import com.taobao.tae.buyingdemo.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>商品结果根据不同的方式进行排序</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/21
 * Time: 上午10:29
 */
public class SearchResultFragment extends Fragment implements MultiColumnPullToRefreshListView.IPinterestListViewListener {

    public String TAG = SearchResultFragment.class.getName();
    public static String SORT_TYPE_TAG = "sortType";
    public static String CATEGORY_ID_TAG = "categoryId";
    public static String KEY_WORD_TAG = "keyword";
    public MultiColumnPullToRefreshListView multiColumnPullToRefreshListView = null;
    public SearchStaggeredAdapter searchStaggeredAdapter = null;
    private Context context;
    private RequestQueue requestQueue;
    private View view;
    private int currentPage = 1;
    private Boolean onPause = false;
    /*默认为 1 */
    private int sortType = 1;
    private int categoryId = AppConfig.ERROR_CODE;
    private String keyword;


    public static SearchResultFragment newInstance(Bundle bundle) {
        SearchResultFragment newFragment = new SearchResultFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public SearchResultFragment() {
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity().getApplicationContext();
        Bundle bundle = getArguments();
        this.sortType = bundle.getInt(SORT_TYPE_TAG);
        this.categoryId = bundle.getInt(CATEGORY_ID_TAG);
        this.keyword = bundle.getString(KEY_WORD_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        initView();
        return view;
    }


    /**
     * 初始化商品列表
     */
    public void initView() {
        multiColumnPullToRefreshListView = (MultiColumnPullToRefreshListView) view.findViewById(R.id.search_items);
        multiColumnPullToRefreshListView.setPullLoadEnable(true);
        multiColumnPullToRefreshListView.setPullRefreshEnable(false);
        multiColumnPullToRefreshListView.setVerticalScrollBarEnabled(false);
        multiColumnPullToRefreshListView.setXListViewListener(this);
        multiColumnPullToRefreshListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        searchStaggeredAdapter = new SearchStaggeredAdapter(context, 2);
        multiColumnPullToRefreshListView.setAdapter(searchStaggeredAdapter);
        addItemToContainer(currentPage, ActionType.PULL_REFRESH_ACTION);
        itemOnClick();
        initBackground();
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
        String url = getSearchRequestUrl(page);
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            renderView(parseJsonObject((JSONObject) o), actionType);
                        } catch (Exception e) {
                            toast(MsgConfig.GET_ITEMS_FAILURE);
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (actionType == ActionType.PULL_REFRESH_ACTION) {
                    multiColumnPullToRefreshListView.stopRefresh();
                }
                if (actionType == ActionType.VIEW_MORE_ITEMS_ACTION) {
                    multiColumnPullToRefreshListView.stopLoadMore();
                }
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        }
        ));
    }


    /**
     * 用户点击商品图片，进入商品详情页
     */
    private void itemOnClick() {
        multiColumnPullToRefreshListView.setOnItemClickListener(new PinterestAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PinterestAdapterView<?> parent, View view,
                                    int position, long id) {
                PinterestListView gv = (PinterestListView) parent;
                Bundle bundle = new Bundle();
                ItemDataObject itemDataObject = (ItemDataObject) gv.getItemAtPosition(position);
                ItemInfoDO itemInfoDO = (ItemInfoDO) itemDataObject.getData();
                //跳转到Native商品详情页
                bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_category);
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

        });
    }

    /**
     * 初始化背景
     */
    private void initBackground() {
        if (searchStaggeredAdapter.getCount() > 0) {
            LinearLayout viewMoreLayout = (LinearLayout) multiColumnPullToRefreshListView.findViewById(R.id.view_more_content);
            viewMoreLayout.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 当下拉刷新时，重置页面，取最新记录。
     */
    @Override
    public void onRefresh() {
        addItemToContainer(0, ActionType.PULL_REFRESH_ACTION);
    }

    @Override
    public void onLoadMore() {
        addItemToContainer(++currentPage, ActionType.VIEW_MORE_ITEMS_ACTION);
    }

    /**
     * 渲染结果页面
     */
    public void renderView(List<ItemDataObject> itemDataObjectList, int actionType) {
        if (actionType == ActionType.PULL_REFRESH_ACTION) {
            searchStaggeredAdapter.addItemTop(itemDataObjectList);
            searchStaggeredAdapter.notifyDataSetChanged();
            multiColumnPullToRefreshListView.stopRefresh();
        }
        if (actionType == ActionType.VIEW_MORE_ITEMS_ACTION) {
            multiColumnPullToRefreshListView.stopLoadMore();
            searchStaggeredAdapter.addItemLast(itemDataObjectList);
            searchStaggeredAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 获取请求参数
     *
     * @param page
     * @return
     */
    private String getSearchRequestUrl(int page) {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.PAGE_NAME, String.valueOf(page)));
            parameters.add(new Parameter(ApiConfig.PAGE_SIZE_NAME, String.valueOf(AppConfig.PAGE_SIZE)));
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            if (StringUtils.isNotEmpty(keyword)) {
                parameters.add(new Parameter(ApiConfig.SEARCH_KEYWORD, keyword));
            }
            if (categoryId != AppConfig.ERROR_CODE) {
                parameters.add(new Parameter(ApiConfig.SUB_CATEGORY_ID, String.valueOf(categoryId)));
            }
            parameters.add(new Parameter(ApiConfig.SEARCH_SORT, String.valueOf(sortType)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);

            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_SEARCH_ITEMS);
            path.append("?").append(ApiConfig.PAGE_NAME).append("=").append(page);
            path.append("&").append(ApiConfig.PAGE_SIZE_NAME).append("=").append(AppConfig.PAGE_SIZE);
            path.append("&").append(ApiConfig.SERVER_KEY_NAME).append("=").append(AppConfig.SERVER_KEY);
            if (StringUtils.isNotEmpty(keyword)) {
                try {
                    String encodeKeyWord = URLEncoder.encode(keyword, AppConfig.ENCODING);
                    path.append("&").append(ApiConfig.SEARCH_KEYWORD).append("=").append(encodeKeyWord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                }

            }
            if (categoryId != AppConfig.ERROR_CODE) {
                path.append("&").append(ApiConfig.SUB_CATEGORY_ID).append("=").append(categoryId);
            }
            path.append("&").append(ApiConfig.SEARCH_SORT).append("=").append(sortType);
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
    public List<ItemDataObject> parseJsonObject(JSONObject jsonObject) {
        List<ItemDataObject> itemDataObjectList = new ArrayList<ItemDataObject>();
        try {
            if (null != jsonObject && jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ItemDataObject itemDataObject = new ItemDataObject();
                    JSONObject json = jsonArray.getJSONObject(i);
                    ItemInfoDO itemInfoDO = new ItemInfoDO();
                    if (json.has("pic") && !json.isNull("pic")) {
                        itemInfoDO.setPicUrl(json.getString("pic"));
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
                    if (json.has("itemId") && !json.isNull("itemId")) {
                        itemInfoDO.setItemId(json.getLong("itemId"));
                    }
                    if (json.has("tbItemId") && !json.isNull("tbItemId")) {
                        itemInfoDO.setTbItemId(json.getString("tbItemId"));
                    }
                    if (json.has("sort") && !json.isNull("sort")) {
                        itemInfoDO.setSort(json.getInt("sort"));
                    }
                    itemDataObject.setData(itemInfoDO);
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
