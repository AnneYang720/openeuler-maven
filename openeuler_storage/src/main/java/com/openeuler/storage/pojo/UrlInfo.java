package com.openeuler.storage.pojo;


import java.util.Date;

public class UrlInfo {
    private String filename;
    private String downloadUrl;
    private java.util.Date updateDate;

    public UrlInfo() {
    }

    public UrlInfo(String filename, String downloadUrl, Date updateDate) {
        this.filename = filename;
        this.downloadUrl = downloadUrl;
        this.updateDate = updateDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
