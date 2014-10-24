package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/19
 * Time: 下午5:00
 */
public class ItemDataObject implements Serializable{
    /**
     * 数据是否被修改
     */
    private boolean isSelected;

    private boolean isChanged;

    // 解析出的协议do字段
    private Object mData;

    private int type;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object obj) {
        mData = obj;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
