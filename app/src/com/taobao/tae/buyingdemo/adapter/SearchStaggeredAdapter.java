package com.taobao.tae.buyingdemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.constant.AppConfig;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.model.ItemInfoDO;
import com.taobao.tae.buyingdemo.view.TopConnerRoundImageView;

import java.util.LinkedList;
import java.util.List;


public class SearchStaggeredAdapter extends DynamicBaseAdapter {

    public SearchStaggeredAdapter(Context context, int viewTypeCount) {
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
        return R.layout.pinterest_content_item;
    }

    /**
     * @param view: 绑定数据的view
     * @return
     */
    @Override
    protected ViewHolder view2Holder(View view) {
        ItemViewHolder itemViewHolder = new ItemViewHolder();
        itemViewHolder.itemPicView = (TopConnerRoundImageView) view.findViewById(R.id.item_pic);
        itemViewHolder.itemTitleView = (TextView) view.findViewById(R.id.item_title);
        itemViewHolder.itemPriceView = (TextView) view.findViewById(R.id.item_price);
        return itemViewHolder;
    }

    /**
     * 将 data 与 view 进行绑定
     *
     * @param viewHolder： 绑定的view的索引
     * @param data：       绑定的数据
     */
    @Override
    protected void bindView(ViewHolder viewHolder, ItemDataObject data) {
        ItemInfoDO itemInfoDO = (ItemInfoDO) data.getData();
        ((ItemViewHolder) viewHolder).itemPriceView.setText("¥".concat(itemInfoDO.getPrice()));
        String title = itemInfoDO.getTitle();
        if (title != null && title.length() > AppConfig.ITEM_TITLE_MAX_LENGTH) {
            title = title.substring(0, AppConfig.ITEM_TITLE_MAX_LENGTH);
            title = title.concat("...");
        }
        ((ItemViewHolder) viewHolder).itemTitleView.setText(title);
        setImageDrawable(itemInfoDO.getPicUrl(), ((ItemViewHolder) viewHolder).itemPicView);
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
    public void addItemTop(List<ItemDataObject> datas) {
        for (int i = datas.size() - 1; i >= 0; i--) {
            mData.addFirst(datas.get(i));
        }
    }
}
