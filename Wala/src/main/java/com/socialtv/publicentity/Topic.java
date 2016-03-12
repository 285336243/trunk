package com.socialtv.publicentity;

import com.socialtv.Response;
import com.socialtv.ShareTemplete;
import com.socialtv.message.entity.Post;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wlanjie on 14-6-25.
 */
public class Topic extends Response implements Serializable {
    private static final long serialVersionUID = -8283170927312812312L;
    private String id;
    private String title;
    private String message;
    private List<Pics> pics;
    private String createTime;
    private User user;
    private long postsCnt;
    private long favoritsCnt;
    private int isFavorit;
    private int isTop;
    private String prize;
    private List<Post> posts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public int getIsFavorit() {
        return isFavorit;
    }

    public void setIsFavorit(int isFavorit) {
        this.isFavorit = isFavorit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }

    public int getIsTop() {
        return isTop;
    }

    public void setIsTop(int isTop) {
        this.isTop = isTop;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
