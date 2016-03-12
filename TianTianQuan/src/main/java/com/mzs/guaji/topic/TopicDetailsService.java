package com.mzs.guaji.topic;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.topic.entity.StarDetail;
import com.mzs.guaji.topic.entity.TopicDetails;
import com.mzs.guaji.topic.entity.TopicDetailsPosts;

import java.util.Map;

/**
 * Created by wlanjie on 14-5-22.
 */
public class TopicDetailsService extends AbstractService {

    private static final String LIST_CONTENT = "topic/posts.json?id=%s&p=%s&cnt=%s&type=%s";
    private static final String LIST_HEADER = "topic/detail.json?id=%s&type=%s";
    private static final String CELEBRITY_POST = "celebrity/topic_post_v1.json?id=%s&type=%s";
    private static final String REPORT = "system/expose.json";
    private static final String SUPPORT = "topic/post_support.json?id=%s&type=%s";
    private static final String LIKE = "topic/like.json?id=%s&type=%s";
    private static final String UNLIKE = "topic/unlike.json?id=%s&type=%s";
    private static final String COMMITCOMMENT = "topic/posts.json";
    private static final String EXPOSE = "system/expose.json";
    private static final String REPLY = "topic/reply.json";
    private static final String DELETE_TOPIC = "topic/del.json?id=%s&type=%s";

    @Inject
    Context context;

    public PageIterator<TopicDetailsPosts> pageTopicDetails(final long topicId, final int page, final int count, final String type) {
        PagedRequest<TopicDetailsPosts> request = createRequest();
        request.setType(new TypeToken<TopicDetailsPosts>(){}.getType());
        request.setUri(String.format(LIST_CONTENT, topicId, page, count, type));
        return createPageIterator(context, request);
    }

    public PageIterator<TopicDetails> pageTopicDetailsHeader(final long topicId, final String type) {
        PagedRequest<TopicDetails> request = createRequest();
        request.setUri(String.format(LIST_HEADER, topicId, type));
        request.setType(new TypeToken<TopicDetails>(){}.getType());
        return  createPageIterator(context, request);
    }

    public PageIterator<StarDetail> pageCelebrityPost(final long topicId, final String type) {
        PagedRequest<StarDetail> request = createRequest();
        request.setUri(String.format(CELEBRITY_POST, topicId, type));
        request.setType(new TypeToken<StarDetail>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageReport(final Map<String, String> bodyMap) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(REPORT);
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        request.setBodyMap(bodyMap);
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageSupport(final long id, final String type) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(SUPPORT, id, type));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageLike(final long topicId, final String type) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(LIKE, topicId, type));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageUnLike(final long topicId, final String type) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(UNLIKE, topicId, type));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageCommitComment(final Map<String, String> bodyMap) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setBodyMap(bodyMap);
        request.setUri(COMMITCOMMENT);
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageReply(final Map<String, String> bodyMap) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(REPLY);
        request.setBodyMap(bodyMap);
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageExpose(final Map<String, String> bodyMap) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(EXPOSE);
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        request.setBodyMap(bodyMap);
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageDeleteTopic(final long topicId, final String type) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(DELETE_TOPIC, topicId, type));
        request.setDelete("delete");
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }
 }
