package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

/**
 * <p>商品图文信息</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/26
 * Time: 下午5:07
 */
public class ItemDescriptionDO implements Serializable {

    private String content;

    private int type;

    public enum DescriptionType {
        TXT(1, "文本"),
        IMAGE(0, "图片");

        int type;
        String comment;

        DescriptionType(int type, String comment) {
            this.type = type;
            this.comment = comment;
        }

        public int getType() {
            return type;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
