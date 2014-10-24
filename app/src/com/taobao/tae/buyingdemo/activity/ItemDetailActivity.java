package com.taobao.tae.buyingdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.demo.common.AndroidSecretUtil;
import com.alibaba.demo.common.Parameter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.app.BuyingDemoApplication;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.constant.ManufacturerType;
import com.taobao.tae.buyingdemo.constant.MsgConfig;
import com.taobao.tae.buyingdemo.fragment.DetailPicWordFragment;
import com.taobao.tae.buyingdemo.model.*;
import com.taobao.tae.buyingdemo.util.BitmapCache;
import com.taobao.tae.buyingdemo.util.NetWorkStateUtil;
import com.taobao.tae.buyingdemo.util.StringUtils;
import com.taobao.tae.buyingdemo.util.VolleySingleton;
import com.taobao.tae.buyingdemo.view.AutoAdjustHeightImageView;
import com.taobao.tae.buyingdemo.view.LazyScrollView;
import com.taobao.tae.buyingdemo.view.UnderLineTextIndicator;
import com.taobao.tae.sdk.TaeSDK;
import com.taobao.tae.sdk.callback.CallbackContext;
import com.taobao.tae.sdk.callback.LoginCallback;
import com.taobao.tae.sdk.callback.TradeProcessCallback;
import com.taobao.tae.sdk.model.OrderItem;
import com.taobao.tae.sdk.model.Session;
import com.taobao.tae.sdk.model.TradeResult;
import com.taobao.tae.sdk.webview.TaeWebViewUiSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/14
 * Time: 下午4:36
 */
public class ItemDetailActivity extends FragmentActivity {

    public String TAG = ItemDetailActivity.class.getName();

    private Activity thisActivity;
    private Context context;
    /*首次点击返回时间*/
    private long firstClickBackTime = 0;
    /* 默认展示第一个 */
    private static final int DEFAULT_INDEX = 0;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private String taobaoItemId;
    //混淆商品ID
    private String openiid;
    //店铺基本信息
    private ShopDO shopDO;
    //商品基本信息
    private ItemInfoDO itemBasicInfoDO;
    //邮费信息，默认展示第一个（一般为快递）
    private ArrayList<DeliveryDO> deliveries;
    //图文描述
    private ArrayList<ItemDescriptionDO> itemDescriptionDOs;
    //默认展示的商品价格
    private ItemPriceUnit defaultItemPriceUnit;
    //商品价格与SKU的关联Map,key为sku
    private Map<String, ItemPriceUnit> skuPriceMap;
    //商品总库存
    private String itemQuantity;
    //商品库存与SKU的关联Map，key为sku
    private Map<String, ItemStockUnit> skuStockMap;
    //商品SKU组合的Map，key为skuId
    private Map<String, SkuAssembleUnit> skuAssembleMap;
    //商品SKU属性Map
    private Map<String, SkuPropertyUnit> skuPropertyMap;
    //图文信息等下的指示器
    private UnderLineTextIndicator underLineTextIndicator;
    // SKU 浮层
    private PopupWindow popupWindow;
    private View popUpView;
    private RelativeLayout itemDetailSkuPanel;
    private RelativeLayout itemDetailPanel;
    //SKU弹出浮层后的半透明背景
    private View skuPropertiesBackgroudShadowView;
    //图文详情
    private DetailPicWordFragment detailPicWordFragment;
    private ImageView addItemCountImageView;
    private ImageView reduceItemCountImageView;
    private SkuSelect skuSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail_frame_layout);
        context = getApplicationContext();
        thisActivity = this;
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        itemDetailPanel = (RelativeLayout) findViewById(R.id.item_detail_all_layout);
        initView();
        addBtnListener();
    }

    public void initView() {
        taobaoItemId = getIntent().getStringExtra(ApiConfig.TAOBAO_ITEM_ID);
        LinearLayout underlineTextLayout = (LinearLayout) findViewById(R.id.item_detail_more_info_underline_text_indicator);
        underLineTextIndicator = new UnderLineTextIndicator(this);
        underLineTextIndicator.setTextSizeResourceId(R.dimen.detail_more_info_title_size);
        underLineTextIndicator.setTitlePaddingBottomLine(getResources().getDimensionPixelSize(R.dimen.detail_more_info_title_bottom_line_margin_top));
        underLineTextIndicator.setLineHeight(getResources().getDimensionPixelSize(R.dimen.detail_more_info_title_bottom_line_height));
        underLineTextIndicator.initView(this, AppConfig.ITEM_DETAIL_MORE_INFO_TITLE, UnderLineTextIndicator.SAPCE_EQUAL_APPAND, R.dimen.detail_more_info_title_underline_append_size);
        underlineTextLayout.addView(underLineTextIndicator);
        initBasicInfo();
        initItemDetail();
    }

    private void initBasicInfo() {
        String pic = getIntent().getStringExtra("pic");
        String title = getIntent().getStringExtra("title");
        AutoAdjustHeightImageView mainImageView = (AutoAdjustHeightImageView) findViewById(R.id.item_detail_main_pic);
        mainImageView.setDetailMainImage(true);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(mainImageView, R.drawable.item_image_empty, R.drawable.item_image_fail);
        imageLoader.get(pic, listener);
        TextView titleView = (TextView) findViewById(R.id.item_detail_title_txt);
        if (title != null && title.length() > 18) {
            titleView.setText(title.substring(0, 18).concat("..."));
        }else{
            titleView.setText(title);
        }
    }

    private void addBtnListener() {
        RelativeLayout backRelativeLayout = (RelativeLayout) findViewById(R.id.item_detail_top_back_btn);
        backRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button buyButton = (Button) findViewById(R.id.item_detail_buy_btn);
        buyButton.setOnClickListener(new BuyNowClickListener());
    }


    /**
     * 立即购买 监听器
     */
    private class BuyNowClickListener implements View.OnClickListener {
        public void onClick(View v) {
            if (NetWorkStateUtil.isConnected(context) && itemBasicInfoDO == null) {
                return;
            }
            if (NetWorkStateUtil.isNoConnected(context)) {
                toast(MsgConfig.NO_NETWORK_CONNECTION);
                return;
            }
            setBackgroudShadowView();
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popUpView = layoutInflater.inflate(R.layout.detail_sku_select_panel, null);
            itemDetailSkuPanel = (RelativeLayout) popUpView.findViewById(R.id.item_detail_sku_panel);
            //针对魅族手机的底部smartbar做兼容处理
            if (android.os.Build.MANUFACTURER.equalsIgnoreCase(ManufacturerType.MEIZU)) {
                TextView autoHigtView = (TextView) popUpView.findViewById(R.id.item_detail_popup_auto_hight);
                autoHigtView.getLayoutParams().height = 100;
            }
            popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(R.style.item_sku_animation);//设置淡入淡出动画效果
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            initItemThumbnail();
            initSkuProperties(popUpView);
            listenConfirmButton();
            RelativeLayout confirmButtonLayout = (RelativeLayout) popUpView.findViewById(R.id.item_detail_buy_bottom_layout);
            confirmButtonLayout.setVisibility(View.VISIBLE);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    itemDetailPanel.removeView(skuPropertiesBackgroudShadowView);
                }
            });
        }

    }

    /**
     * 商品SKU属性弹出时，设置除SKU视图部分为半透明
     */
    private void setBackgroudShadowView() {
        skuPropertiesBackgroudShadowView = new View(ItemDetailActivity.this);
        skuPropertiesBackgroudShadowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        skuPropertiesBackgroudShadowView.setBackgroundColor(getResources().getColor(R.color.detail_sku_out_bg_shadow));
        itemDetailPanel.addView(skuPropertiesBackgroudShadowView);
    }


    /**
     * 初始化 商品SKU 浮层
     *
     * @param view
     */
    public void initSkuProperties(View view) {
        addItemCountImageView = (ImageView) popUpView.findViewById(R.id.item_buy_count_add_btn);
        reduceItemCountImageView = (ImageView) popUpView.findViewById(R.id.item_buy_count_reduce_btn);
        final TextView selectSkuNotice = (TextView) popUpView.findViewById(R.id.item_detail_sku_select_notice);
        Display display = getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - 2 * getResources().getDimensionPixelSize(R.dimen.detail_sku_panel_dynamic_fill_margin_left_right);
        int buttonHeight = getResources().getDimensionPixelSize(R.dimen.detail_sku_property_btn_height);
        int skuTitleMarginTop = getResources().getDimensionPixelSize(R.dimen.detail_sku_title_margin_top);
        int skuLineMarginTop = getResources().getDimensionPixelSize(R.dimen.detail_sku_line_margin_top);
        int skuBtnRightMargin = getResources().getDimensionPixelSize(R.dimen.detail_sku_property_btn_margin_right);
        int skuBtnTopMargin = getResources().getDimensionPixelSize(R.dimen.detail_sku_property_btn_margin_top);
        int skuLineHeight = getResources().getDimensionPixelSize(R.dimen.detail_sku_line_height);
        LinearLayout.LayoutParams skuTitleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        skuTitleLayoutParams.setMargins(0, skuTitleMarginTop, 0, 0);
        //如果商品存在SKU属性
        if (itemBasicInfoDO.isHasSKU()) {
            skuSelect = new SkuSelect();
            selectSkuNotice.setVisibility(View.VISIBLE);
            LinearLayout skuPropertiesLayout = (LinearLayout) popUpView.findViewById(R.id.item_detail_dynamic_sku_properties);
            final TextView itemCountTextView = (TextView) popUpView.findViewById(R.id.item_buy_count);
            final TextView quantityView = (TextView) popUpView.findViewById(R.id.item_deatil_sku_quantity_txt);
            for (Map.Entry entry : skuPropertyMap.entrySet()) {
                final SkuPropertyUnit skuProperty = (SkuPropertyUnit) entry.getValue();
                TextView textView = new TextView(this);
                textView.setText(skuProperty.getPropName());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.detail_sku_title_size));
                textView.setTextColor(getResources().getColor(R.color.global_font));
                textView.setId(Integer.valueOf(skuProperty.getPropId()));
                textView.setLayoutParams(skuTitleLayoutParams);
                skuPropertiesLayout.addView(textView);
                skuSelect.put(skuProperty.getPropId(), skuProperty.getPropName());
                final List<Button> skuViewList = new ArrayList<Button>();
                if (skuProperty.getValues() != null && skuProperty.getValues().entrySet().size() > 0) {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setLayoutParams(layoutParams);

                    LinearLayout newlinearLayout = new LinearLayout(this);
                    newlinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    newlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    newlinearLayout.setLayoutParams(layoutParams);

                    int widthSoFar = 0;

                    for (Map.Entry skuEntry : skuProperty.getValues().entrySet()) {
                        final String skuId = (String) skuEntry.getKey();
                        final SkuPropertyValueUnit propertyValue = (SkuPropertyValueUnit) skuEntry.getValue();
                        final String skuName = propertyValue.getName();
                        final Button skuButton = new Button(this);

                        LinearLayout.LayoutParams skuLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, buttonHeight);
                        skuLayoutParams.setMargins(0, skuBtnTopMargin, skuBtnRightMargin, 0);
                        skuButton.setText(skuName);
                        skuButton.measure(0, 0);
                        skuButton.setId(Integer.valueOf(skuId));
                        skuButton.setClickable(true);
                        skuButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.detail_sku_property_size));
                        skuButton.setTextColor(getResources().getColor(R.color.global_font));
                        skuButton.setLayoutParams(skuLayoutParams);
                        skuButton.setSingleLine();
                        skuButton.setBackgroundResource(R.drawable.sku_button_unselect_bg);
                        skuButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (Button allButton : skuViewList) {
                                    allButton.setTextColor(Color.parseColor("#ff000000"));
                                    allButton.setBackgroundResource(R.drawable.sku_button_unselect_bg);
                                }
                                //允许用户反选
                                SkuSelect.SkuPropertySelect skuPropertySelect = skuSelect.getSkuSelectMap().get(skuProperty.getPropId());
                                if (skuPropertySelect.isSelected() && skuId.equals(skuPropertySelect.getSkuId())) {
                                    selectSkuNotice.setVisibility(View.VISIBLE);
                                    skuPropertySelect.setSelected(false);
                                    addItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_plus_disable));
                                    reduceItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_reduce_disable));
                                    addItemCountImageView.setOnClickListener(null);
                                    reduceItemCountImageView.setOnClickListener(null);
                                    itemCountTextView.setText("1");
                                    quantityView.setText("");
                                    return;
                                }
                                skuButton.setTextColor(getResources().getColor(R.color.detail_sku_select_text_color));
                                skuButton.setBackgroundResource(R.drawable.sku_button_select_bg);
                                /* 当用户选择了所有的SKU分类属性后，商品价格、库存量联动刷新显示 */
                                skuSelect.setSelectedSkuId(skuProperty.getPropId(), skuId, skuName);
                                if (skuSelect.isSelectedAllSkus()) {
                                    selectSkuNotice.setVisibility(View.GONE);
                                    String ppath = skuSelect.getPpath();
                                    SkuAssembleUnit skuAssembleUnit = skuAssembleMap.get(ppath);

                                    if (skuAssembleUnit == null) {
                                        quantityView.setText("(库存0件)");
                                        toast("此属性无商品");
                                        return;
                                    }
                                    final int quantity = Integer.valueOf(skuStockMap.get(skuAssembleUnit.getSkuId()).getQuantity());
                                    quantityView.setText("(库存".concat(skuStockMap.get(skuAssembleUnit.getSkuId()).getQuantity()).concat("件)"));
                                    //更新 商品图片
                                    if (skuProperty.isRelationImage) {
                                        SkuPropertyValueUnit skuPropertyValueUnit = skuProperty.getValues().get(skuId);
                                        ImageView imageView = (ImageView) popUpView.findViewById(R.id.item_detail_sku_sm_img);
                                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.item_image_empty, R.drawable.item_image_fail);
                                        imageLoader.get(skuPropertyValueUnit.getImgUrl(), listener);
                                    }
                                    //更新商品价格
                                    ItemPriceUnit itemPriceUnit = skuPriceMap.get(skuAssembleUnit.getSkuId());
                                    if (itemPriceUnit != null) {
                                        TextView priceView = (TextView) popUpView.findViewById(R.id.item_deatil_sku_price_txt);
                                        priceView.setTypeface(Typeface.createFromAsset(BuyingDemoApplication.getInstance().getAssetsss(), AppConfig.NUMBER_FONT_NAME));
                                        if (itemPriceUnit.getPrice().equals(itemPriceUnit.getPromotionPrice())) {
                                            priceView.setText("￥".concat(itemPriceUnit.getPrice()));
                                        } else {
                                            priceView.setText("￥".concat(itemPriceUnit.getPromotionPrice()));
                                        }
                                    }
                                    if (quantity > 1) {
                                        addItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_plus_enable));
                                    }
                                    addItemCountImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Integer itemCount = Integer.valueOf(itemCountTextView.getText().toString());
                                            if (itemCount == quantity) {
                                                return;
                                            }
                                            itemCount = itemCount + 1;
                                            itemCountTextView.setText(itemCount.toString());
                                            if (itemCount > 1) {
                                                reduceItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_reduce_enable));
                                            }
                                        }
                                    });
                                    reduceItemCountImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Integer itemCount = Integer.valueOf(itemCountTextView.getText().toString());
                                            if (itemCount == 1) {
                                                return;
                                            }
                                            itemCount = itemCount - 1;
                                            itemCountTextView.setText(itemCount.toString());
                                            if (itemCount == 1) {
                                                reduceItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_reduce_disable));
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        skuViewList.add(skuButton);
                        skuButton.measure(0, 0);
                        widthSoFar += skuButton.getMeasuredWidth();
                        widthSoFar += skuBtnRightMargin;
                        if (widthSoFar >= maxWidth) {
                            linearLayout.addView(newlinearLayout);
                            newlinearLayout = new LinearLayout(this);
                            newlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            newlinearLayout.setLayoutParams(layoutParams);
                            newlinearLayout.addView(skuButton);
                            widthSoFar = skuButton.getMeasuredWidth();
                        } else {
                            newlinearLayout.addView(skuButton);
                        }

                    }
                    linearLayout.addView(newlinearLayout);
                    skuPropertiesLayout.addView(linearLayout);
                }

                View lineView = new View(this);
                lineView.setBackgroundColor(getResources().getColor(R.color.global_single_line));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, skuLineHeight);
                layoutParams.setMargins(0, skuLineMarginTop, 0, 0);
                lineView.setLayoutParams(layoutParams);
                skuPropertiesLayout.addView(lineView);
            }
        } else {
            showThumbnailItemInfoOnNoSkuInfo();
        }
    }


    /**
     * 商品无SKU信息时，只展示总库存
     */
    public void showThumbnailItemInfoOnNoSkuInfo() {
        //不存在商品SKU属性，直接展示商品库存量等信息
        TextView quantityView = (TextView) popUpView.findViewById(R.id.item_deatil_sku_quantity_txt);
        quantityView.setText("(库存".concat(itemQuantity).concat("件)"));
        if (StringUtils.isNotEmpty(itemQuantity) && Integer.valueOf(itemQuantity) > 1) {
            addItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_plus_enable));
            addItemCountImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView itemCountTextView = (TextView) popUpView.findViewById(R.id.item_buy_count);
                    Integer itemCount = Integer.valueOf(itemCountTextView.getText().toString());
                    if (itemCount == Integer.valueOf(itemQuantity)) {
                        return;
                    }
                    itemCount = itemCount + 1;
                    itemCountTextView.setText(itemCount.toString());
                    if (itemCount > 1) {
                        reduceItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_reduce_enable));
                    }
                }
            });

            reduceItemCountImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView itemCountTextView = (TextView) popUpView.findViewById(R.id.item_buy_count);
                    Integer itemCount = Integer.valueOf(itemCountTextView.getText().toString());
                    if (itemCount == 1) {
                        return;
                    }
                    itemCount = itemCount - 1;
                    itemCountTextView.setText(itemCount.toString());
                    if (itemCount == 1) {
                        reduceItemCountImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_count_reduce_disable));
                    }
                }
            });
        } else {
            toast("商品数量不足");
            finish();
        }
    }


    /**
     * 初始化商品SKU选择时顶部的商品缩略信息
     */
    public void initItemThumbnail() {
        String pic = getIntent().getStringExtra("pic");
        String title = getIntent().getStringExtra("title");
        ImageView tagLogoImageView = (ImageView) popUpView.findViewById(R.id.sku_panel_item_source_tag_logo);
        if (shopDO != null) {
            if (ShopType.C.getType().equalsIgnoreCase(shopDO.getShopType())) {
                tagLogoImageView.setBackgroundResource(R.drawable.taobao_logo_tag);
            }
            if (ShopType.B.getType().equalsIgnoreCase(shopDO.getShopType())) {
                tagLogoImageView.setBackgroundResource(R.drawable.tmall_logo_tag);
            }
        }
        TextView titleTextView = (TextView) popUpView.findViewById(R.id.item_deatil_sku_title_txt);
        if (title != null && title.length() > 14) {
            titleTextView.setText(title.substring(0, 14).concat("..."));
        } else {
            titleTextView.setText(title);
        }
        TextView priceTextView = (TextView) popUpView.findViewById(R.id.item_deatil_sku_price_txt);
        priceTextView.setTypeface(Typeface.createFromAsset(BuyingDemoApplication.getInstance().getAssetsss(), AppConfig.NUMBER_FONT_NAME));
        if (defaultItemPriceUnit != null) {
            if (defaultItemPriceUnit.getPrice().equals(defaultItemPriceUnit.getPromotionPriceName())) {
                priceTextView.setText("￥".concat(defaultItemPriceUnit.getPrice()));
            } else {
                priceTextView.setText("￥".concat(defaultItemPriceUnit.getPromotionPrice()));
            }
        }
        ImageView imageThumbnailView = (ImageView) popUpView.findViewById(R.id.item_detail_sku_sm_img);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageThumbnailView, R.drawable.item_image_empty, R.drawable.item_image_fail);
        imageLoader.get(pic, listener);
    }

    /**
     * 监听 商品SKU浮层中的确定按钮
     * 首先判断用户是否选择了所有的SKU属性，如果没有选择，则提示选择。
     * 如果用户选择了所有必选的SKU属性后，则判断是否登录，已登录，创建订单；未登录 ，跳转登录页面。
     */
    public void listenConfirmButton() {
        Button confirmButton = (Button) popUpView.findViewById(R.id.item_detail_buy_confirm_btn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkuAssembleUnit skuAssembleUnit = null;
                if (itemBasicInfoDO.isHasSKU() && skuSelect != null && !skuSelect.isSelectedAllSkus()) {
                    toast("请先选择".concat(skuSelect.getPropNameString()));
                    return;
                }

                if (itemBasicInfoDO.isHasSKU()) {
                    String ppath = skuSelect.getPpath();
                    skuAssembleUnit = skuAssembleMap.get(ppath);
                    if (skuSelect != null && skuSelect.isSelectedAllSkus()) {
                        if (skuAssembleUnit == null) {
                            toast(MsgConfig.NO_ITEM_ON_THIS_SKU.concat(skuSelect.getPropNameString()));
                            return;
                        }
                    }
                }


                TextView itemCountTextView = (TextView) popUpView.findViewById(R.id.item_buy_count);
                final Integer itemCount = Integer.valueOf(itemCountTextView.getText().toString());
                TaeWebViewUiSettings taeWebViewUiSettings = new TaeWebViewUiSettings();
                taeWebViewUiSettings.title = "title";
                if(StringUtils.isEmpty(openiid)){
                    toast("商品信息缺失，购买异常");
                    return;
                }
                List<com.taobao.tae.sdk.model.OrderItem> orderItems = new ArrayList<OrderItem>();
                OrderItem orderItem = new OrderItem();
                orderItem.itemId = openiid;
                orderItem.quantity = itemCount;
                if (itemBasicInfoDO.isHasSKU() && skuAssembleUnit != null) {
                    orderItem.skuId = skuAssembleUnit.getSkuId();
                }
                orderItems.add(orderItem);
                //用户未登录
                if (!TaeSDK.getSession().isLogin()) {
                    clickBuyBtnToLogin(orderItems);
                } else {
                    //创建订单，跳转到订单确认页面
                    toItemOrderConfirm(orderItems);
                }

            }
        });
    }


    /**
     * 用户未登录时，点击购买,跳转到登录界面，登录成功后，跳转到订单确认页面
     */
    public void clickBuyBtnToLogin(final List<com.taobao.tae.sdk.model.OrderItem> orderItems) {
        LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onSuccess(Session session) {
                toItemOrderConfirm(orderItems);
            }

            @Override
            public void onFailure(int i, String s) {
            }
        };
        TaeSDK.showLogin(this, loginCallback);
    }

    /**
     * 跳转到订单确认页面
     */
    public void toItemOrderConfirm(List<com.taobao.tae.sdk.model.OrderItem> orderItems) {
        TradeProcessCallback tradeProcessCallback = new TradeProcessCallback(){
            @Override
            public void onFailure(int i, String s) {
                toast(s);
            }

            @Override
            public void onPaySuccess(TradeResult tradeResult) {

            }
        };
        TaeSDK.showOrder(this, tradeProcessCallback, orderItems);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 获取商品详情
     */
    protected void initItemDetail() {
        if (NetWorkStateUtil.isNoConnected(context)) {
            toast(MsgConfig.NO_NETWORK_CONNECTION);
            return;
        }
        String url = getDetailRequestUrl();
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        try {
                            parseRenderDetailJsonObject((JSONObject) o);
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
     * 获取请求参数
     *
     * @return
     */
    private String getDetailRequestUrl() {
        StringBuilder path = new StringBuilder();
        try {
            String timstamp = String.valueOf(new Date().getTime());
            List<Parameter> parameters = new ArrayList<Parameter>();
            parameters.add(new Parameter(ApiConfig.ID, String.valueOf(taobaoItemId)));
            parameters.add(new Parameter(ApiConfig.SERVER_KEY_NAME, String.valueOf(AppConfig.SERVER_KEY)));
            parameters.add(new Parameter(ApiConfig.TIME_STAMP_NAME, timstamp));
            String token = AndroidSecretUtil.getToken(parameters, AppConfig.SERVER_SECRET);

            path.append(AppConfig.SERVER_DOMAIN);
            path.append(ApiConfig.GET_ITEMS_DETAIL);
            path.append("?").append(ApiConfig.ID).append("=").append(taobaoItemId);
            path.append("&").append(ApiConfig.SERVER_KEY_NAME).append("=").append(AppConfig.SERVER_KEY);
            path.append("&").append(ApiConfig.TIME_STAMP_NAME).append("=").append(timstamp);
            path.append("&").append(ApiConfig.SIGN_NAME).append("=").append(token);
        } catch (IOException e) {
            e.printStackTrace();
            toast(MsgConfig.SYSTEM_ERROR);
        }
        return path.toString();
    }

    /**
     * 解析 并渲染 商品详情
     *
     * @param jsonObject
     */
    public List<ItemDataObject> parseRenderDetailJsonObject(JSONObject jsonObject) {
        List<ItemDataObject> itemDataObjectList = new ArrayList<ItemDataObject>();
        try {
            if (null != jsonObject && jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                if(jsonObject.has("openiid")){
                    openiid = jsonObject.getString("openiid");
                }
                if (jsonObject.has("item")) {
                    JSONObject basicItemInfo = jsonObject.getJSONObject("item");
                    parseBasicItemJsonObject(basicItemInfo);
                    renderBasicItemView();
                }
                if (jsonObject.has("aitaobaoShop")) {
                    JSONObject shopInfo = jsonObject.getJSONObject("aitaobaoShop");
                    parseShopJsonObject(shopInfo);
                    renderShopView();
                }
                if (jsonObject.has("baichuanItem")) {
                    JSONObject baichuanItemInfo = jsonObject.getJSONObject("baichuanItem");
                    parseDeliveryInfoJsonObject(baichuanItemInfo);
                    renderDeliveryInfo();
                    parseItemDescriptionJsonObject(jsonObject);
                    renderItemDescription();
                    parseItemPriceJsonObject(baichuanItemInfo);
                    renderItemDefaultPriceUnit();
                    parseItemStockJsonObject(baichuanItemInfo);
                    parseItemSkuAssembleJsonObject(baichuanItemInfo);
                }
            } else {
                toast(MsgConfig.GET_ITEMS_FAILURE);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return itemDataObjectList;
    }


    /**
     * 解析 店铺基本信息
     *
     * @param jsonObject
     */
    private void parseBasicItemJsonObject(JSONObject jsonObject) {
        itemBasicInfoDO = new ItemInfoDO();
        try {
            if (jsonObject != null) {
                if (jsonObject.has("location")) {
                    itemBasicInfoDO.setLocation(jsonObject.getString("location"));
                }
                if (jsonObject.has("monthlySales")) {
                    itemBasicInfoDO.setMonthlySales(jsonObject.getInt("monthlySales"));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 解析 店铺基本信息
     *
     * @param jsonObject
     */
    private void parseShopJsonObject(JSONObject jsonObject) {
        shopDO = new ShopDO();
        try {
            if (jsonObject != null) {
                if (jsonObject.has("sellerCredit")) {
                    shopDO.setSellerCredit(jsonObject.getString("sellerCredit"));
                }
                if (jsonObject.has("sellerNick")) {
                    shopDO.setSellerNick(jsonObject.getString("sellerNick"));
                }
                if (jsonObject.has("shopTitle")) {
                    shopDO.setShopTitle(jsonObject.getString("shopTitle"));
                }
                if (jsonObject.has("shopType")) {
                    shopDO.setShopType(jsonObject.getString("shopType"));
                }
                if (jsonObject.has("userId")) {
                    shopDO.setUserId(jsonObject.getLong("userId"));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 解析 邮费信息
     *
     * @param jsonObject
     */
    private void parseDeliveryInfoJsonObject(JSONObject jsonObject) {
        try {
            deliveries = new ArrayList<DeliveryDO>();
            if (jsonObject != null && jsonObject.has("deliveryInfo")) {
                JSONArray carriageList = jsonObject.getJSONObject("deliveryInfo").getJSONArray("carriageList");
                for (int i = 0; i < carriageList.length(); i++) {
                    DeliveryDO deliveryDO = new DeliveryDO();
                    JSONObject carry = carriageList.getJSONObject(i);
                    deliveryDO.setName(carry.getString("name"));
                    deliveryDO.setPrice(carry.getString("price"));
                    deliveries.add(deliveryDO);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 解析商品图文信息,如果移动端描述信息存在的话，使用移动端描述信息，否则采用默认的PC描述信息
     */
    private void parseItemDescriptionJsonObject(JSONObject jsonObject) {
        try {
            itemDescriptionDOs = new ArrayList<ItemDescriptionDO>();
            if (jsonObject.has("mobileItemDesc")) {
                JSONArray descriptions = jsonObject.getJSONArray("mobileItemDesc");
                for (int i = 0; i < descriptions.length(); i++) {
                    JSONObject description = descriptions.getJSONObject(i);
                    ItemDescriptionDO itemDescriptionDO = new ItemDescriptionDO();
                    itemDescriptionDO.setType(description.getInt("type"));
                    itemDescriptionDO.setContent(description.getString("content"));
                    itemDescriptionDOs.add(itemDescriptionDO);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }


    /**
     * 解析商品价格属性
     *
     * @param jsonObject
     */
    private void parseItemPriceJsonObject(JSONObject jsonObject) {
        try {
            if (jsonObject.has("itemInfo")) {
                JSONObject itemInfoJson = jsonObject.getJSONObject("itemInfo");
                if (itemInfoJson.has("inSale")) {
                    itemBasicInfoDO.setInSale(itemInfoJson.getBoolean("inSale"));
                    itemBasicInfoDO.setHasSKU(itemInfoJson.getBoolean("skuItem"));
                }
            }
            if (jsonObject.has("priceInfo")) {
                JSONObject priceJson = jsonObject.getJSONObject("priceInfo");
                if (priceJson.has("itemPrice")) {
                    JSONObject itemPrice = priceJson.getJSONObject("itemPrice");
                    defaultItemPriceUnit = new ItemPriceUnit();
                    defaultItemPriceUnit.setPrice(itemPrice.getJSONObject("price").getString("price"));
                    defaultItemPriceUnit.setPriceName(itemPrice.getJSONObject("price").getString("name"));
                    if (itemPrice.has("promotionPrice")) {
                        defaultItemPriceUnit.setPromotionPrice(itemPrice.getJSONObject("promotionPrice").getString("price"));
                        defaultItemPriceUnit.setPromotionPriceName(itemPrice.getJSONObject("promotionPrice").getString("name"));
                    }
                }

                if (priceJson.has("skuPriceList")) {
                    JSONArray skuPrice = priceJson.getJSONArray("skuPriceList");
                    skuPriceMap = new HashMap<String, ItemPriceUnit>();
                    for (int i = 0; i < skuPrice.length(); i++) {
                        JSONObject price = skuPrice.getJSONObject(i);
                        ItemPriceUnit priceUnit = new ItemPriceUnit();
                        String skuId = price.getString("skuId");
                        priceUnit.setSkuId(skuId);
                        priceUnit.setPrice(price.getJSONObject("price").getString("price"));
                        priceUnit.setPriceName(price.getJSONObject("price").getString("name"));
                        if (price.has("promotionPrice")) {
                            priceUnit.setPromotionPrice(price.getJSONObject("promotionPrice").getString("price"));
                            priceUnit.setPromotionPriceName(price.getJSONObject("promotionPrice").getString("name"));
                        }
                        skuPriceMap.put(skuId, priceUnit);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }


    /**
     * 解析商品库存属性
     *
     * @param jsonObject
     */
    private void parseItemStockJsonObject(JSONObject jsonObject) {
        try {
            if (jsonObject.has("stockInfo")) {
                JSONObject stockInfoJson = jsonObject.getJSONObject("stockInfo");
                if (stockInfoJson.has("itemQuantity")) {
                    itemQuantity = stockInfoJson.getString("itemQuantity");
                }
                if (stockInfoJson.has("skuQuantityList")) {
                    JSONArray skuStock = stockInfoJson.getJSONArray("skuQuantityList");
                    skuStockMap = new HashMap<String, ItemStockUnit>();
                    for (int i = 0; i < skuStock.length(); i++) {
                        JSONObject stock = skuStock.getJSONObject(i);
                        ItemStockUnit stockUnit = new ItemStockUnit();
                        String skuId = stock.getString("skuId");
                        stockUnit.setSkuId(skuId);
                        stockUnit.setQuantity(stock.getString("quantity"));
                        skuStockMap.put(skuId, stockUnit);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 解析商品SKU组合
     *
     * @param jsonObject
     */
    private void parseItemSkuAssembleJsonObject(JSONObject jsonObject) {
        try {
            if (jsonObject.has("skuInfo")) {
                JSONObject skuInfoJson = jsonObject.getJSONObject("skuInfo");
                if (skuInfoJson.has("pvMapSkuList")) {
                    JSONArray pvArray = skuInfoJson.getJSONArray("pvMapSkuList");
                    skuAssembleMap = new HashMap<String, SkuAssembleUnit>();
                    for (int i = 0; i < pvArray.length(); i++) {
                        JSONObject pv = pvArray.getJSONObject(i);
                        SkuAssembleUnit skuAssembleUnit = new SkuAssembleUnit();
                        skuAssembleUnit.setSkuId(pv.getString("skuId"));
                        skuAssembleUnit.setAssemble(pv.getString("pv"));
                        skuAssembleMap.put(pv.getString("pv"), skuAssembleUnit);
                    }
                }
                if (skuInfoJson.has("skuProps")) {
                    JSONArray propertyArray = skuInfoJson.getJSONArray("skuProps");
                    skuPropertyMap = new HashMap<String, SkuPropertyUnit>();
                    for (int i = 0; i < propertyArray.length(); i++) {
                        JSONObject property = propertyArray.getJSONObject(i);
                        SkuPropertyUnit skuPropertyUnit = new SkuPropertyUnit();
                        if (property.has("propId")) {
                            skuPropertyUnit.setPropId(property.getString("propId"));
                        }
                        if (property.has("propName")) {
                            skuPropertyUnit.setPropName(property.getString("propName"));
                        }
                        if (property.has("values")) {
                            JSONArray values = property.getJSONArray("values");
                            Map<String, SkuPropertyValueUnit> skuPropertyValueUnits = new HashMap<String, SkuPropertyValueUnit>();
                            for (int j = 0; j < values.length(); j++) {
                                JSONObject value = values.getJSONObject(j);
                                SkuPropertyValueUnit skuPropertyValueUnit = new SkuPropertyValueUnit();
                                if (value.has("name")) {
                                    skuPropertyValueUnit.setName(value.getString("name"));
                                }
                                if (value.has("valueId")) {
                                    skuPropertyValueUnit.setValueId(value.getString("valueId"));
                                }
                                if (value.has("imgUrl")) {
                                    skuPropertyValueUnit.setImgUrl(value.getString("imgUrl"));
                                    if (!skuPropertyUnit.isRelationImage) {
                                        skuPropertyUnit.setRelationImage(true);
                                    }
                                }
                                skuPropertyValueUnits.put(value.getString("valueId"), skuPropertyValueUnit);
                            }
                            skuPropertyUnit.setValues(skuPropertyValueUnits);
                        }
                        skuPropertyMap.put(property.getString("propId"), skuPropertyUnit);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 渲染基本商品信息
     */
    private void renderBasicItemView() {
        TextView monthLySalesView = (TextView) findViewById(R.id.item_detail_monthly_sales);
        monthLySalesView.setText("月销" + itemBasicInfoDO.getMonthlySales() + "笔");
        TextView locationView = (TextView) findViewById(R.id.item_detail_delivery_location);
        locationView.setText(itemBasicInfoDO.getLocation());
    }

    /**
     * 渲染店铺相关信息
     */
    private void renderShopView() {
        ImageView logoImageView = (ImageView) findViewById(R.id.item_source_tag_logo);
        if (shopDO != null) {
            if (ShopType.C.getType().equalsIgnoreCase(shopDO.getShopType())) {
                logoImageView.setBackgroundResource(R.drawable.taobao_logo_tag);
            }
            if (ShopType.B.getType().equalsIgnoreCase(shopDO.getShopType())) {
                logoImageView.setBackgroundResource(R.drawable.tmall_logo_tag);
            }
        }
    }


    /**
     * 渲染邮费信息
     */
    private void renderDeliveryInfo() {
        TextView textView = (TextView) findViewById(R.id.item_detail_postage);
        if (deliveries != null && deliveries.size() > 0) {
            textView.setText(deliveries.get(0).getName() + " " + deliveries.get(0).getPrice());
        }
    }

    /**
     * 初始化 图文信息、宝贝参数、评价详情等 Tab
     */
    private void renderItemDescription() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailPicWordFragment.ITEM_DESCRIPTIONS_TAG, itemDescriptionDOs);
        detailPicWordFragment = DetailPicWordFragment.newInstance(bundle);
        LazyScrollView lazyScrollView = (LazyScrollView) findViewById(R.id.item_detail_lazy_scroll_view);
        lazyScrollView.getView();
        lazyScrollView.setOnScrollListener(new LazyScrollView.OnScrollListener() {
            @Override
            public void onBottom() {
                detailPicWordFragment.loadMore();
            }

            @Override
            public void onTop() {
            }

            @Override
            public void onScroll() {
            }
        });
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.item_detail_more_info_content, detailPicWordFragment).commit();
    }

    /**
     * 渲染默认的商品价格属性
     */
    private void renderItemDefaultPriceUnit() {
        TextView priceView = (TextView) findViewById(R.id.item_detail_price_txt);
        if (defaultItemPriceUnit != null && !defaultItemPriceUnit.getPrice().equals(defaultItemPriceUnit.getPromotionPrice()) && StringUtils.isNotEmpty(defaultItemPriceUnit.getPromotionPrice())) {
            priceView.setText("￥" + defaultItemPriceUnit.getPromotionPrice());
            TextView originalPriceWordView = (TextView) findViewById(R.id.item_detail_origin_price_word_txt);
            originalPriceWordView.setText("原价:");
            TextView promotionPriceView = (TextView) findViewById(R.id.item_detail_deleteprice_txt);
            promotionPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            promotionPriceView.setText("￥" + defaultItemPriceUnit.getPrice());
            promotionPriceView.setTypeface(Typeface.createFromAsset(BuyingDemoApplication.getInstance().getAssetsss(), AppConfig.NUMBER_FONT_NAME));
        } else {
            priceView.setText("￥" + defaultItemPriceUnit.getPrice());
        }
        priceView.setTypeface(Typeface.createFromAsset(BuyingDemoApplication.getInstance().getAssetsss(), AppConfig.NUMBER_FONT_NAME));
        if (itemBasicInfoDO != null && !itemBasicInfoDO.isInSale()) {
            Button buyButton = (Button) findViewById(R.id.item_detail_buy_btn);
            buyButton.setBackgroundColor(getResources().getColor(R.color.global_single_line));
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
