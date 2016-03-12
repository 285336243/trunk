package com.socialtv.shop.entity;

import com.socialtv.Response;
import com.socialtv.home.entity.Banner;

import java.util.List;

/**
 *
 *  广告
 *
 */
public class AdVert extends Response{
    private List<Banner> banners;

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

}
