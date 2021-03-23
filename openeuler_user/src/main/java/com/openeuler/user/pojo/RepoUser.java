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
    private String repo;//库的repo
    @NotNull
    private String repoDir;//库url地址
    @NotNull
    private String userName;//认证该库的用户名
    @NotNull
    private String password;//认证该库的密码


    public RepoUser(String id, @NotNull String ownerId, @NotNull String repo, @NotNull String repoDir, @NotNull String userName, @NotNull String password) {
        this.id = id;
        this.ownerId = ownerId;
        this.repo = repo;
        this.repoDir = repoDir;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }
}
