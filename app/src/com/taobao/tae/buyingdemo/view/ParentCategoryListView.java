package com.taobao.tae.buyingdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * <p>分类菜单中左侧的可下滑菜单列表</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/22
 * Time: 下午5:51
 */
public class ParentCategoryListView extends ListView implements AbsListView.OnScrollListener {

    public ParentCategoryListView(Context context) {
        super(context);
    }

    public ParentCategoryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentCategoryListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

    /**
     * 根据 position 获取item的view
     *
     * @param pos
     * @return
     */
    public View getViewByPosition(int pos) {
        final int firstListItemPosition = this.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + this.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return this.getAdapter().getView(pos, null, this);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return this.getChildAt(childIndex);
        }
    }
}
