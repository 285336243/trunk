package com.socialtv.star;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.GsonRequest;
import com.socialtv.star.entity.Star;
import com.socialtv.star.entity.StarHeader;

/**
 * Created by wlanjie on 14-6-27.
 * 明星中所有网络请求接口
 */
public class StarServices {

    private final static String STAR_HEADER = "celebrity/detail.json?id=%s";
    private final static String STAR_DYNAMIC = "celebrity/celebrity_topic.json?id=%s&page=%s&cnt=%s";
    private final static String STAR_TOPICS = "celebrity/topic.json?id=%s&page=%s&cnt=%s";
    private final static String STAR_FOLLOW = "celebrity/follow.json?id=%s";
    private final static String STAR_UN_FOLLOW = "celebrity/unfollow.json?id=%s";

    public Request<StarHeader> createStarHeaderRequest(final String starId) {
        GsonRequest<StarHeader> request = new GsonRequest<StarHeader>(Request.Method.GET, String.format(STAR_HEADER, starId));
        request.setClazz(StarHeader.class);
        return request;
    }

    public Request<Star> createStarDynamicRequest(final String starId, final long page, final long count) {
        GsonRequest<Star> request = new GsonRequest<Star>(Request.Method.GET, String.format(STAR_DYNAMIC, starId, page, count));
        request.setClazz(Star.class);
        return request;
    }

    public Request<Star> createStarTopicsRequest(final String starId, final long page, final long count) {
        GsonRequest<Star> request = new GsonRequest<Star>(Request.Method.GET, String.format(STAR_TOPICS, starId, page, count));
        request.setClazz(Star.class);
        return request;
    }

    public Request<Response> createStarFollowRequest(final String id) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(STAR_FOLLOW, id));
        request.setClazz(Response.class);
        return request;
    }

    public Request<Response> createStarUnFollowRequest(final String id) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(STAR_UN_FOLLOW, id));
        request.setClazz(Response.class);
        return request;
    }
}
