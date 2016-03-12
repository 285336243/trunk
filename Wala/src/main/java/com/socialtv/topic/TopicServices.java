package com.socialtv.topic;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.MultipartRequest;
import com.socialtv.publicentity.Pics;
import com.socialtv.publicentity.Topic;
import com.socialtv.topic.entity.TopicHeader;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-7-1.
 * 所有话题的网络请求接口
 */
public class TopicServices {

    private final static String TOPIC_HEADER_URL = "topic/detail.json?id=%s";
    private final static String TOPIC_POSTS_URL = "topic/list.json?id=%s&page=%s&cnt=%s";
    private final static String TOPIC_COMMENT_URL = "topic/post.json";
    private final static String TOPIC_REPLY_URL = "post/reply.json";
    private final static String TOPIC_LIKE_URI = "topic/favorit.json?id=%s";
    private final static String TOPIC_UNLIKE_URI = "topic/unfavorit.json?id=%s";
    private final static String TOPIC_DELETE_URI = "topic/del.json?id=%s";
    private final static String SUBMIT_ACTIVITY_TOPIC = "activity/topic.json";
    private final static String SUBMIT_STAR_TOPIC = "celebrity/topic.json";
    private final static String REPORT_URI = "system/expose.json";
    private final static String SUBMIT_GROUP_TOPIC = "group/topic.json";

    public Request<TopicHeader> createTopicHeaderRequest(final String topicId) {
        GsonRequest<TopicHeader> request = new GsonRequest<TopicHeader>(Request.Method.GET, String.format(TOPIC_HEADER_URL, topicId));
        request.setClazz(TopicHeader.class);
        return request;
    }

    public Request<Topic> createPostsRequest(final String topicId, final int page, final int count) {
        GsonRequest<Topic> request = new GsonRequest<Topic>(Request.Method.GET, String.format(TOPIC_POSTS_URL, topicId, page, count));
        request.setClazz(Topic.class);
        return request;
    }

    public Request<Response> createCommentRequest(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, TOPIC_COMMENT_URL);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public Request<Response> createReplyRequest(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, TOPIC_REPLY_URL);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public Request<Response> createLikeRequest(final String topicId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(TOPIC_LIKE_URI, topicId));
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createUnLikeRequest(final String topicId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(TOPIC_UNLIKE_URI, topicId));
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createDeleteRequest(final String topicId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, String.format(TOPIC_DELETE_URI, topicId));
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createSubmitActivityTopicRequest(final String id, final String msg, final List<Pics> items) {
        MultipartRequest<Response> request = new MultipartRequest<Response>(SUBMIT_ACTIVITY_TOPIC);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Pics pics = items.get(i);
                if (pics != null) {
                    request.addMultipartFileEntity("img" + i, new File(pics.getImg()));
                }
            }
        }
        request.addMultipartStringEntity("id", id)
                .addMultipartStringEntity("msg", msg);
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createSubmitStarTopicRequest(final String id, final String msg, final List<Pics> items) {
        MultipartRequest<Response> request = new MultipartRequest<Response>(SUBMIT_STAR_TOPIC);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Pics pics = items.get(i);
                if (pics != null) {
                    request.addMultipartFileEntity("img" + i, new File(pics.getImg()));
                }
            }
        }
        request.addMultipartStringEntity("id", id)
                .addMultipartStringEntity("msg", msg);
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createSubmitGroupTopicRequest(final String id, final String msg, final List<Pics> items) {
        MultipartRequest<Response> request = new MultipartRequest<Response>(SUBMIT_GROUP_TOPIC);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Pics pics = items.get(i);
                if (pics != null) {
                    request.addMultipartFileEntity("img" + i, new File(pics.getImg()));
                }
            }
        }
        request.addMultipartStringEntity("id", id)
                .addMultipartStringEntity("msg", msg);
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createReportRequest(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, REPORT_URI);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public Request<com.socialtv.publicentity.Topic> createCommentRequest(final String topicId, final int page, final int count) {
        GsonRequest<com.socialtv.publicentity.Topic> request = new GsonRequest<com.socialtv.publicentity.Topic>(Request.Method.GET, String.format(TOPIC_POSTS_URL, topicId, page, count));
        request.setClazz(com.socialtv.publicentity.Topic.class);
        return request;
    }
}
