package com.taobao.tae.buyingdemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.alibaba.demo.common.AndroidSecretUtil;
import com.alibaba.demo.common.Parameter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.adapter.ParentCategoryAdapter;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.fragment.ChildCategoryFragment;
import com.taobao.tae.buyingdemo.model.*;
import com.taobao.tae.buyingdemo.util.NetWorkStateUtil;
import com.taobao.tae.buyingdemo.util.VolleySingleton;
import com.taobao.tae.buyingdemo.view.ParentCategoryListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 下午4:13
 */
public class CategoryActivity extends FragmentActivity {

    public String TAG = CategoryActivity.class.getName();
    private Context context;
    private RequestQueue requestQueue;
    private ParentCategoryAdapter parentCategoryAdapter;
    private ParentCategoryListView parentCategoryListView;
    //父分类数据
    private List<ItemDataObject> parentItemDataObjectList;
    //子分类Fragment
    private Map<Integer, ChildCategoryFragment> childFragmentMap;
    //当前选中的父分类下标
    private int currentSelectedParentCategoryIndex;
    private int lastSelectedChildFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_layout);
        context = getApplicationContext();
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        childFragmentMap = new HashMap<Integer, ChildCategoryFragment>();
        initParentCategories();
        addListener();
    }


    /**
     * 初始化父分类
     */
    public void initParentCategories() {
        parentCategoryListView = (ParentCategoryListView) findViewById(R.id.category_tree_list);
        parentCategoryAdapter = new ParentCategoryAdapter(this);
        parentCategoryListView.setAdapter(parentCategoryAdapter);
        getParentCategories();
        parentCategoryOnClick();
    }

    /**
     * 初始化子分类
     */
    public void initChildCategories(int parentCategoryId) {
        getChildCategories(parentCategoryId);
    }


    public void addListener() {
        RelativeLayout searchView = (RelativeLayout) findViewById(R.id.category_search_input_view_id);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast(getResources().getString(R.string.search_default_text));
            }
        });
    }

    /**
     * 获取父分类
     */
    protected void getParentCategories() {
        if (NetWorkStateUtil.isNoConnected(context)) {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            return;
        }
        String url = buildParentCategoriesUrl();
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            renderParentCategoriesView(parseCategoriesJson((JSONObject) o));
                        } catch (Exception e) {
                            toast(MsgConfig.GET_ITEMS_FAILURE);
                            if (e != null) {
                                Log.e(TAG, e.getMessage());
                            }
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
     * 获取子分类
     */
    protected void getChildCategories(int parentCategoryId) {
        if (NetWorkStateUtil.isNoConnected(context)) {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            return;
        }
        String url = buildChildCategoriesUrl(parentCategoryId);
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            renderChildCategoriesView(parseCategoriesJson((JSONObject) o));
                        } catch (Exception e) {
                            toast(MsgConfig.GET_ITEMS_FAILURE);
                            if (e != null) {
                                Log.e(TAG, e.getMessage());
                            }
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
     * 用户点击父分类事件
     */
    private void parentCategoryOnClick() {
        parentCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastSelectedChildFragmentIndex = currentSelectedParentCategoryIndex;
                changeSelectCategoryView(i);
                if (parentItemDataObjectList.get(i) != null) {
                    CategoryDO categoryDO = (CategoryDO) parentItemDataObjectList.get(i).getData();
                    initChildCategories(categoryDO.getId());
                } else {
                    toast(MsgConfig.SYSTEM_ERROR);
                }
            }
        });
    }


    /**
     * 用户点击分类后，改变主分类列表样式
     *
     * @param position
     */
    private void changeSelectCategoryView(int position) {
        if (currentSelectedParentCategoryIndex == position) {
            return;
        } else {
            unSelectedParentCategoryItem(currentSelectedParentCategoryIndex);
        }
        ParentCategoryAdapter.selectCategoryIndex = position;
        selectParentCategoryItem(position);
    }

    /**
     * 取消上一次选中分类的边框颜色
     *
     * @param position
     */
    private void unSelectedParentCategoryItem(int position) {
        parentCategoryListView.setItemChecked(position, false);
        View view = parentCategoryListView.getViewByPosition(position);
        View leftIndicatorLine = view.findViewById(R.id.left_indicator_line);
        View rightIndicatorLine = view.findViewById(R.id.right_indicator_line);
        leftIndicatorLine.setVisibility(View.GONE);
        rightIndicatorLine.setVisibility(View.VISIBLE);
        RelativeLayout parentLayout = (RelativeLayout) view.findViewById(R.id.category_parent_btn_ly);
        parentLayout.setBackgroundColor(view.getResources().getColor(R.color.pinterest_backgroud));
    }

    /**
     * 设置本次选中的分类的边框颜色
     *
     * @param position 分类下标
     */
    private void selectParentCategoryItem(int position) {
        parentCategoryListView.setItemChecked(position, true);
        View view = parentCategoryListView.getViewByPosition(position);
        View leftIndicatorLine = view.findViewById(R.id.left_indicator_line);
        View rightIndicatorLine = view.findViewById(R.id.right_indicator_line);
        leftIndicatorLine.setVisibility(View.VISIBLE);
        rightIndicatorLine.setVisibility(View.GONE);
        RelativeLayout parentLayout = (RelativeLayout) view.findViewById(R.id.category_parent_btn_ly);
        parentLayout.setBackgroundColor(view.getResources().getColor(R.color.white));
        currentSelectedParentCategoryIndex = position;
    }


    /**
     * 生成 父分类的URL
     *
     * @return
     */
    private String buildParentCategoriesUrl() {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);
            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_PARENT_CATEGORY);
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
     * 生成 子分类的URL
     *
     * @return
     */
    private String buildChildCategoriesUrl(int parentCategoryId) {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            parameters.add(new Parameter(ApiConfig.CATEGORY_ID, String.valueOf(parentCategoryId)));
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);
            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_CHILD_CATEGORY);
            path.append("?").append(ApiConfig.SERVER_KEY_NAME).append("=").append(AppConfig.SERVER_KEY);
            path.append("&").append(ApiConfig.CATEGORY_ID).append("=").append(parentCategoryId);
            path.append("&").append(ApiConfig.TIME_STAMP_NAME).append("=").append(timstamp);
            path.append("&").append(ApiConfig.SIGN_NAME).append("=").append(token);
        } catch (IOException e) {
            e.printStackTrace();
            toast(MsgConfig.SYSTEM_ERROR);
        }
        return path.toString();
    }

    /**
     * 解析分类数据
     *
     * @param jsonObject
     */
    public ArrayList<ItemDataObject> parseCategoriesJson(JSONObject jsonObject) {
        ArrayList<ItemDataObject> itemDataObjectList = new ArrayList<ItemDataObject>();
        try {
            if (null != jsonObject && jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ItemDataObject itemDataObject = new ItemDataObject();
                    JSONObject json = jsonArray.getJSONObject(i);
                    CategoryDO categoryDO = new CategoryDO();

                    if (json.has("id") && !json.isNull("id")) {
                        categoryDO.setId(json.getInt("id"));
                    }
                    if (json.has("name") && !json.isNull("name")) {
                        categoryDO.setName(json.getString("name"));
                    }
                    if (json.has("father") && !json.isNull("father")) {
                        categoryDO.setFather(json.getInt("father"));
                    }
                    if (json.has("pic") && !json.isNull("pic")) {
                        categoryDO.setPic(json.getString("pic"));
                    }
                    if (json.has("sequence") && !json.isNull("sequence")) {
                        categoryDO.setSequence(json.getInt("sequence"));
                    }
                    if (json.has("gmtCreated") && !json.isNull("gmtCreated")) {
                        categoryDO.setGmtCreated(json.getString("gmtCreated"));
                    }
                    if (json.has("gmtModified") && !json.isNull("gmtModified")) {
                        categoryDO.setGmtModified(json.getString("gmtModified"));
                    }
                    itemDataObject.setData(categoryDO);
                    itemDataObjectList.add(itemDataObject);
                }
            } else {
                toast(MsgConfig.GET_CATEGORY_FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemDataObjectList;
    }

    /**
     * 渲染结果页面
     */
    public void renderParentCategoriesView(List<ItemDataObject> itemDataObjectList) {
        //渲染右侧默认的分类列表
        parentItemDataObjectList = itemDataObjectList;
        if (itemDataObjectList != null && itemDataObjectList.size() > ParentCategoryAdapter.selectCategoryIndex) {
            ItemDataObject itemDataObject = itemDataObjectList.get(ParentCategoryAdapter.selectCategoryIndex);
            if (itemDataObject.getData() != null) {
                CategoryDO categoryDO = (CategoryDO) itemDataObject.getData();
                int categoryId = categoryDO.getId();
                initChildCategories(categoryId);
            }

        }
        //渲染左侧主分类列表
        parentCategoryAdapter.addCategory(itemDataObjectList);
        parentCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 渲染子分类列表
     *
     * @param itemDataObjectList
     */
    public void renderChildCategoriesView(ArrayList<ItemDataObject> itemDataObjectList) {
        if (childFragmentMap.containsKey(currentSelectedParentCategoryIndex)) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(childFragmentMap.get(lastSelectedChildFragmentIndex)).show(childFragmentMap.get(currentSelectedParentCategoryIndex)).commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ChildCategoryFragment.CHILD_CATEGORIES_TAG, itemDataObjectList);
            ChildCategoryFragment childCategoryFragment = ChildCategoryFragment.newInstance(bundle);
            childFragmentMap.put(currentSelectedParentCategoryIndex, childCategoryFragment);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (currentSelectedParentCategoryIndex == 0 && fragmentTransaction.isEmpty()) {
                fragmentTransaction.add(R.id.child_category_content, childCategoryFragment).commit();
            } else {
                fragmentTransaction.hide(childFragmentMap.get(lastSelectedChildFragmentIndex)).add(R.id.child_category_content, childCategoryFragment).commit();
            }

        }

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
