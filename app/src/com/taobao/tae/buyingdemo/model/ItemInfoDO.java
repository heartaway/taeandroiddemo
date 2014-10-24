package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

public class ItemInfoDO implements Serializable {

    /*标题*/
    private String title;

    /*后台商品库中商品ID*/
    private Long itemId;

    /*淘宝商品ID*/
    private String tbItemId;

    /* 商品价格 */
    private String price;

    /*商品促销价*/
    private String promotionPrice;

    private String picUrl;

    /*喜欢数*/
    private String favorCount;

    /*商品打开方式*/
    private Integer type;

    /*商品打开方式名称*/
    private String name;

    /*商品所属二级分类ID*/
    private Integer categoryId;

    /*以H5打开时的URL地址*/
    private String H5Url;

    /*以搜索方式打开时的关键字*/
    private String keyword;

    /*排序方式*/
    private Integer sort;

    /*商品所在地*/
    private String location;

    /*月销量*/
    private int monthlySales;

    /*商品是否处于销售中*/
    private boolean inSale;

    /*是否有SKU属性*/
    private boolean hasSKU;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTbItemId() {
        return tbItemId;
    }

    public void setTbItemId(String tbItemId) {
        this.tbItemId = tbItemId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(String promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(String favorCount) {
        this.favorCount = favorCount;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getH5Url() {
        return H5Url;
    }

    public void setH5Url(String h5Url) {
        H5Url = h5Url;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }


    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(int monthlySales) {
        this.monthlySales = monthlySales;
    }

    public boolean isInSale() {
        return inSale;
    }

    public void setInSale(boolean inSale) {
        this.inSale = inSale;
    }

    public boolean isHasSKU() {
        return hasSKU;
    }

    public void setHasSKU(boolean hasSKU) {
        this.hasSKU = hasSKU;
    }
}
