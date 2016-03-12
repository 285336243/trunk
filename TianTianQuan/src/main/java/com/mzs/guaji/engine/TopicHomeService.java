package com.mzs.guaji.engine;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.entity.ActivityTopic;

/**
 * Created by wlanjie on 14-4-9.
 */
public class TopicHomeService extends AbstractService {

    @Inject
    private Context context;

    public PageIterator<ActivityTopic> createTopicHome(final long activityId, final int start, final int size) {
        PagedRequest<ActivityTopic> request = createPagedRequest(start, size);
        request.setUri(getTopicHomeRequest(activityId, start, size));
        request.setType(new TypeToken<ActivityTopic>() {}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<ActivityTopic> createTopicHomeHot(final long activityId, final int start, final int size) {
        PagedRequest<ActivityTopic> request = createPagedRequest(start, size);
        request.setUri(getTopicHomeHotRequest(activityId, start, size));
        request.setType(new TypeToken<ActivityTopic>() {}.getType());
        return createPageIterator(context, request);
    }

    private String getTopicHomeRequest(long activityId, long page, long count) {
        return "topic/activity.json?activityId=" + activityId + "&p=" + page + "&cnt=" + count;
    }

    private String getTopicHomeHotRequest(long activityId, long page, long count) {
        return "topic/activity_hot.json?activityId=" + activityId + "&p=" + page + "&cnt=" + count;
    }
}
