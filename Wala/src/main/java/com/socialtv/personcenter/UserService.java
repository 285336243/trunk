package com.socialtv.personcenter;

import com.android.volley.Request;
import com.socialtv.Response;
import com.socialtv.http.BodyRequest;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.MultipartRequest;
import com.socialtv.personcenter.entity.AuthCode;
import com.socialtv.personcenter.entity.ExternalAccounts;
import com.socialtv.personcenter.entity.ForgetPassword;
import com.socialtv.personcenter.entity.InviteCode;
import com.socialtv.personcenter.entity.InviteMobile;
import com.socialtv.personcenter.entity.RegisterResponse;
import com.socialtv.publicentity.MessageResource;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-8-12.
 */
public class UserService {
    private final static String GET_AUTH_CODE = "system/verifycode.json?mobile=%s&ignoreCheck=%s";
    private final static String VERIFY_AUTH_CODE = "system/verifycode.json";
    private static final String REGISTE_URL = "identity/signup.json";
    private final static String INVITE_CODE = "user/invitecode.json";
    private final static String MOBILES = "user/uploadmobile.json";
    private final static String INVITE_FRIEND = "user/invite.json";
    private final static String BINDING_MOBILE = "user/mobile.json";
    private final static String EXTERNAL_ACCOUNT = "identity/external_account.json";
    private final static String WEIXIN_MESSAGE = "user/invite_message.json";
    private final static String DELETE_BINDING_MOBILE = "user/mobile.json";
    private final static String DELETE_EXTERNAL_ACCOUNT = "identity/external_account.json?externalAccountId=%s";
    private final static String FORGET_PASSWORD = "identity/reset_password.json";
    private final static String MODIFY_PASSWORD = "identity/change_password.json";

    public final Request<AuthCode> createGetAuthCode(final String mobileNumber, final int ignoreCheck) {
        GsonRequest<AuthCode> request = new GsonRequest(Request.Method.GET, String.format(GET_AUTH_CODE, mobileNumber, ignoreCheck));
        request.setClazz(AuthCode.class);
        return request;
    }

    public final Request<Response> createModifyPassword(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, MODIFY_PASSWORD);
        request.setHeaders(bodys);
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createVerifyAuthCode(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, VERIFY_AUTH_CODE);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public final Request<ForgetPassword> createForgetPassword(final Map<String, String> bodys) {
        GsonRequest<ForgetPassword> request = new GsonRequest<ForgetPassword>(Request.Method.POST, FORGET_PASSWORD);
        request.setHeaders(bodys);
        request.setClazz(ForgetPassword.class);
        return request;
    }

    public final Request<RegisterResponse> createRegister(final File file, final String account, final String nickname, final String password) {
        MultipartRequest<RegisterResponse> request = new MultipartRequest<RegisterResponse>(REGISTE_URL);
        request.addMultipartStringEntity("account", account)
                .addMultipartStringEntity("nickname", nickname)
                .addMultipartStringEntity("password", password);
        if (file != null) {
            request.addMultipartFileEntity("avatar", file);
        }
        request.setClazz(RegisterResponse.class);
        return request;
    }

    public final Request<InviteCode> createInviteCode(final Map<String, String> bodys) {
        GsonRequest<InviteCode> request = new GsonRequest(Request.Method.POST, INVITE_CODE);
        request.setHeaders(bodys);
        request.setClazz(InviteCode.class);
        return request;
    }

    public final Request<InviteMobile> createMobiles(final List<String> mobiles) {
        BodyRequest<InviteMobile> request = new BodyRequest<InviteMobile>(Request.Method.POST, MOBILES, mobiles);
        request.setClazz(InviteMobile.class);
        return request;
    }

    public final Request<Response> createInviteFriend(final List<String> phoneNumbers) {
        BodyRequest<Response> request = new BodyRequest<Response>(Request.Method.POST, INVITE_FRIEND, phoneNumbers);
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createBindingMobile(final Map<String, String> bodys) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, BINDING_MOBILE);
        request.setClazz(Response.class);
        request.setHeaders(bodys);
        return request;
    }

    public final Request<ExternalAccounts> createExternalAccount(final Map<String, String> bodys) {
        GsonRequest<ExternalAccounts> request = new GsonRequest<ExternalAccounts>(Request.Method.POST, EXTERNAL_ACCOUNT);
        request.setHeaders(bodys);
        request.setClazz(ExternalAccounts.class);
        return request;
    }

    public final Request<MessageResource> createWeixinMessage() {
        GsonRequest<MessageResource> request = new GsonRequest<MessageResource>(Request.Method.GET, WEIXIN_MESSAGE);
        request.setClazz(MessageResource.class);
        return request;
    }

    public final Request<Response> createDeleteBindingMobile() {
        final GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, DELETE_BINDING_MOBILE);
        request.setClazz(Response.class);
        return request;
    }

    public final Request<Response> createDeleteExternalAccount(final String externalAccountId) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.DELETE, String.format(DELETE_EXTERNAL_ACCOUNT, externalAccountId));
        request.setClazz(Response.class);
        return request;
    }
}
