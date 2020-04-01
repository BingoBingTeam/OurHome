package com.lotus.ourhome.model.bean;

import java.io.Serializable;

public class BaseBean implements Serializable {
    public static final  String ID = "id";//id
    public static final  String NAME = "name";//名称
    public static final  String CREATE_TIME = "create_time";//创建时间

    private String id;
    private String name;
    private long createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
