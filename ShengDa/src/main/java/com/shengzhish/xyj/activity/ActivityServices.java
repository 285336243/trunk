package com.shengzhish.xyj.activity;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.shengzhish.xyj.activity.entity.Activity;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.CommentListResponse;
import com.shengzhish.xyj.activity.entity.VoteListResponse;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.core.Log;
import com.shengzhish.xyj.core.PageIterator;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.util.CacheDataKeeper;

import java.util.Locale;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityServices extends AbstractService {

    private static final String COMMENT_LIST_URL = "activity/status_post.json?id=%s&cusorId=%s&cnt=%s";
    private static final String VOTE_LIST_URL = "activity/vote_list.json?id=%s";
    @Inject
    private Context context;

    public final static String ACTIVITY = "activity/list.json?p=%s&cnt=%s";

    private final static String ACTIVITY_DETAILS = "activity/detail.json?id=%s";

    public PageIterator<Activity> pageActivity(final int page, final int count) {
        PagedRequest<Activity> request = createRequest();
        request.setUri(String.format(ACTIVITY, page, count));
        request.setType(new TypeToken<Activity>() {
        }.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<ActivityDetails> pageActivityDetails(final String id) {
        PagedRequest<ActivityDetails> request = createRequest();
        request.setUri(String.format(ACTIVITY_DETAILS, id));
        request.setType(new TypeToken<ActivityDetails>() {
        }.getType());
        return createPageIterator(context, request);
    }

    public Request<Activity> createActivityRequest(final Context context,final int page, final int count) {
        GsonRequest<Activity> request = new GsonRequest<Activity>(Request.Method.GET, String.format(ACTIVITY, page, count));
        request.setClazz(Activity.class);
/*        request.setJsonListenner(new GsonRequest.DataWatch() {
            @Override
            public void JsonListenner(String json) {
                Log.v("person","person jsonlistenner =="+json);
//                CacheDataKeeper.saveActivityResponse(context,json);
            }
        });*/
        return request;
    }

    public Request<ActivityDetails> createActivityDetailRequest(final String id) {
        GsonRequest<ActivityDetails> request = new GsonRequest<ActivityDetails>(Request.Method.GET, String.format(ACTIVITY_DETAILS, id));
        request.setClazz(ActivityDetails.class);
        return request;
    }

    public Request<CommentListResponse> createCommentListRequest(final String id, final String cusorId, final int count) {
        GsonRequest<CommentListResponse> request = new GsonRequest<CommentListResponse>(Request.Method.GET, String.format(COMMENT_LIST_URL, id, cusorId, count));
        request.setClazz(CommentListResponse.class);
        return request;
    }

    public Request<VoteListResponse> createVoteListRequest(String id) {
        GsonRequest<VoteListResponse> request = new GsonRequest<VoteListResponse>(Request.Method.GET, String.format(VOTE_LIST_URL, id));
        request.setClazz(VoteListResponse.class);
        return request;
    }
}
