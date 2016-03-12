package com.shengzhish.xyj.home;

import android.content.Context;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.home.entity.Feed;
import com.shengzhish.xyj.http.GsonRequest;

/**
 * Created by wlanjie on 14-5-30.
 */
public class HomeServices extends AbstractService {

    @Inject
    Context context;

    private final static String FEED = "feed/read.json?p=%s&cnt=%s";

    public Request<Feed> pageFeed(final long page, final long count) {
//        GsonRequest<Feed> request = new GsonRequest();
//        request.setUri(String.format(FEED, page, count));
//        request.setType(new TypeToken<Feed>(){}.getType());
//        return createPageIterator(context, request);
        GsonRequest<Feed> request = new GsonRequest<Feed>(Request.Method.GET, String.format(FEED, page, count));
        request.setClazz(Feed.class);
        return request;
    }
}
