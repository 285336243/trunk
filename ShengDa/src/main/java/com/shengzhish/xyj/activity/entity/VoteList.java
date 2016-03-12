package com.shengzhish.xyj.activity.entity;

/**
 *
 *     投票list
 *
 *
 */
public class VoteList {

    private String id;
    private String name;
    private String img;
    private String vote;
    /**
     *     是否投票
     *
     */
    private int isVote;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public int getIsVote() {
        return isVote;
    }

    public void setIsVote(int isVote) {
        this.isVote = isVote;
    }
}
