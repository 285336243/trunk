package com.jiujie8.choice.home;

import com.jiujie8.choice.Response;
import com.jiujie8.choice.home.entity.ChoiceList;
import com.jiujie8.choice.home.entity.Comment;
import com.jiujie8.choice.home.entity.PostList;
import com.jiujie8.choice.home.entity.VoteResponse;
import com.jiujie8.choice.home.entity.VoteResultResponse;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;

import java.util.Map;

/**
 * Created by wlanjie on 14/12/4.
 * 主页中的所有网络请求的接口
 */
public class HomeServices {

    private final static String HOME_PAGER = "choice/list";
    private final static String HOME_POST = "choice/post/list";
    private final static String COMMENT_POST = "choice/post";
    private final static String REPORT_POST = "choice/report";
    private final static String DELETE_CHOICE = "/choice/delete";
    private final static String FAVORITE = "choice/favorite";
    private final static String CANCEL_VAVORITE = "choice/unFavorite";
    private final static String CHOICE_VOTE = "choice/vote";
    private final static String VOTE_RESULT = "choice/vote/list";

    public final static GsonRequest<ChoiceList> createHomePagerRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(ChoiceList.class, HOME_PAGER, map);
    }

    public final static GsonRequest<PostList> createHomePostRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(PostList.class, HOME_POST, map);
    }

    public final static GsonRequest<Comment> createCommentPostRequest(final Map<String, String> map) {
        return HttpUtils.createPostRequest(Comment.class, COMMENT_POST, map);
    }

    public final static GsonRequest<Response> createReportPostRequest(final Map<String, String> map) {
        return HttpUtils.createPostRequest(Response.class, REPORT_POST, map);
    }

    public final static GsonRequest<Response> createDeleteChoiceRequest(final Map<String, String> map) {
        return HttpUtils.createDeleteRequest(Response.class, DELETE_CHOICE, map);
    }

    public final static GsonRequest<Response> createFavoriteRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(Response.class, FAVORITE, map);
    }

    public final static GsonRequest<Response> createCancelFavoriteRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(Response.class, CANCEL_VAVORITE, map);
    }

    public final static GsonRequest<VoteResponse> createVoteRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(VoteResponse.class, CHOICE_VOTE, map);
    }

    public final static GsonRequest<VoteResultResponse> createVoteResultRequest(final Map<String, String> map) {
        return HttpUtils.createGetRequest(VoteResultResponse.class, VOTE_RESULT, map);
    }
}
