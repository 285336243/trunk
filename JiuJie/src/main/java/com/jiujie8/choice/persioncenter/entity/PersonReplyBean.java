package com.jiujie8.choice.persioncenter.entity;

import com.jiujie8.choice.Response;
import com.jiujie8.choice.home.entity.ChoiceMode;

import java.util.List;

/**
 * Created by 51wanh on 2015/1/23.
 */
public class PersonReplyBean extends Response {
    private List<ChoiceMode> rs;

    public List<ChoiceMode> getRs() {
        return rs;
    }

    public void setRs(List<ChoiceMode> rs) {
        this.rs = rs;
    }

}
