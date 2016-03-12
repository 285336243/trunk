package feipai.qiangdan.other;


/**
 * 设置bean
 */
public class SetBean {
    private String text;
    private int resid;

    public SetBean(String text) {
        this.text = text;
    }

    public SetBean(String text, int resid) {
        this.text = text;
        this.resid = resid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public int getResid() {
        return resid;
    }

    public void setResid(int resid) {
        this.resid = resid;
    }

}
