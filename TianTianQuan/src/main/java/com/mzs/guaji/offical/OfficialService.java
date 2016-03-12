package com.mzs.guaji.offical;

import android.content.Context;

import com.android.volley.PagedRequest;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.core.AbstractService;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.entity.ApplyType;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.Game;
import com.mzs.guaji.offical.entity.OfficialModules;

/**
 * Created by wlanjie on 14-5-27.
 */
public class OfficialService extends AbstractService {

    @Inject
    private Context context;

    private final static String MODULES = "group/modules.json?id=%s&platform=ANDROID";
    private final static String QUIT = "group/quit.json?id=%s";
    private final static String JOIN = "group/join.json?id=%s";
    private final static String GAME = "group/games_list.json?gid=%stvCircleId&p=1&cnt=100&platform=ANDROID";
    private final static String APPLY = "group/entry_form.json?groupId=%s";

    public PageIterator<OfficialModules> pageModules(final long tvCircleId) {
        PagedRequest<OfficialModules> request = createRequest();
        request.setUri(String.format(MODULES, tvCircleId));
        request.setType(new TypeToken<OfficialModules>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageQuit(final long tvCircleId) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(QUIT, tvCircleId));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<DefaultReponse> pageJoin(final long tvCircleId) {
        PagedRequest<DefaultReponse> request = createRequest();
        request.setUri(String.format(JOIN, tvCircleId));
        request.setType(new TypeToken<DefaultReponse>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<Game> pageGame(final long tvCircleId) {
        PagedRequest<Game> request = createRequest();
        request.setUri(String.format(GAME, tvCircleId));
        request.setType(new TypeToken<Game>(){}.getType());
        return createPageIterator(context, request);
    }

    public PageIterator<ApplyType> pageApply(final long tvCircleId) {
        PagedRequest<ApplyType> request = createRequest();
        request.setUri(String.format(APPLY, tvCircleId));
        request.setType(new TypeToken<ApplyType>(){}.getType());
        return createPageIterator(context, request);
    }
}
