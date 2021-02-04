package com.openeuler.storage.pojo;


public class UrlInfo {
    private String filename;
    private String downloadUrl;

    public UrlInfo() {
    }

    public UrlInfo(String filename, String downloadUrl) {
        this.filename = filename;
        this.downloadUrl = downloadUrl;
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
}
