package com.mzs.guaji.topic;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.topic.entity.DynamicToic;
import com.mzs.guaji.topic.entity.FindTopic;

/**
 * Created by wlanjie on 14-5-20.
 */
public class TopicService extends AbstractService {

    private final static String DYNAMIC = "feed/attention_v1.json?p=%s&cnt=%s";
    private final static String FIND = "topic/discovery.json?p=%s&cnt=%s&token=%s";

    @Inject
    Context context;

    public PageIterator<FindTopic> pageFind(final int page, final int count, final String token) {
        PagedRequest<FindTopic> request = createPagedRequest();
        request.setUri(String.format(FIND, page, count, token));
        request.setType(new TypeToken<FindTopic>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DynamicToic> pageDynamic(final int page, final int count) {
        PagedRequest<DynamicToic> request = createRequest();
        request.setUri(String.format(DYNAMIC, page, count));
        request.setType(new TypeToken<DynamicToic>(){}.getType());
        return createPageIterator(context, request);
    }
}
