package com.taobao.tae.buyingdemo.model;

/**
 * <p>商品价格单元</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/26
 * Time: 下午5:31
 */
public class ItemPriceUnit {

    public String skuId;
    public String price;
    public String priceName;
    public String promotionPrice;
    public String promotionPriceName;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(String promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public String getPromotionPriceName() {
        return promotionPriceName;
    }

    public void setPromotionPriceName(String promotionPriceName) {
        this.promotionPriceName = promotionPriceName;
    }
}
