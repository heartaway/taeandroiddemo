package com.taobao.tae.buyingdemo.model;

import java.util.Map;

/**
 * <p>单个商品SKU的属性单元</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/26
 * Time: 下午6:21
 */
public class SkuPropertyUnit {

    public String propId;

    public String propName;

    //此SKU属性是否关联了商品图片的变更
    public boolean isRelationImage;

    public Map<String,SkuPropertyValueUnit> values;

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public boolean isRelationImage() {
        return isRelationImage;
    }

    public void setRelationImage(boolean isRelationImage) {
        this.isRelationImage = isRelationImage;
    }

    public Map<String, SkuPropertyValueUnit> getValues() {
        return values;
    }

    public void setValues(Map<String, SkuPropertyValueUnit> values) {
        this.values = values;
    }
}
