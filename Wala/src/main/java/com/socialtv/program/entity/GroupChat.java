package com.socialtv.program.entity;

import com.socialtv.Response;

import java.util.List;

/**
 * Created by wlanjie on 14-7-8.
 */
public class GroupChat extends Response {
    private List<GroupChatItem> posts;

    public List<GroupChatItem> getPosts() {
        return posts;
    }

    public void setPosts(List<GroupChatItem> posts) {
        this.posts = posts;
    }
}
