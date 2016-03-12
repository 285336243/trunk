package com.socialtv.program.entity;

import com.socialtv.home.entity.Banner;

import java.util.List;

/**
 * Created by wlanjie on 14-7-8.
 */
public class Group {
    private String id;
    private String name;
    private String tag;
    private long topicsCnt;
    private long followCnt;
    private int isFollow;
    private String img;
    private List<Banner> banners;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getTopicsCnt() {
        return topicsCnt;
    }

    public void setTopicsCnt(long topicsCnt) {
        this.topicsCnt = topicsCnt;
    }

    public long getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(long followCnt) {
        this.followCnt = followCnt;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
