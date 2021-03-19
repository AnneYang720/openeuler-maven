package com.openeuler.user.pojo;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 * @author Administrator
 *
 */
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Id
    private String id;//ID
    @NotNull
    private String loginName;//登录名
    @NotNull
    private String email;//E-Mail
    @NotNull
    private String password;//密码
    private java.util.Date regDate;//注册日期
    private java.util.Date updateDate;//修改日期
    private java.util.Date lastDate;//最后登陆日期
    private String repoUserReleaseId;
    private String repoUserSnapshotId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginname) {
        this.loginName = loginname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastdate) {
        this.lastDate = lastdate;
    }

    public String getRepoUserReleaseId() {
        return repoUserReleaseId;
    }

    public void setRepoUserReleaseId(String repoUserReleaseId) {
        this.repoUserReleaseId = repoUserReleaseId;
    }

    public String getRepoUserSnapshotId() {
        return repoUserSnapshotId;
    }

    public void setRepoUserSnapshotId(String repoUserSnapshotId) {
        this.repoUserSnapshotId = repoUserSnapshotId;
    }
}