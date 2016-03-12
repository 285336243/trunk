package com.socialtv.activity;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.activity.entitiy.Activity;
import com.socialtv.activity.entitiy.ActivityDetail;
import com.socialtv.activity.entitiy.ScreenShot;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.MultipartRequest;

import java.io.File;

/**
 * Created by wlanjie on 14-6-25.
 *
 * 活动所有的接口请求
 */
public class ActivityServices {

    private final static String HEADER_URL = "activity/detail.json?id=%s";
    private final static String HOT_URL = "activity/topic_hot.json?id=%s&page=%s&cnt=%s";
    private final static String LATEST_URL = "activity/topic_latest.json?id=%s&page=%s&cnt=%s";
    private final static String SCREEN_SHOT = "activity/capture.json?id=%s";
    private final static String SUBMIT_ACTIVITY_TOPIC = "activity/topic.json";

    public Request<ActivityDetail> createHeaderRequest(final String id) {
        GsonRequest<ActivityDetail> request = new GsonRequest<ActivityDetail>(Request.Method.GET, String.format(HEADER_URL, id));
        request.setClazz(ActivityDetail.class);
        return request;
    }

    public Request<Activity> createHotRequest(final String id, final int page, final int count) {
        GsonRequest<Activity> request = new GsonRequest<Activity>(Request.Method.GET, String.format(HOT_URL, id, page, count));
        request.setClazz(Activity.class);
        return request;
    }

    public Request<Activity> createLatestRequest(final String id, final int page, final int count) {
        GsonRequest<Activity> request = new GsonRequest<Activity>(Request.Method.GET, String.format(LATEST_URL, id, page, count));
        request.setClazz(Activity.class);
        return request;
    }

    public Request<ScreenShot> createScreenShotRequest(final String activityId) {
        GsonRequest<ScreenShot> request = new GsonRequest<ScreenShot>(Request.Method.GET, String.format(SCREEN_SHOT, activityId));
        request.setClazz(ScreenShot.class);
        return request;
    }

    public Request<Response> createSubmitActivityTopicRequest(final String id, final String msg, final File imageFile) {
        MultipartRequest<Response> request = new MultipartRequest<Response>(SUBMIT_ACTIVITY_TOPIC);
        if (imageFile != null) {
            request.addMultipartFileEntity("img", imageFile);
        }
        request.addMultipartStringEntity("id", id)
                .addMultipartStringEntity("msg", msg);
        request.setClazz(Response.class);
        return request;
    }
}
