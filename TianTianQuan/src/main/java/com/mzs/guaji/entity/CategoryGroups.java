package com.mzs.guaji.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-1-17.
 */
public class CategoryGroups {
    @Expose
    private String categoryTitle;
    @Expose
    private String type;
    @Expose
    private JsonElement result;

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getResult() {
        return result;
    }

    public void setResult(JsonElement result) {
        this.result = result;
    }
}
