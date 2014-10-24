package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

/**
 * <p>邮费</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/27
 * Time: 下午6:11
 */
public class DeliveryDO implements Serializable {

    //邮寄方式
    private String name;
    //费用
    private String price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
