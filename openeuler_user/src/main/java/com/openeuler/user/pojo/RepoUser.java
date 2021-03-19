package com.openeuler.user.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 实体类
 *
 */
@Entity
@Table(name = "tb_repo_user")
public class RepoUser implements Serializable{
    @Id
    private String id;//ID
    @NotNull
    private String ownerId;//库创建者userId
    @NotNull
    private String repoDir;//库url地址
    @NotNull
    private String user_name;//认证该库的用户名
    @NotNull
    private String password;//认证该库的密码

    public RepoUser(String id, @NotNull String ownerId, @NotNull String repoDir, @NotNull String user_name, @NotNull String password) {
        this.id = id;
        this.ownerId = ownerId;
        this.repoDir = repoDir;
        this.user_name = user_name;
        this.password = password;
    }

    public RepoUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public void setRepoDir(String repoDir) {
        this.repoDir = repoDir;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
