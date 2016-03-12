package com.jiujie8.choice.choice;

import com.jiujie8.choice.Response;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.http.MultipartRequest;

import java.util.Map;

/**
 * Created by wlanjie on 14/12/24.
 *
 */
public class ChoiceService {

    private final static String CHOICE_OR = "choice/or";
    private final static String CHOICE_TEXT = "choice/text";
    private final static String CHOICE_YN = "choice/yn";

    public final static MultipartRequest<Response> createChoiceOrRequest(final Map<String, Object> mBodys) {
        return HttpUtils.createMultipartRequest(Response.class, CHOICE_OR, mBodys);
    }


    public final static MultipartRequest<Response> createChoiceTextRequest(final Map<String, Object> mBodys) {
        return HttpUtils.createMultipartRequest(Response.class, CHOICE_TEXT, mBodys);
    }


    public final static MultipartRequest<Response> createChoiceYnRequest(final Map<String, Object> mBodys) {
        return HttpUtils.createMultipartRequest(Response.class, CHOICE_YN, mBodys);
    }
}
