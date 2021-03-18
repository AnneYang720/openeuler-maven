package com.openeuler.share.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tb_shareinfo")
public class ShareInfo implements Serializable {
    @Id
    private String id;

    private String userId;

    private String sharedUserId;

    private String repoUserId;

    public ShareInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSharedUserId() {
        return sharedUserId;
    }

    public void setSharedUserId(String sharedUserId) {
        this.sharedUserId = sharedUserId;
    }

    public String getRepoUserId() {
        return repoUserId;
    }

    public void setRepoUserId(String repoUserId) {
        this.repoUserId = repoUserId;
    }
}
