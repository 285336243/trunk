package com.jiujie8.choice.persioncenter.entity;

import com.jiujie8.choice.publicentity.User;

/**
 *
 */
public class UserDetailsModel {
    private User user;
    /**
     * 发布的纠结数量
     */
    private long choices;
    /**
     * 收藏的纠结数量
     */
    private long favorites;
    /**
     * 评论的纠结数量
     */
    private long posts;
    /**
     * 纠结列表是否有更新
     */
    private boolean choiceStatus;
    /**
     * 回答列表是否有更新
     */
    private boolean postStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getChoices() {
        return choices;
    }

    public void setChoices(long choices) {
        this.choices = choices;
    }

    public long getFavorites() {
        return favorites;
    }

    public void setFavorites(long favorites) {
        this.favorites = favorites;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public boolean isChoiceStatus() {
        return choiceStatus;
    }

    public void setChoiceStatus(boolean choiceStatus) {
        this.choiceStatus = choiceStatus;
    }

    public boolean isPostStatus() {
        return postStatus;
    }

    public void setPostStatus(boolean postStatus) {
        this.postStatus = postStatus;
    }
}
