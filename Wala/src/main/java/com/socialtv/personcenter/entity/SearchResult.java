package com.socialtv.personcenter.entity;

import java.util.List;

/**
 * Created by wlanjie on 14/10/27.
 */
public class SearchResult {
    private List<Mobile> mobiles;
    private List<String> phones;

    public List<Mobile> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<Mobile> mobiles) {
        this.mobiles = mobiles;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
