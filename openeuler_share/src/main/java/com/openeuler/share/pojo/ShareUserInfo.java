package com.openeuler.share.pojo;

public class ShareUserInfo {
    private String userId;
    private String userEmail;
    private String loginName;

    public ShareUserInfo(String userId, String userEmail, String loginName) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.loginName = loginName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
