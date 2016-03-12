package com.shengzhish.xyj.news;

import com.android.volley.Request;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.news.entity.NewsPost;

/**
 * Created by wlanjie on 14-6-13.
 */
public class NewsServices extends AbstractService {

    private final static String URL = "news/post.json?id=%s&cusorId=%s&cnt=%s";

    public Request<NewsPost> createNewsRequest(final String id, final String cusorId, final int count) {
        GsonRequest<NewsPost> request = new GsonRequest<NewsPost>(Request.Method.GET, String.format(URL, id, cusorId, count));
        request.setClazz(NewsPost.class);
        return request;
    }
}
