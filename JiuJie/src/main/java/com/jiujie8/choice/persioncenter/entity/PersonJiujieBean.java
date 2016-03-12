package com.jiujie8.choice.persioncenter.entity;

import com.jiujie8.choice.Response;
import com.jiujie8.choice.home.entity.ChoiceMode;

import java.util.List;

/**
 * 个人中心纠结列表
 */
public class PersonJiujieBean extends Response{

    private List<ChoiceMode> rs;
    public List<ChoiceMode> getRs() {
        return rs;
    }

    public void setRs(List<ChoiceMode> rs) {
        this.rs = rs;
    }
}
