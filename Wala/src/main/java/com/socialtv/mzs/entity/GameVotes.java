package com.socialtv.mzs.entity;

/**
 * Created by wlanjie on 14-9-9.
 */
public class GameVotes {
    private String vid;
    private String name;
    private String img;
    private String voteAction;
    private String voteResult;
    private String resultMsg;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
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

    public String getVoteAction() {
        return voteAction;
    }

    public void setVoteAction(String voteAction) {
        this.voteAction = voteAction;
    }

    public String getVoteResult() {
        return voteResult;
    }

    public void setVoteResult(String voteResult) {
        this.voteResult = voteResult;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
