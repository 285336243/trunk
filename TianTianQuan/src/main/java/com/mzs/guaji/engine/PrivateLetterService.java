package com.mzs.guaji.engine;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.entity.PrivateLetter;

/**
 * Created by wlanjie on 14-3-3.
 */
public class PrivateLetterService extends AbstractService {

    @Inject
    Context context;
    public PrivateLetterService() {
        super();
    }

    public PageIterator<PrivateLetter> pagePrivateLetter(final int start, final int size, final String uri) {
        PagedRequest<PrivateLetter> request = createPagedRequest(start, size);
        request.setType(new TypeToken<PrivateLetter>(){}.getType());
        return createPageIterator(context, request);
    }
}
