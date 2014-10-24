package com.taobao.tae.buyingdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.model.CategoryDO;
import com.taobao.tae.buyingdemo.model.ItemDataObject;

import java.util.LinkedList;
import java.util.List;


/**
 * 父分类适配器
 */
public class ParentCategoryAdapter extends BaseAdapter {
    /**
     * 数据列表
     */
    protected LinkedList<ItemDataObject> mData;

    /**
     * 选择的分类下标，默认为第一个
     */
    public static int selectCategoryIndex = 0;

    /**
     * 初始化
     *
     * @param context
     */
    public ParentCategoryAdapter(Context context) {
        mData = new LinkedList<ItemDataObject>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryTreeViewHolder categoryTreeViewHolder;
        ItemDataObject itemDataObject = mData.get(position);
        CategoryDO categoryDO = (CategoryDO) itemDataObject.getData();
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.category_item_button, null);
            categoryTreeViewHolder = new CategoryTreeViewHolder();
            categoryTreeViewHolder.categoryNameView = (TextView) convertView.findViewById(R.id.category_name_txt);
            categoryTreeViewHolder.leftIndicatorLineView = convertView.findViewById(R.id.left_indicator_line);
            categoryTreeViewHolder.rightIndicatorLineView = convertView.findViewById(R.id.right_indicator_line);
            categoryTreeViewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.category_parent_btn_ly);
            convertView.setTag(categoryTreeViewHolder);
        }
        categoryTreeViewHolder = (CategoryTreeViewHolder) convertView.getTag();
        categoryTreeViewHolder.categoryNameView.setText(categoryDO.getName());

        if (selectCategoryIndex == position) {
            categoryTreeViewHolder.leftIndicatorLineView.setVisibility(View.VISIBLE);
            categoryTreeViewHolder.rightIndicatorLineView.setVisibility(View.GONE);
            categoryTreeViewHolder.itemLayout.setBackgroundColor(convertView.getResources().getColor(R.color.white));
        }else{
            categoryTreeViewHolder.leftIndicatorLineView.setVisibility(View.GONE);
            categoryTreeViewHolder.rightIndicatorLineView.setVisibility(View.VISIBLE);
            categoryTreeViewHolder.itemLayout.setBackgroundColor(convertView.getResources().getColor(R.color.pinterest_backgroud));
        }
        return convertView;
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


    /**
     * 添加分类
     *
     * @param datas
     */
    public void addCategory(List<ItemDataObject> datas) {
        mData.addAll(datas);
    }


    /**
     * <p>分类中左侧的一级分类数</p>
     * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
     * Date: 14/8/19
     * Time: 下午4:59
     */
    public class CategoryTreeViewHolder extends ViewHolder {
        /*左侧指示器*/
        protected View leftIndicatorLineView;
        /*中间分类名称*/
        protected TextView categoryNameView;
        /*右边指示器*/
        protected View rightIndicatorLineView;
        /*整个item布局*/
        protected RelativeLayout itemLayout;
    }
}
