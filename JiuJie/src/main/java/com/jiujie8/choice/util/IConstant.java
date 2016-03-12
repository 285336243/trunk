package com.jiujie8.choice.util;

/**
 * Created by wlanjie on 14/11/20.
 * <p/>
 * 所有常量的接口
 */
public interface IConstant {
    public final static String DOMAIN = "http://210.14.68.162:8080/";
    //    public final static String DOMAIN = "http://192.168.0.104:8080/";
    public final static boolean DEBUG = false;
    public final static String STATE_OK = "0000";

    public final static String MODE_ITEM = "MODE_ITEM";

    //登录状态, 用户信息
    public final static String USER_LOGIN = "USER_LOGIN";
    public final static String LOGIN_STATE = "LOGIN_STATE";
    public final static String USER_ID = "USER_ID";
    public final static String USER_NICKNAME = "USER_NICKNAME";
    public final static String USER_AVATAR = "USER_AVATAR";
    public final static String USER_GENDER = "USER_GENDER";
    public final static String USER_EXPIRED_TIME = "USER_EXPIRED_TIME";
    public final static String USER_TOKEN = "USER_TOKEN";
    public final static String IS_SELF = "IS_SELF";

    //Reqeust filed, don't not change
    public final static String CHOICE_ID = "choiceId";
    public final static String REPORT_MESSAGE = "repostMessage";
    public final static String IMAGE_PATH = "image_path";
    public final static String CROP_IMAGE_ACTION = "crop_image_action";
    public final static String USER = "USER";
    public final static String USER_MODIFY = "USER_MODIFY";
    public final static int JIUJIE_RESULT_CODE = 0XAD;
    public final static String ChOICE_MODE = "ChOICE_MODE";
    public final static String LIST = "LIST";
    public final static int SELF_REPLY_CODE = 0xFA;
    public final static int SELF_COLLECT_CODE = 0XDC;
    public final static int OTHER_RESULT_CODE = 0XBE;
}
