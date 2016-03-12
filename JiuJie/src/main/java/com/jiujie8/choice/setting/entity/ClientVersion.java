package com.jiujie8.choice.setting.entity;

/**
 *
 */
public class ClientVersion {

    /**
     * 更新链接
     */
    private String updateUrl;
    /**
     * 更新内容
     */
    private String updateNote;
    /**
     * 客户端类型
     */
    private String platform;
    /**
     * 客户端版本
     */
    private String version;
    /**
     * 服务器端版本号
     */
    private int versionCode;

    /**
     * 序号id
     */
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUpdateNote() {
        return updateNote;
    }

    public void setUpdateNote(String updateNote) {
        this.updateNote = updateNote;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

}
