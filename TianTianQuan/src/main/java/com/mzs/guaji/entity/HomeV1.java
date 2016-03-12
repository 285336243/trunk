package com.mzs.guaji.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by wlanjie on 14-1-17.
 */
public class HomeV1 {

    @Expose
    private long responseCode;
    @Expose
    private String responseMessage;
    @Expose
    private Score givenScore;
    @Expose
    private ShareTemplete shareTemplete;
    @Expose
    private List<Banners> banners;
    @Expose
    private List<CategoryGroups> categoryGroups;
    @Expose
    private List<HomeRecommend> recommend;
    public long getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(long responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Score getGivenScore() {
        return givenScore;
    }

    public void setGivenScore(Score givenScore) {
        this.givenScore = givenScore;
    }

    public ShareTemplete getShareTemplete() {
        return shareTemplete;
    }

    public List<Banners> getBanners() {
        return banners;
    }

    public void setBanners(List<Banners> banners) {
        this.banners = banners;
    }

    public List<CategoryGroups> getCategoryGroups() {
        return categoryGroups;
    }

    public void setCategoryGroups(List<CategoryGroups> categoryGroups) {
        this.categoryGroups = categoryGroups;
    }

    public void setShareTemplete(ShareTemplete shareTemplete) {
        this.shareTemplete = shareTemplete;
    }

    public List<HomeRecommend> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<HomeRecommend> recommend) {
        this.recommend = recommend;
    }
}
