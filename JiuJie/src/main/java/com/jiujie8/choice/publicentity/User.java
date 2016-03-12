package com.jiujie8.choice.publicentity;

import java.io.Serializable;

/**
 * Created by wlanjie on 14/11/25.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 3255191204552324699L;
    private String id;
    private String nickname;
    private String avatar;
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
