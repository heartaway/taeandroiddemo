package com.taobao.tae.buyingdemo.model;

/**
 * <p>商品库存单元</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/26
 * Time: 下午5:31
 */
public class ItemStockUnit {

    public String skuId;
    //库存量
    public String quantity;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
