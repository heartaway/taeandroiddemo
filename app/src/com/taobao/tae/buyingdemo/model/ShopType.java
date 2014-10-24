package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/26
 * Time: 下午4:07
 */
public enum ShopType implements Serializable {

    B("B", "天猫店铺"),
    C("C", "集市店铺");

    ShopType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String type;
    public String description;

    public String getType() {
        return type;
    }

}
