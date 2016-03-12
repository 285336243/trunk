package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.SharePage;
import com.jiujie8.choice.publicentity.User;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/12/9.
 */
public class ChoiceItem implements Serializable {
    private static final long serialVersionUID = 836489913108723568L;
    private int id;
    private String choiceType;
    private String title;
    private User user;
    private int favoriteCnt;
    private int postCnt;
    private int voteCnt;
    private SharePage sharePage;
    private String createTime;
    private String leftImg;
    private String rightImg;
    private int voteLeftCnt;
    private int voteRightCnt;
    private String desc;
    private int bgColorRed;
    private int bgColorGreen;
    private int bgColorBlue;
    private String img;
    private int voteYesCnt;
    private int voteNoCnt;
    private boolean voteSupport;
    private int postStatus;
    private int voteStatus;

    public int getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(int postStatus) {
        this.postStatus = postStatus;
    }

    public int getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(int voteStatus) {
        this.voteStatus = voteStatus;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(String choiceType) {
        this.choiceType = choiceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getFavoriteCnt() {
        return favoriteCnt;
    }

    public void setFavoriteCnt(int favoriteCnt) {
        this.favoriteCnt = favoriteCnt;
    }

    public int getPostCnt() {
        return postCnt;
    }

    public void setPostCnt(int postCnt) {
        this.postCnt = postCnt;
    }

    public int getVoteCnt() {
        return voteCnt;
    }

    public void setVoteCnt(int voteCnt) {
        this.voteCnt = voteCnt;
    }

    public String getCreateTime() {
        return createTime;
    }

    public SharePage getSharePage() {
        return sharePage;
    }

    public void setSharePage(SharePage sharePage) {
        this.sharePage = sharePage;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLeftImg() {
        return leftImg;
    }

    public void setLeftImg(String leftImg) {
        this.leftImg = leftImg;
    }

    public String getRightImg() {
        return rightImg;
    }

    public void setRightImg(String rightImg) {
        this.rightImg = rightImg;
    }

    public int getVoteLeftCnt() {
        return voteLeftCnt;
    }

    public void setVoteLeftCnt(int voteLeftCnt) {
        this.voteLeftCnt = voteLeftCnt;
    }

    public int getVoteRightCnt() {
        return voteRightCnt;
    }

    public void setVoteRightCnt(int voteRightCnt) {
        this.voteRightCnt = voteRightCnt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getBgColorRed() {
        return bgColorRed;
    }

    public void setBgColorRed(int bgColorRed) {
        this.bgColorRed = bgColorRed;
    }

    public int getBgColorGreen() {
        return bgColorGreen;
    }

    public void setBgColorGreen(int bgColorGreen) {
        this.bgColorGreen = bgColorGreen;
    }

    public int getBgColorBlue() {
        return bgColorBlue;
    }

    public void setBgColorBlue(int bgColorBlue) {
        this.bgColorBlue = bgColorBlue;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getVoteYesCnt() {
        return voteYesCnt;
    }

    public void setVoteYesCnt(int voteYesCnt) {
        this.voteYesCnt = voteYesCnt;
    }

    public int getVoteNoCnt() {
        return voteNoCnt;
    }

    public void setVoteNoCnt(int voteNoCnt) {
        this.voteNoCnt = voteNoCnt;
    }

    public boolean isVoteSupport() {
        return voteSupport;
    }

    public void setVoteSupport(boolean voteSupport) {
        this.voteSupport = voteSupport;
    }
}
