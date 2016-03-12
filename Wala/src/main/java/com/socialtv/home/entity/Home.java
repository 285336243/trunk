package com.socialtv.home.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-6-22.
 */
public class Home extends Response {
    private HomeRecommand recommand;
    private List<Banner> banners;
    private List<Entries> entries;

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
    public List<Banner> getBanners() {
        return banners;
    }

    public List<Entries> getEntries() {
        return entries;
    }

    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }

    public HomeRecommand getRecommand() {
        return recommand;
    }

    public void setRecommand(HomeRecommand recommand) {
        this.recommand = recommand;
    }
}
