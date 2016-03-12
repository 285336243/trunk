package feipai.qiangdan.javabean;

/**
 * 帐号bean
 */
public class AccountBean {
    /**
     * 账号余额
     */
    private String balance;
    /**
     * 可以金额
     */
    private String use_money;
    /**
     * 是否有支付宝账号(1:有；0：无)
     */
    private int hasAccount_zfb;
    /**
     * 支付宝账号(没有则为“”)
     */
    private String account_zfb;
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUse_money() {
        return use_money;
    }

    public void setUse_money(String use_money) {
        this.use_money = use_money;
    }

    public int getHasAccount_zfb() {
        return hasAccount_zfb;
    }

    public void setHasAccount_zfb(int hasAccount_zfb) {
        this.hasAccount_zfb = hasAccount_zfb;
    }

    public String getAccount_zfb() {
        return account_zfb;
    }

    public void setAccount_zfb(String account_zfb) {
        this.account_zfb = account_zfb;
    }
}
