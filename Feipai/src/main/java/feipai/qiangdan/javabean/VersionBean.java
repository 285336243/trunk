package feipai.qiangdan.javabean;

/**
 * 版本信息
 */
public class VersionBean {
    /**
     * 版本号描述
     */
    private String version;
    /**
     *  链接地址
     */
    private String link;
    /**
     *
     * 版本信息
     */
    private String info;

    /**
     * 版本号
     */
    private int versionNum;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

}
