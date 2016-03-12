package com.shengzhish.xyj.persionalcore;

import com.android.volley.Request;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.core.AbstractService;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.persionalcore.entity.Login;
import com.shengzhish.xyj.persionalcore.entity.MessageAmount;
import com.shengzhish.xyj.persionalcore.entity.Update;

/**
 * person atrr url create
 */
public class UserPersonService extends AbstractService {

    private static final String LOGIN_URI = "user/self.json";
    private static final String MESSAGE_AMOUNT ="user/message_cal.json";
    private static final String Exit_ACCOUNT="identity/signout.json";
    private final static String CHECK_UPDATE = "system/version.json?p=android";

    public Request<Login> createPersonRequest( ) {
        GsonRequest<Login> request = new GsonRequest<Login>(Request.Method.GET, LOGIN_URI);
        request.setClazz(Login.class);
        return request;
    }

    public Request<MessageAmount> createMessageRequest( ) {
        GsonRequest<MessageAmount> request = new GsonRequest<MessageAmount>(Request.Method.GET, MESSAGE_AMOUNT);
        request.setClazz(MessageAmount.class);
        return request;
    }
    public Request<Response>creatExitRequest(){
        GsonRequest<Response> request=new GsonRequest<Response>(Request.Method.GET,Exit_ACCOUNT);
        request.setClazz(Response.class);
        return  request;
    }
    public Request<Update> createUpdateRequest() {
        GsonRequest<Update> request = new GsonRequest<Update>(Request.Method.GET, CHECK_UPDATE);
        request.setClazz(Update.class);
        return request;
    }
}
