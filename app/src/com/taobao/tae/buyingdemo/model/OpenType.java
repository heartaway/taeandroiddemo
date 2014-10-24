package com.taobao.tae.buyingdemo.model;

/**
 * <p>商品打开方式：用户点击一个商品图片，此类型决定跳转到什么页面</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/15
 * Time: 下午1:02
 */
public enum OpenType {

    ITEM(2, "item", "商品详情页打开"),
    SEARCH(0, "search", "搜索结果页打开"),
    H5(1, "h5", "WebView打开");

    Integer type;
    String name;
    String description;

    OpenType(Integer type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
