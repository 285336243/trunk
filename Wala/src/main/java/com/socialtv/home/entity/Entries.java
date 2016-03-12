package com.socialtv.home.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-6-22.
 */
public class Entries {

    private String name;
    private String tagImg;
    private String type;
    private List<Entry> entry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagImg() {
        return tagImg;
    }

    public void setTagImg(String tagImg) {
        this.tagImg = tagImg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }
}
