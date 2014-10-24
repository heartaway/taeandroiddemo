package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

public class ShopDO implements Serializable {

    private String sellerCredit;

    private String sellerNick;

    private String shopTitle;

    private String shopType;

    private Long userId;

    public String getSellerCredit() {
        return sellerCredit;
    }

    public void setSellerCredit(String sellerCredit) {
        this.sellerCredit = sellerCredit;
    }

    public String getSellerNick() {
        return sellerNick;
    }

    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
