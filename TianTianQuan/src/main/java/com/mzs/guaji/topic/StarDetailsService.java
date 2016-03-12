package com.mzs.guaji.topic;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.topic.entity.DynamicToic;
import com.mzs.guaji.topic.entity.StarTopicList;

/**
 * 明星请求的网络服务
 */
public class StarDetailsService extends AbstractService {

    private static final String STAR_TOPIC_LIST = "topic/celebrity.json?uid=%s&p=%s&cnt=%s";
    private static final String STAR_DYNAMIC = "feed/celebrity.json?uid=%s&p=%s&cnt=%s";


    @Inject
    Context context;

    public PageIterator<StarTopicList> starTopicList(final long starId, final int page, final int count) {
        PagedRequest<StarTopicList> request = createRequest();
        request.setType(new TypeToken<StarTopicList>() {
        }.getType());
        request.setUri(String.format(STAR_TOPIC_LIST, starId, page, count));
        return createPageIterator(context, request);
    }

    public PageIterator<DynamicToic> starDynamic(final long starId, final int page, final int count) {
        PagedRequest<DynamicToic> request = createRequest();
        request.setUri(String.format(STAR_DYNAMIC, starId, page, count));
        request.setType(new TypeToken<DynamicToic>() {
        }.getType());
        return createPageIterator(context, request);
    }


}
