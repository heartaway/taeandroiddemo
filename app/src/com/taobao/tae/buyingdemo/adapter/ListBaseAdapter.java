package com.taobao.tae.buyingdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.util.BitmapCache;
import com.taobao.tae.buyingdemo.util.VolleySingleton;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 数据与图片绑定适配器
 */
public abstract class ListBaseAdapter extends BaseAdapter {
    /**
     * 数据列表
     */
    protected LinkedList<ItemDataObject> mData;
    /**
     * 绑定view的资源id
     */
    protected int mResource;
    protected LayoutInflater mInflater;
    protected ArrayList<ViewHolder> holders;

    /**
     * 构造函数
     *
     * @param context：Context实例，可以使用Application Context
     * @param resource：item                     layout id
     */
    public ListBaseAdapter(Context context, int resource, LinkedList<ItemDataObject> data) {
        this(context, resource);
        mData = data;
    }

    /**
     * 构造函数
     *
     * @param context：Context实例，可以使用Application Context
     * @param resource：item                     layout id
     */
    public ListBaseAdapter(Context context, int resource) {
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holders = new ArrayList<ViewHolder>();
    }

    /**
     * 设置数据列表
     *
     * @param data: 列表数据
     */
    protected void setDataList(LinkedList<ItemDataObject> data) {
        mData = data;
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        //TaoLog.Logd("ListBaseAdapter", "mData size:"+mData.size());
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    /*
     * 创建view，并绑定数据
     */
    private View createViewFromResource(int position, View convertView,
                                        ViewGroup parent, int resource) {
        //TaoLog.Logd("ListBaseAdapter", "pos:"+position);
        if (position >= mData.size())
            return convertView;
        View v;
        ViewHolder viewHolder;
        ItemDataObject data = (ItemDataObject) mData.get(position);
        //是否已经有view绑定了该数据
        int size = holders.size();


        if (convertView != null) {
            ViewHolder tmpHolder = (ViewHolder) convertView.getTag();
            //丢失了该viewholder
            if (tmpHolder != null && !holders.contains(tmpHolder)) {
                holders.add(tmpHolder);
            }
            //已经和data绑定
            if (tmpHolder.bindedDo == data) {
                if (!data.isChanged())
                    return convertView;
            } else {
                //防止数据绑定多个view
                if (position != 0) {
                    for (int i = 0; i < size; i++) {
                        ViewHolder holder = holders.get(i);
                        //数据已和已有的view的绑定
                        if (holder.bindedDo == data && convertView != holder.contentView) {
                            //交换view
                            View childView = ((ViewGroup) convertView).getChildAt(0);
                            View childView1 = ((ViewGroup) holder.contentView).getChildAt(0);

                            ((ViewGroup) convertView).removeViewAt(0);
                            ((ViewGroup) holder.contentView).removeViewAt(0);

                            ((ViewGroup) convertView).addView(childView1);
                            ((ViewGroup) holder.contentView).addView(childView);

                            //交换contentview
                            tmpHolder = (ViewHolder) convertView.getTag();
                            tmpHolder.contentView = holder.contentView;
                            holder.contentView = convertView;


                            //交换viewholder
                            tmpHolder.contentView.setTag(tmpHolder);
                            convertView.setTag(holder);

                            convertView.requestLayout();
                            //holder.contentView.requestLayout();
                            convertView.invalidate();
                            if (!data.isChanged())
                                return convertView;

                        }
                    }
                }
            }
        }
        if (convertView == null) {//创建新的content view
            v = mInflater.inflate(resource, null, false);
            FrameLayout frame = new FrameLayout(v.getContext());
            frame.addView(v);
            viewHolder = view2Holder(v);
            v = frame;
            v.setTag(viewHolder);
            viewHolder.contentView = v;
            holders.add(viewHolder);
        } else {
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        if (viewHolder.bindedDo != data || data.isChanged()) {
            viewHolder.bindedDo = data;
            bindView(viewHolder, data);
            //try2BindImg(viewHolder, data);
        }

        return v;
    }

    /**
     * 将view转换成viewholder，减少每次查询view内子view的次数，加快绑定过程。
     * 将要绑定view内的各子view引用通过viewholder长期持有。以减少findViewById调用次数
     * 在inflate一个view后调用
     *
     * @param view: 绑定数据的view
     * @return 解析完成的一份子view索引
     */
    protected abstract ViewHolder view2Holder(View view);

    /**
     * 数据与view绑定
     *
     * @param viewHolder： 绑定的view的索引
     * @param data：       绑定的数据
     */
    protected abstract void bindView(ViewHolder viewHolder, ItemDataObject data);

    /**
     * 将view的background设置为url对应图片,必须在主线程调用
     * 参数：
     *
     * @param url：  图片对应的链接
     * @param view： 设置背景的控件
     * @return true：	图片在内存中，直接绑定成功 false：	图片不在内存中，函数返回时还未绑定，load完成后自动绑定
     */
    private boolean setBackgroundDrawable(String url, View view) {
        //TODO
        return false;
    }

    /**
     * 将ImageView的src设置为url对应图片,必须在主线程调用
     *
     * @param url：  图片对应的链接
     * @param view： 设置src的ImageView
     * @return true：	图片在内存中，直接绑定成功 false：	图片不在内存中，函数返回时还未绑定，load完成后自动绑定
     */
    protected void setImageDrawable(String url, ImageView view) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(view, R.drawable.item_image_empty, R.drawable.item_image_fail);
        imageLoader.get(url, listener);
    }

    /**
     * 销毁adapter，一般在datalogic的destroy时调用
     */
    protected void destroy() {
        int size = holders.size();
        for (int i = 0; i < size; i++) {
            holders.get(i).bindedDo = null;
            holders.get(i).contentView = null;
        }
        holders.clear();
    }

}
