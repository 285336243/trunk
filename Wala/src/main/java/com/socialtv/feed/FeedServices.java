package com.socialtv.feed;

import com.android.volley.Request;
import com.socialtv.feed.entity.Feed;
import com.socialtv.feed.entity.Follow;
import com.socialtv.feed.entity.OthersFeedHeader;
import com.socialtv.feed.entity.PersionFeed;
import com.socialtv.http.GsonRequest;

/**
 * Created by wlanjie on 14-7-3.
 * 所有动态中的数据接口
 */
public class FeedServices {
    private final static String FEED_LIST = "feed/attention.json?page=%s&cnt=%s";
    private final static String OTHERS_FEED_LIST = "feed/user.json?userid=%s&page=%s&cnt=%s";
    private final static String OTHERS_FEED_HEADER = "user/read.json?userid=%s";
    private final static String OTHERS_FEED_FOLLOW = "user/follow.json?userid=%s";
    private final static String OTHERS_UNFEED_FOLLOW = "user/unfollow.json?userid=%s";
    private final static String LASTEST = "topic/lastest.json?cnt=%s&cusorId=%s";
    private final static String HOT = "topic/hot.json?cnt=%s&cusorId=%s";
    private final static String PERSION_FEED = "user/topic.json?userid=%s&page=%s&cnt=%s";

    public final Request<Feed> createLastestRequest(final int count, final String cusorId) {
        GsonRequest<Feed> request = new GsonRequest<Feed>(Request.Method.GET, String.format(LASTEST, count, cusorId));
        request.setClazz(Feed.class);
        return request;
    }

    public final Request<Feed> createHotRequest(final int count, final String cusorId) {
        GsonRequest<Feed> request = new GsonRequest<Feed>(Request.Method.GET, String.format(HOT, count, cusorId));
        request.setClazz(Feed.class);
        return request;
    }

    public final Request<Feed> createFeedRequest(final int page, final int count) {
        GsonRequest<Feed> request = new GsonRequest<Feed>(Request.Method.GET, String.format(FEED_LIST, page, count));
        request.setClazz(Feed.class);
        return request;
    }

    public final Request<PersionFeed> createOthersFeedRequest(final String userId, final int page, final int count) {
        GsonRequest<PersionFeed> request = new GsonRequest<PersionFeed>(Request.Method.GET, String.format(PERSION_FEED, userId, page, count));
        request.setClazz(PersionFeed.class);
        return request;
    }

    public final Request<OthersFeedHeader> createOthersFeedHeader(final String userId) {
        GsonRequest<OthersFeedHeader> request = new GsonRequest<OthersFeedHeader>(Request.Method.GET, String.format(OTHERS_FEED_HEADER, userId));
        request.setClazz(OthersFeedHeader.class);
        return request;
    }

    public final Request<Follow> createOthersFollowRequest(final String userId) {
        GsonRequest<Follow> request = new GsonRequest<Follow>(Request.Method.GET, String.format(OTHERS_FEED_FOLLOW, userId));
        request.setClazz(Follow.class);
        return request;
    }

    public final Request<Follow> createOthersUnFollowRequest(final String userId) {
        GsonRequest<Follow> request = new GsonRequest<Follow>(Request.Method.GET, String.format(OTHERS_UNFEED_FOLLOW, userId));
        request.setClazz(Follow.class);
        return request;
    }
}
