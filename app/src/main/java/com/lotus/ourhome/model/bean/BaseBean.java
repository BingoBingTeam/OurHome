package com.lotus.ourhome.model.bean;

import java.io.Serializable;

public class BaseBean implements Serializable {
    public static final  String ID = "id";
    public static final  String NAME = "name";
    public static final  String CREATE_TIME = "create_time";

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
