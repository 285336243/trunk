package com.socialtv.activity.entitiy;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-21.
 */
public class ScreenShot extends Response {

    private String coverImg;
    private List<String> pics;

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }
}
