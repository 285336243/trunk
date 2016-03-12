package com.socialtv.personcenter.entity;

import com.socialtv.Response;
import com.socialtv.publicentity.Pics;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wlanjie on 14-7-4.
 */
public class PhotoAlbum extends Response implements Serializable {
    private static final long serialVersionUID = -1947245992715999479L;
    private List<Pics> pics;

    public List<Pics> getPics() {
        return pics;
    }

    public void setPics(List<Pics> pics) {
        this.pics = pics;
    }
}
