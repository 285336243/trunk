package com.socialtv.program;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.GsonRequest;
import com.socialtv.program.entity.GroupChat;
import com.socialtv.program.entity.Join;
import com.socialtv.program.entity.Program;
import com.socialtv.program.entity.ProgramHeader;
import com.socialtv.program.entity.ProgramNews;
import com.socialtv.program.entity.UpScreen;

import java.util.Map;

/**
 * Created by wlanjie on 14-7-8.
 * 节目组中所有网络请求接口
 */
public class ProgramServices {
    private final static String PROGRAM_HEADER_URI = "group/detail.json?id=%s";
    private final static String PROGRAM_LIST_URI = "group/topic.json?id=%s&page=%s&cnt=%s";
    private final static String PROGRAM_JOIN_URI = "group/follow.json?id=%s";
    private final static String PROGRAM_UN_JOIN_URI = "group/unfollow.json?id=%s";
    private final static String GROUP_CHAT = "group/posts.json?id=%s&position=%s&cnt=%s";
    private final static String MESSAGE_NUMBER_URI = "group/posts_cal.json?id=%s&position=%s";
    private final static String PUBLISH_POST_URI = "group/post.json";
    private final static String PROGRAM_NEWS_URI = "group/news.json?id=%s&page=%s&cnt=%s";
    private final static String UPSCREEN_URI = "post/notify.json?cursorId=%s";

    public final Request<ProgramHeader> createProgramHeaderRequest(final String programId) {
        GsonRequest<ProgramHeader> request = new GsonRequest<ProgramHeader>(Request.Method.GET, String.format(PROGRAM_HEADER_URI, programId));
        request.setClazz(ProgramHeader.class);
        return request;
    }

    public final Request<Program> createProgramListRequest(final String programId, final int page, final int count) {
        GsonRequest<Program> request = new GsonRequest<Program>(Request.Method.GET, String.format(PROGRAM_LIST_URI, programId, page, count));
        request.setClazz(Program.class);
        return request;
    }

    public final Request<Join> createProgramJoinRequest(final String programId) {
        GsonRequest<Join> request = new GsonRequest<Join>(Request.Method.GET, String.format(PROGRAM_JOIN_URI, programId));
        request.setClazz(Join.class);
        return request;
    }

    public final Request<Join> createProgramUnJoinRequest(final String programId) {
        GsonRequest<Join> request = new GsonRequest<Join>(Request.Method.GET, String.format(PROGRAM_UN_JOIN_URI, programId));
        request.setClazz(Join.class);
        return request;
    }

    public final Request<GroupChat> createGroupChatRequest(final String programId, final String position, final int count) {
        GsonRequest<GroupChat> request = new GsonRequest<GroupChat>(Request.Method.GET, String.format(GROUP_CHAT, programId, position, count));
        request.setClazz(GroupChat.class);
        return request;
    }

    public final Request<Response> createMessageNumberRequest(final String programId, final String position) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, String.format(MESSAGE_NUMBER_URI, programId, position));
        request.setClazz(Response.class);
        return request;
    }

    public final Request<UpScreen> createUpScreenRequest(final String cursorId) {
        GsonRequest<UpScreen> request = new GsonRequest<UpScreen>(Request.Method.GET, String.format(UPSCREEN_URI, cursorId));
        request.setClazz(UpScreen.class);
        return request;
    }

    public final Request<Response> createPublishMessageRequest(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, PUBLISH_POST_URI);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public final Request<ProgramNews> createNewsRequest(final String programId, final int page, final int count) {
        GsonRequest<ProgramNews> request = new GsonRequest<ProgramNews>(Request.Method.GET, String.format(PROGRAM_NEWS_URI, programId, page, count));
        request.setClazz(ProgramNews.class);
        return request;
    }
}
