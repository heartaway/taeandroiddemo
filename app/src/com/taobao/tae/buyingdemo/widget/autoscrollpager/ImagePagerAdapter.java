package com.taobao.tae.buyingdemo.widget.autoscrollpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.model.ItemDataObject;
import com.taobao.tae.buyingdemo.model.ItemInfoDO;
import com.taobao.tae.buyingdemo.util.BitmapCache;
import com.taobao.tae.buyingdemo.util.ListUtils;
import com.taobao.tae.buyingdemo.util.VolleySingleton;

import java.util.List;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/14
 * Time: 上午11:18
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private RequestQueue requestQueue;
    private List<ItemDataObject> itemDataObjects;

    private int size;
    private boolean isInfiniteLoop;

    public ImagePagerAdapter(Context context, List<ItemDataObject> itemDataObjects) {
        this.context = context;
        this.itemDataObjects = itemDataObjects;
        this.size = ListUtils.getSize(itemDataObjects);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        return isInfiniteLoop ? Integer.MAX_VALUE : ListUtils.getSize(itemDataObjects);
    }

    /**
     * 处理 图片轮播中的点击事件
     *
     * @param view
     */
    @Override
    public void onlickPager(View view) {
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ItemDataObject itemDataObject = itemDataObjects.get(getPosition(position));
        ItemInfoDO itemInfoDO = (ItemInfoDO) itemDataObject.getData();
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.imageView, R.drawable.item_image_empty, R.drawable.item_image_fail);
        imageLoader.get(itemInfoDO.getPicUrl(), listener);
        return view;
    }

    private static class ViewHolder {

        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}

