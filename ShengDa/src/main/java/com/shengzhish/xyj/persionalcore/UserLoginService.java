package com.shengzhish.xyj.persionalcore;

import com.android.volley.Request;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.persionalcore.entity.Login;

import java.util.Map;

/**
 * user login url create
 */
public class UserLoginService extends AbstractService {

    private static final String LOGIN_URI = "identity/signin.json";

    public Request<Login> createLoginRequest(final Map<String, String> bodyMap) {
        GsonRequest<Login> request = new GsonRequest<Login>(Request.Method.POST, LOGIN_URI);
        request.setClazz(Login.class);
        request.setHeaders(bodyMap);
        return request;
    }
}
