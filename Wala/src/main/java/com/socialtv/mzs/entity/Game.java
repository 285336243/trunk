package com.socialtv.mzs.entity;

import java.util.List;

/**
 * Created by wlanjie on 14-9-2.
 */
public class Game {
    private long id;
    private String name;
    private String time;
    private int isStart;
    private String verse;
    private String notice;
    private String rule;
    private List<GameTools> tools;
    private String img;
    private List<GameVotes> votes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public String getVerse() {
        return verse;
    }

    public void setVerse(String verse) {
        this.verse = verse;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<GameTools> getTools() {
        return tools;
    }

    public void setTools(List<GameTools> tools) {
        this.tools = tools;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<GameVotes> getVotes() {
        return votes;
    }

    public void setVotes(List<GameVotes> votes) {
        this.votes = votes;
    }
}
