package com.taobao.tae.buyingdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.taobao.tae.buyingdemo.R;

/**
 * <p>宽度固定，高度自适应的图片，适用于加载本地图片（setBackground）和加载网络图片（setImageSource）</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/20
 * Time: 下午4:38
 */
public class AutoAdjustHeightImageView extends ImageView {

    //加载的网络图片资源
    private Bitmap imageBitMap;
    //网络图片宽度
    private int imageWidth;
    //网络图片高度
    private int imageHeight;
    //是否是商品详情页主图
    private boolean isDetailMainImage = false;
    private int defaultImageResource;
    //屏幕宽度
    private int screenWidth;
    private int screenHeight;

    public AutoAdjustHeightImageView(Context context) {
        super(context);
    }

    public AutoAdjustHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 如果图片背景为null，则获取图片资源
     */
    protected void initImageSize() {
        try {
            Drawable imageDrawable = this.getBackground();
            if (imageDrawable == null && imageBitMap != null) {
                imageWidth = imageBitMap.getWidth();
                imageHeight = imageBitMap.getHeight();
            }
            if (imageDrawable != null && imageDrawable == null && !isDetailMainImage) {
                Bitmap bitmap = ((BitmapDrawable) imageDrawable).getBitmap();
                imageWidth = bitmap.getWidth();
                imageHeight = bitmap.getHeight();
            }
            if (isDetailMainImage && imageWidth > 0) {
                getWidthAndHeight();
                int maxHeight = screenHeight - getResources().getDimensionPixelSize(R.dimen.detail_first_sreen_height);
                int adjustImageHeight = imageHeight * screenWidth / imageWidth;
                if (adjustImageHeight > maxHeight) {
                    imageWidth = screenWidth;
                    imageHeight = maxHeight;
                }
            }
        } catch (Exception e) {
            Log.e("", e.getMessage(), e.fillInStackTrace());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        initImageSize();
        if (imageWidth == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int adjustHeight = width * imageHeight / imageWidth;
            this.setMeasuredDimension(width, adjustHeight);
        }
    }

    /**
     * 重写setImageResource 以便拿到获取从服务端拿到的图片
     *
     * @param bm
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        imageBitMap = bm;
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        defaultImageResource = resId;
    }

    /**
     * 获取屏幕的宽度和高度
     */
    public void getWidthAndHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    public boolean isDetailMainImage() {
        return isDetailMainImage;
    }

    public void setDetailMainImage(boolean isDefault) {
        this.isDetailMainImage = isDefault;
    }
}
