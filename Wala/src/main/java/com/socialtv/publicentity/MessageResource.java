package com.socialtv.publicentity;

/**
 * Created by wlanjie on 14-8-12.
 */
public class MessageResource {
    private String BIND_MOBILE;
    private String REGISTER_MOBILE;
    private String VERIFY_CODE_MESSAGE;
    private String INVITE_SCORE;
    private String message;

    public String getBIND_MOBILE() {
        return BIND_MOBILE;
    }

    public void setBIND_MOBILE(String BIND_MOBILE) {
        this.BIND_MOBILE = BIND_MOBILE;
    }

    public String getREGISTER_MOBILE() {
        return REGISTER_MOBILE;
    }

    public void setREGISTER_MOBILE(String REGISTER_MOBILE) {
        this.REGISTER_MOBILE = REGISTER_MOBILE;
    }

    public String getVERIFY_CODE_MESSAGE() {
        return VERIFY_CODE_MESSAGE;
    }

    public void setVERIFY_CODE_MESSAGE(String VERIFY_CODE_MESSAGE) {
        this.VERIFY_CODE_MESSAGE = VERIFY_CODE_MESSAGE;
    }

    public String getINVITE_SCORE() {
        return INVITE_SCORE;
    }

    public void setINVITE_SCORE(String INVITE_SCORE) {
        this.INVITE_SCORE = INVITE_SCORE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
