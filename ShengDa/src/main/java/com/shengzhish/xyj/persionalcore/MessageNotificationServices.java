package com.shengzhish.xyj.persionalcore;

import com.android.volley.Request;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.persionalcore.entity.MessageNotification;

/**
 * Created by wlanjie on 14-6-18.
 */
public class MessageNotificationServices {

    private final static String URL = "user/message.json?p=%s&cnt=%s";

    public Request<MessageNotification> createMessageNotificationRequest(final int page, final int count) {
        GsonRequest<MessageNotification> request = new GsonRequest<MessageNotification>(Request.Method.GET, String.format(URL, page, count));
        request.setClazz(MessageNotification.class);
        return request;
    }
}
