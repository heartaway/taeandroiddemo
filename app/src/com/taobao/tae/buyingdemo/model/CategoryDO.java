package com.taobao.tae.buyingdemo.model;

import java.io.Serializable;

public class CategoryDO implements Serializable {

    private int id;
    /*标题*/
    private String name;

    /*父分类ID*/
    private int father;

    /*分类图标*/
    private String pic;

    /*分类展示序号*/
    private int sequence;

    /*创建时间*/
    private String gmtCreated;

    /*修改时间*/
    private String gmtModified;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(String gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }
}
