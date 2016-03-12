package com.jiujie8.choice.home.entity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/12/4.
 */
public class Vote implements Serializable {
    private static final long serialVersionUID = 217602417043122106L;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
