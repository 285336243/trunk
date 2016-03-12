package com.socialtv.topic.entity;

import com.socialtv.publicentity.User;
import com.socialtv.home.entity.Refer;
import com.socialtv.publicentity.Pics;

import java.util.List;

/**
 * Created by wlanjie on 14-7-1.
 */
public class TopicHeaderItem {
    private Refer refer;
    private List<Pics> pics;
    private User user;
    private String id;
    private String title;
    private String message;
    private String createTime;
    private long postsCnt;
    private long favoritsCnt;
    private long isFavorit;

    public Refer getRefer() {
        return refer;
    }

    public void setRefer(Refer refer) {
        this.refer = refer;
    }

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getPostsCnt() {
        return postsCnt;
    }

    public void setPostsCnt(long postsCnt) {
        this.postsCnt = postsCnt;
    }

    public long getFavoritsCnt() {
        return favoritsCnt;
    }

    public void setFavoritsCnt(long favoritsCnt) {
        this.favoritsCnt = favoritsCnt;
    }

    public long getIsFavorit() {
        return isFavorit;
    }

    public void setIsFavorit(long isFavorit) {
        this.isFavorit = isFavorit;
    }
}
