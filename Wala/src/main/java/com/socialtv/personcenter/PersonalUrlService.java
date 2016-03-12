package com.socialtv.personcenter;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.MultipartRequest;
import com.socialtv.personcenter.entity.FollowResponse;
import com.socialtv.personcenter.entity.JoinResponse;
import com.socialtv.personcenter.entity.PhotoAlbum;
import com.socialtv.personcenter.entity.UserResponse;

import java.io.File;

/**
 * person atrr url create
 */
public class PersonalUrlService {

    private static final String JOIN_GROUP = "user/joins.json";
    private static final String Exit_ACCOUNT = "identity/signout.json";
    private final static String CHECK_UPDATE = "system/version.json?p=android";
    private static final String GET_URL = "user/self.json";
    private final static String PHOTO_ALBUM_URI = "user/gallery.json?userid=%s";
    private final static String DELETE_PHOTO_ALBUM_URI = "user/gallery.json?id=%s";
    private final static String UPLOAD_PHOTO_ALBUM_URI = "user/gallery.json";
    private final static String FOLLOW_LIST_URI = "user/follows.json?userid=%s";
    private final static String UNFOLLOW_URI = "user/unfollow.json?userid=%s";
    private final static String FANS_URI = "user/fans.json?userid=%s";
    private final static String FOLLOW_URI = "user/follow.json?userid=%s";
    private final static String FOLLOW_RECOMMEND = "user/recommend.json";
    private final static String CELEBRITY_RECOMMEND ="celebrity/recommend.json";
    private final static String GROUP_RECOMMEND = "group/recommend.json";

    public Request<UserResponse> createPersonRequest() {
        GsonRequest<UserResponse> request = new GsonRequest<UserResponse>(Request.Method.GET, GET_URL);
        request.setClazz(UserResponse.class);
        return request;
    }

    public Request<Response> creatExitRequest() {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.GET, Exit_ACCOUNT);
        request.setClazz(Response.class);
        return request;
    }
   public Request<JoinResponse> createJoinGroupRequest( ) {
        GsonRequest<JoinResponse> request = new GsonRequest<JoinResponse>(Request.Method.GET, JOIN_GROUP);
        request.setClazz(JoinResponse.class);
        return request;
    }

    public final Request<PhotoAlbum> createPhotoAlbumRequest(final String userId) {
        GsonRequest<PhotoAlbum> request = new GsonRequest<PhotoAlbum>(Request.Method.GET, String.format(PHOTO_ALBUM_URI, userId));
        request.setClazz(PhotoAlbum.class);
        return request;
    }

    public final Request<Response> createDeletePhotoAlbumRequest(final String id) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, String.format(DELETE_PHOTO_ALBUM_URI, id));
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createUploadPhotoAlbumRequest(final File file) {
        MultipartRequest<Response> request = new MultipartRequest<Response>(UPLOAD_PHOTO_ALBUM_URI);
        request.addMultipartFileEntity("img", file);
        request.setClazz(Response.class);
        return request;
    }

    public final Request<FollowResponse> createFollowListRequest(final String userId) {
        GsonRequest<FollowResponse> request = new GsonRequest<FollowResponse>(Request.Method.GET, String.format(FOLLOW_LIST_URI, userId));
        request.setClazz(FollowResponse.class);
        return request;
    }

    public final Request<FollowResponse> createFollowRequest(final String userId) {
        GsonRequest<FollowResponse> request = new GsonRequest<FollowResponse>(Request.Method.GET, String.format(FOLLOW_URI, userId));
        request.setClazz(FollowResponse.class);
        return request;
    }

    public final Request<FollowResponse> createUnFollowRequest(final String userId) {
        GsonRequest<FollowResponse> request = new GsonRequest<FollowResponse>(Request.Method.GET, String.format(UNFOLLOW_URI, userId));
        request.setClazz(FollowResponse.class);
        return request;
    }

    public final Request<FollowResponse> createFansRequest(final String userId) {
        GsonRequest<FollowResponse> request = new GsonRequest<FollowResponse>(Request.Method.GET, String.format(FANS_URI, userId));
        request.setClazz(FollowResponse.class);
        return request;
    }

    public final Request<FollowResponse> createRecommendRequest() {
        GsonRequest<FollowResponse> request = new GsonRequest<FollowResponse>(Request.Method.GET, FOLLOW_RECOMMEND);
        request.setClazz(FollowResponse.class);
        return request;
    }

    public final Request<JoinResponse> createCelebrityRecommendRequest() {
        GsonRequest<JoinResponse> request = new GsonRequest<JoinResponse>(Request.Method.GET, CELEBRITY_RECOMMEND);
        request.setClazz(JoinResponse.class);
        return request;
    }

    public final Request<JoinResponse> createGroupRecommendRequest() {
        GsonRequest<JoinResponse> request = new GsonRequest<JoinResponse>(Request.Method.GET, GROUP_RECOMMEND);
        request.setClazz(JoinResponse.class);
        return request;
    }
}
