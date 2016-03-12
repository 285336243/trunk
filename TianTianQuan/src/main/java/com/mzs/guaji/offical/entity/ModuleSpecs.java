package com.mzs.guaji.offical.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-1-8.
 */
public class ModuleSpecs {

    @Expose
    private String name;
    @Expose
    private String icon;
    @Expose
    private String title;
    @Expose
    private String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
