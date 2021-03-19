package com.openeuler.share.pojo;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

public class SharedFileInfo {
    String groupId;
    String artifactId;
    Date updateTime;
    int versionNum;
    String latestVersion;
    String uploadUser;
    String userId;
    List<String> versionList;
    List<String> idList;
    String repoUserName;
    String repoPassword;

    public SharedFileInfo() {
    }

    public SharedFileInfo(String groupId, String artifactId, Date updateTime, int versionNum, String latestVersion, String uploadUser, String userId, List<String> versionList, List<String> idList, String repoUserName, String repoPassword) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.updateTime = updateTime;
        this.versionNum = versionNum;
        this.latestVersion = latestVersion;
        this.uploadUser = uploadUser;
        this.userId = userId;
        this.versionList = versionList;
        this.idList = idList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public List<String> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<String> versionList) {
        this.versionList = versionList;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRepoUserName() {
        return repoUserName;
    }

    public void setRepoUserName(String repoUserName) {
        this.repoUserName = repoUserName;
    }

    public String getRepoPassword() {
        return repoPassword;
    }

    public void setRepoPassword(String repoPassword) {
        this.repoPassword = repoPassword;
    }
}
