package com.taobao.tae.buyingdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.model.ItemDescriptionDO;
import com.taobao.tae.buyingdemo.util.BitmapCache;
import com.taobao.tae.buyingdemo.util.VolleySingleton;
import com.taobao.tae.buyingdemo.view.AutoAdjustHeightImageView;

import java.util.ArrayList;

/**
 * <p>商品详情中 图文信息碎块，采用懒加载</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/21
 * Time: 上午10:29
 */
public class DetailPicWordFragment extends Fragment {

    public static String ITEM_DESCRIPTIONS_TAG = "itemDescriptions";
    private Context context;
    private ArrayList<ItemDescriptionDO> itemDescriptionDOs;
    private LinearLayout dynamicFillContentLinearView;
    private ViewGroup.LayoutParams layoutParams;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 2;


    public static DetailPicWordFragment newInstance(Bundle bundle) {
        DetailPicWordFragment newFragment = new DetailPicWordFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public DetailPicWordFragment() {
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new BitmapCache());
    }

    /**
     * 渲染图文信息
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.itemDescriptionDOs = (ArrayList) bundle.getSerializable(ITEM_DESCRIPTIONS_TAG);
        this.context = getActivity().getApplicationContext();
        View contextView = inflater.inflate(R.layout.detail_picword_fragment, container, false);
        dynamicFillContentLinearView = (LinearLayout) contextView.findViewById(R.id.item_detail_dynamic_fill_content);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadMore();
        return contextView;
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        if ((currentPage + 1) * PAGE_SIZE <= itemDescriptionDOs.size()) {
            for (int i = 0; i < PAGE_SIZE; i++) {
                addView(itemDescriptionDOs.get(currentPage * PAGE_SIZE + i));
            }
        } else {
            for (int i = 0; i < itemDescriptionDOs.size() - currentPage * PAGE_SIZE; i++) {
                addView(itemDescriptionDOs.get(currentPage * PAGE_SIZE + i));
            }
        }
        currentPage++;
    }

    /**
     * 添加 View 到LinearLayout中
     * @param itemDescriptionDO
     */
    private void addView(ItemDescriptionDO itemDescriptionDO){
        if (ItemDescriptionDO.DescriptionType.IMAGE.getType() == itemDescriptionDO.getType()) {
            final AutoAdjustHeightImageView imageView = new AutoAdjustHeightImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(layoutParams);
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.item_image_empty, R.drawable.item_image_fail);
            imageLoader.get(itemDescriptionDO.getContent(), listener);
            dynamicFillContentLinearView.addView(imageView);
        }
        if (ItemDescriptionDO.DescriptionType.TXT.getType() == itemDescriptionDO.getType()) {
            TextView textView = new TextView(context);
            textView.setText(itemDescriptionDO.getContent());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.detail_more_info_text_word_size));
            textView.setTextColor(getResources().getColor(R.color.global_font));
            int padding = getResources().getDimensionPixelSize(R.dimen.detail_more_info_text_word_padding);
            textView.setPadding(padding,padding,padding,padding);
            dynamicFillContentLinearView.addView(textView);
        }
    }

}
