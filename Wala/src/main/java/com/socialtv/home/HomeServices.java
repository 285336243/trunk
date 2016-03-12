package com.socialtv.home;

import com.android.volley.Request;
import com.socialtv.home.entity.Home;
import com.socialtv.home.entity.Search;
import com.socialtv.http.GsonRequest;
import com.socialtv.personcenter.entity.UpdateResponse;
import com.socialtv.program.entity.ProgramNews;

/**
 * Created by wlanjie on 14-6-22.
 * Home中的所有网络接口
 */
public class HomeServices {

    private final static String HOME_URL = "home/v2.json";
    private final static String CHECK_UPDATE = "system/version.json?platform=android";
    private final static String NEWS_URI = "group/news.json?type=%s&page=%s&cnt=%s";
    private final static String NEWS_WEBVIEW_URI = "user/news.json?userid=%s&page=%s&cnt=%s";
    private final static String SEARCH_URI = "user/search.json?key=%s&page=%s&cnt=%s";
    private final static String SEARCH_RECOMMEND = "user/recommend.json";

    public Request<Home> createHomeRequest() {
        GsonRequest<Home> request = new GsonRequest<Home>(Request.Method.GET, HOME_URL);
        request.setClazz(Home.class);
        return request;
    }

    public Request<UpdateResponse> createCheckUpdateRequest() {
        GsonRequest<UpdateResponse> request = new GsonRequest<UpdateResponse>(Request.Method.GET, CHECK_UPDATE);
        request.setClazz(UpdateResponse.class);
        return request;
    }

    public Request<ProgramNews> createNewsRequest(final String type, final long page, final long count) {
        GsonRequest<ProgramNews> request = new GsonRequest<ProgramNews>(Request.Method.GET, String.format(NEWS_URI, type, page, count));
        request.setClazz(ProgramNews.class);
        return request;
    }

    public final Request<ProgramNews> createNewsWebViewRequest(final String id, final int page, final int count) {
        GsonRequest<ProgramNews> request = new GsonRequest<ProgramNews>(Request.Method.GET, String.format(NEWS_WEBVIEW_URI, id, page, count));
        request.setClazz(ProgramNews.class);
        return request;
    }

    public final Request<Search> createSearchRequest(final String key, final int page, final int count) {
        GsonRequest<Search> request = new GsonRequest<Search>(Request.Method.GET, String.format(SEARCH_URI, key, page, count));
        request.setClazz(Search.class);
        return request;
    }

    public final Request<Search> createSearchRecommendRequest() {
        GsonRequest<Search> request = new GsonRequest<Search>(Request.Method.GET, SEARCH_RECOMMEND);
        request.setClazz(Search.class);
        return request;
    }
}
