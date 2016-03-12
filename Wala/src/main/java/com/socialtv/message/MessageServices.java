package com.socialtv.message;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.home.entity.Badges;
import com.socialtv.http.GsonRequest;
import com.socialtv.message.entity.Message;
import com.socialtv.message.entity.PrivateLetter;
import com.socialtv.message.entity.PrivateLetterDetail;

import java.util.Map;

/**
 * Created by wlanjie on 14-7-2.
 * 所有消息和私信的网络接口
 */
public class MessageServices {
    private final static String MESSAGE_URI = "message/list.json?position=%s&cnt=%s";
    private final static String DELETE_MESSAGE_URI = "message/del.json?id=%s";
    private final static String SINGLE_READ_MESSAGE = "message/read.json?id=%s";
    private final static String READ_ALL_MESSAGE = "message/readall.json?id=%s";
    private final static String PRIVATE_LETTER = "privatePost/aggregation.json?page=%s&cnt=%s";
    private final static String PRIVATE_LETTER_DETAIL = "privatePost/list.json?position=%s&cnt=%s&userid=%s";
    private final static String PUBLISH_PRIVATE_LETTER = "privatePost/new.json";
    private final static String DELETE_PRIVATE_LETTER = "privatePost/del.json?id=%s";
    private final static String BADGES = "system/badges.json";

    public final Request<Message> createMessageRequest(final String cusorId, final int count) {
        GsonRequest<Message> request = new GsonRequest<Message>(Request.Method.GET, String.format(MESSAGE_URI, cusorId, count));
        request.setClazz(Message.class);
        return request;
    }

    public final Request<Response> createDeleteMessageRequest(final String messageId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, String.format(DELETE_MESSAGE_URI, messageId));
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createSingleReadMessageRequest(final String messageId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(SINGLE_READ_MESSAGE, messageId));
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createReadAllMessageRequest(final String messageId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(READ_ALL_MESSAGE, messageId));
        request.setClazz(Response.class);
        return request;
    }

    public final Request<PrivateLetter> createPrivateLetterRequest(final int page, final int count) {
        GsonRequest<PrivateLetter> request = new GsonRequest<PrivateLetter>(Request.Method.GET, String.format(PRIVATE_LETTER, page, count));
        request.setClazz(PrivateLetter.class);
        return request;
    }

    public final Request<PrivateLetterDetail> createPrivateLetterDetailRequest(final String cusorId, final int count, final String userId) {
        GsonRequest<PrivateLetterDetail> request = new GsonRequest<PrivateLetterDetail>(Request.Method.GET, String.format(PRIVATE_LETTER_DETAIL, cusorId, count, userId));
        request.setClazz(PrivateLetterDetail.class);
        return request;
    }

    public final Request<Response> createPublishPrivateLetterRequest(Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, PUBLISH_PRIVATE_LETTER);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public final Request<Response> createDeletePrivateLetterRequest(final String id) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, String.format(DELETE_PRIVATE_LETTER, id));
        request.setClazz(Response.class);
        return request;
    }


    public final Request<Badges> createBadgesRequest() {
        GsonRequest<Badges> request = new GsonRequest<Badges>(Request.Method.GET, BADGES);
        request.setClazz(Badges.class);
        return request;
    }
}
