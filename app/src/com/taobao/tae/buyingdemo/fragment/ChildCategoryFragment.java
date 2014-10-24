package com.taobao.tae.buyingdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.activity.SearchResultSortActivity;
import com.taobao.tae.buyingdemo.constant.ApiConfig;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.model.*;
import com.taobao.tae.buyingdemo.util.BitmapCache;
import com.taobao.tae.buyingdemo.util.VolleySingleton;

import java.util.ArrayList;

/**
 * <p>子分类Fragment</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/21
 * Time: 上午10:29
 */
public class ChildCategoryFragment extends Fragment {

    public String TAG = ChildCategoryFragment.class.getName();
    private Context context;
    private RequestQueue requestQueue;
    private ArrayList<ItemDataObject> itemDataObjectList;
    public static  String CHILD_CATEGORIES_TAG = "childCategories";
    private View childViewItem;
    //默认每行展示 3 个 分类
    private int column = 3;

    public static ChildCategoryFragment newInstance(Bundle bundle) {
        ChildCategoryFragment newFragment = new ChildCategoryFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public ChildCategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity().getApplicationContext();
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Bundle bundle = getArguments();
        this.itemDataObjectList = (ArrayList)bundle.getSerializable(CHILD_CATEGORIES_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }


    /**
     * 初始化子分类列表
     */
    public View initView() {
        LinearLayout verticalLinearLayout = new LinearLayout(context);
        verticalLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        verticalLinearLayout.setLayoutParams(layoutParams);

        LinearLayout horizontalLinearLayout = new LinearLayout(context);
        horizontalLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams chileItemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.category_child_item_margin);
        chileItemLayoutParams.setMargins(itemMargin, itemMargin, 0, 0);

        int widthSoFar = 0;
        int maxWidth = getChildCategoryLayoutMaxWidth();
        for (ItemDataObject itemDataObject : itemDataObjectList) {
            final CategoryDO childCategoryDO = (CategoryDO) itemDataObject.getData();
            LinearLayout chileItemView = new LinearLayout(context);
            chileItemView.setOrientation(LinearLayout.VERTICAL);
            chileItemView.setLayoutParams(chileItemLayoutParams);
            int itemWidth = getChildCategoryItemWidth();
            int imageHeight = getChildCategoryItemWidth();
            int textHeight =  23 * itemWidth / 70;
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, imageHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.item_image_empty, R.drawable.item_image_fail);
            imageLoader.get(childCategoryDO.getPic(), listener);
            TextView categoryNameView = new TextView(context);
            categoryNameView.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, textHeight));
            categoryNameView.setGravity(Gravity.CENTER);
            categoryNameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.category_child_item_text_size));
            categoryNameView.setTextColor(getResources().getColor(R.color.category_child_item_name));
            chileItemView.addView(imageView);
            chileItemView.addView(categoryNameView);
            categoryNameView.setText(childCategoryDO.getName());
            chileItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppConfig.ACTIVITY_NAME_KEY, R.string.activity_name_of_category);
                    Intent intent = new Intent(getActivity(), SearchResultSortActivity.class);
                    bundle.putString(ApiConfig.SEARCH_TITLE, childCategoryDO.getName());
                    bundle.putInt(ApiConfig.CATEGORY_ID, childCategoryDO.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            chileItemView.measure(0, 0);
            widthSoFar += chileItemView.getMeasuredWidth();
            if (widthSoFar >= maxWidth) {
                verticalLinearLayout.addView(horizontalLinearLayout);
                horizontalLinearLayout = new LinearLayout(context);
                horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLinearLayout.addView(chileItemView);
                widthSoFar = chileItemView.getMeasuredWidth();
            } else {
                horizontalLinearLayout.addView(chileItemView);
            }
        }
        verticalLinearLayout.addView(horizontalLinearLayout);
        return verticalLinearLayout;
    }

    /**
     * 获取右侧子分类View的最大宽度
     *
     * @return
     */
    public int getChildCategoryLayoutMaxWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - getResources().getDimensionPixelSize(R.dimen.category_parent_item_width);
        return maxWidth;
    }

    /**
     * 获取每个子分类的宽度
     * @return
     */
    public int getChildCategoryItemWidth() {
        int maxWidth = getChildCategoryLayoutMaxWidth();
        int nItemWidth = maxWidth - (column + 1) * getResources().getDimensionPixelSize(R.dimen.category_child_item_margin);
        int itemWidth = nItemWidth / column;
        return itemWidth;
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
