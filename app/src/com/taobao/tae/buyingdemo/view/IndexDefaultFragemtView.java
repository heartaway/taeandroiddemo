package com.taobao.tae.buyingdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.taobao.tae.buyingdemo.widget.pinterest.MultiColumnPullToRefreshListView;

/**
 * <p>自定义首页布局View，包括顶部图片轮播和中部瀑布流商品</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/15
 * Time: 下午3:09
 */
public class IndexDefaultFragemtView extends MultiColumnPullToRefreshListView {

    private float touchX = 0f, downX = 0f;

    private float touchY = 0f, downY = 0f;

    /*双列表中增加图片轮播组件*/
    private IndexAutoScrollView indexAutoScrollView;

    public IndexDefaultFragemtView(Context context) {
        super(context);
        initAutoScrollView(context);
    }

    public IndexDefaultFragemtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoScrollView(context);
    }

    public IndexDefaultFragemtView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoScrollView(context);
    }

    public void initAutoScrollView(Context context) {
        indexAutoScrollView = new IndexAutoScrollView(context);
        addHeaderView(indexAutoScrollView);
    }

    public LinearLayout getBannerView(){
        return indexAutoScrollView.getAutoScrollView();
    }


    /**
     * 重载 整个页面的触屏分发事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        touchX = ev.getX();
        touchY = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = touchX;
            downY = touchY;
        }
        float diffX = Math.abs(touchX - downX);
        float diffY = Math.abs(touchY - downY);
        if (diffY > diffX) {
            onTouchEvent(ev);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

}
