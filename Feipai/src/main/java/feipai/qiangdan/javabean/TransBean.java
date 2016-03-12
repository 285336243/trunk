package feipai.qiangdan.javabean;

/**
 * 转账bean
 */
public class TransBean {
    /**
     * 流水号
     */
    private String serial_number;
    /**
     * 支付宝真实姓名
     */
    private String userName_zfb;
    /**
     * 支付宝账号
     */
    private String account_zfb;
    /**
     * 转账金额
     */
    private String money;
    /**
     * 状态(未转账，已转账)
     */
    private String state;
    /**
     * 申请时间
     */
    private String publishTime;
    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getUserName_zfb() {
        return userName_zfb;
    }

    public void setUserName_zfb(String userName_zfb) {
        this.userName_zfb = userName_zfb;
    }

    public String getAccount_zfb() {
        return account_zfb;
    }

    public void setAccount_zfb(String account_zfb) {
        this.account_zfb = account_zfb;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
