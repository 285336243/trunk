package com.mzs.guaji.offical;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.offical.entity.OfficialTvCircleTopic;

/**
 * Created by wlanjie on 14-5-27.
 */
public class OfficialTopicService extends AbstractService {

//    @Inject
    Context context;

    @Inject
    public OfficialTopicService(Context context) {
        this.context = context;
    }

    private final static String TOPIC = "topic/group.json?groupId=%s&p=%s&cnt=%s";
    private final static String LIKE = "topic/like.json?id=%s&type=group";
    private final static String UNLIKE = "topic/unlike.json?id=%s&type=group";

    public PageIterator<OfficialTvCircleTopic> pageTopic(final long groupId, final long page, final long count) {
        PagedRequest<OfficialTvCircleTopic> request = createRequest();
        request.setUri(String.format(TOPIC, groupId, page, count));
        request.setType(new TypeToken<OfficialTvCircleTopic>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageLike(final long topicId) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(LIKE, topicId));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageUnLike(final long topicId) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(UNLIKE, topicId));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }
}
