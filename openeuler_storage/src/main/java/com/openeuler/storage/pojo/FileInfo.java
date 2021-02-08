package com.openeuler.storage.pojo;

import com.openeuler.user.pojo.User;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tb_fileinfo")
//@Document(indexName = "openeuler", type = "fileInfo")
@IdClass(FileInfo.class)//指定联合主键
public class FileInfo implements Serializable {

    @Id
    private String id;
    @Id
    private String userId;

    @NotNull
    private String jarUrl;
    @NotNull
    private String pomUrl;

    private String repo;

//    @Field
    private String groupId;
//    @Field
    private String artifactId;
    private String version;

    private String packaging;
    private java.util.Date updateDate;

    public FileInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJarUrl() {
        return jarUrl;
    }

    public void setJarUrl(String jarUrl) {
        this.jarUrl = jarUrl;
    }

    public String getPomUrl() {
        return pomUrl;
    }

    public void setPomUrl(String pomUrl) {
        this.pomUrl = pomUrl;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
