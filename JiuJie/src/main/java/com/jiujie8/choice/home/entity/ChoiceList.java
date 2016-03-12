package com.jiujie8.choice.home.entity;

import com.jiujie8.choice.Response;

import java.util.List;

/**
 * Created by wlanjie on 14/12/4.
 */
public class ChoiceList extends Response {
    private List<ChoiceMode> rs;

    public List<ChoiceMode> getRs() {
        return rs;
    }

    public void setRs(List<ChoiceMode> rs) {
        this.rs = rs;
    }
}
