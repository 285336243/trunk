package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 13-12-16.
 */
public class EntryForm {

    @Expose
    private long id;
    @Expose
    private String name;
    @Expose
    private String coverImg;
    @Expose
    private String about;
    @Expose
    private String templateName;
    @Expose
    private String clause;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }
}
