package com.taobao.tae.buyingdemo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.app.BuyingDemoApplication;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.model.ItemInfoDO;
import com.taobao.tae.buyingdemo.util.StringUtils;
import com.taobao.tae.buyingdemo.view.AllConnerRoundImageView;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.model.ItemDataObjectType;
import com.taobao.tae.buyingdemo.view.TopConnerRoundImageView;

import java.text.SimpleDateFormat;
import java.util.*;


public class IndexItemListAdapter extends DynamicBaseAdapter {

    public IndexItemListAdapter(Context context, int viewTypeCount) {
        super(context, viewTypeCount);
        mData = new LinkedList<ItemDataObject>();
    }

    /**
     * 通过类型判断数据使用的布局资源
     *
     * @param data: item数据
     * @return
     */
    @Override
    protected int mapData2Id(ItemDataObject data) {
        if (data != null && data.getType() == ItemDataObjectType.ITEM) {
            return R.layout.pinterest_content_item;
        }
        if (data != null && data.getType() == ItemDataObjectType.UPDATE_TIME) {
            return R.layout.index_update_time_view;
        }
        if (data != null && data.getType() == ItemDataObjectType.H5_OR_SEARCH) {
            return R.layout.pinterest_content_image;
        }
        return 0;
    }

    /**
     * @param view: 绑定数据的view
     * @return
     */
    @Override
    protected ViewHolder view2Holder(View view) {
        if (view.getId() ==  R.layout.pinterest_content_item) {
            ItemViewHolder itemViewHolder = new ItemViewHolder();
            itemViewHolder.itemPicView = (TopConnerRoundImageView) view.findViewById(R.id.item_pic);
            itemViewHolder.itemTitleView = (TextView) view.findViewById(R.id.item_title);
            itemViewHolder.itemPriceView = (TextView) view.findViewById(R.id.item_price);
            return itemViewHolder;
        }
        if (view.getId() == R.layout.index_update_time_view) {
            TimeViewHolder timeViewHolder = new TimeViewHolder();
            timeViewHolder.timeTextView = (TextView) view.findViewById(R.id.homepage_update_time_time);
            timeViewHolder.dateTextView = (TextView) view.findViewById(R.id.homepage_update_time_date);
            timeViewHolder.weekTextView = (TextView) view.findViewById(R.id.homepage_update_time_week);
            return timeViewHolder;
        }
        if (view.getId() == R.layout.pinterest_content_image) {
            ImageViewHolder imageViewHolder = new ImageViewHolder();
            imageViewHolder.itemPicView = (AllConnerRoundImageView) view.findViewById(R.id.item_pic);
            return imageViewHolder;
        }
        return null;
    }

    /**
     * 将 data 与 view 进行绑定
     *
     * @param viewHolder： 绑定的view的索引
     * @param data：       绑定的数据
     */
    @Override
    protected void bindView(ViewHolder viewHolder, ItemDataObject data) {
        if (data != null && data.getType() == ItemDataObjectType.ITEM) {
            ItemInfoDO itemInfoDO = (ItemInfoDO) data.getData();
            if(itemInfoDO.getPrice() != null){
                ((ItemViewHolder) viewHolder).itemPriceView.setText("¥".concat(itemInfoDO.getPrice()));
            }
            ((ItemViewHolder) viewHolder).itemPriceView.setTypeface(Typeface.createFromAsset(BuyingDemoApplication.getInstance().getAssetsss(), AppConfig.NUMBER_FONT_NAME));
            String title = itemInfoDO.getName();
            if(StringUtils.isEmpty(title) && StringUtils.isNotEmpty(itemInfoDO.getTitle())){
                title = itemInfoDO.getTitle();
            }
            if (title != null && title.length() > AppConfig.ITEM_TITLE_MAX_LENGTH) {
                title = title.substring(0, AppConfig.ITEM_TITLE_MAX_LENGTH);
                title = title.concat("...");
            }
            ((ItemViewHolder) viewHolder).itemTitleView.setText(title);
            setImageDrawable(itemInfoDO.getPicUrl(), ((ItemViewHolder) viewHolder).itemPicView);
        }

        if (data != null && data.getType() == ItemDataObjectType.UPDATE_TIME) {
            Date dateTime = (Date) data.getData();
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd");
            SimpleDateFormat weekformat = new SimpleDateFormat("EEEE");
            ((TimeViewHolder) viewHolder).timeTextView.setText(timeformat.format(dateTime));
            ((TimeViewHolder) viewHolder).dateTextView.setText(dateformat.format(dateTime));
            ((TimeViewHolder) viewHolder).weekTextView.setText("周".concat(weekformat.format(dateTime).substring(2, 3)));
        }

        if (data != null && data.getType() == ItemDataObjectType.H5_OR_SEARCH) {
            ItemInfoDO itemInfoDO = (ItemInfoDO) data.getData();
            setImageDrawable(itemInfoDO.getPicUrl(), ((ImageViewHolder) viewHolder).itemPicView);
        }

    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public void addItemLast(List<ItemDataObject> datas) {
        mData.addAll(datas);
    }

    /**
     * 倒序添加，最后推送的商品展示在最上面
     *
     * @param datas
     */
    public void addItemTop(List<ItemDataObject> datas, Date time) {
        for (int i = datas.size() - 1; i >= 0; i--) {
            mData.addFirst(datas.get(i));
        }
        if (datas != null && datas.size() > 0 && time != null) {
            ItemDataObject itemDataObject = new ItemDataObject();
            itemDataObject.setData(time);
            itemDataObject.setType(ItemDataObjectType.UPDATE_TIME);
            mData.addFirst(itemDataObject);
        }

    }
}
