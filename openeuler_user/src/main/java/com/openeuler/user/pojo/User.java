package com.openeuler.user.pojo;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体类
 * @author Administrator
 *
 */
@Entity
@Table(name="tb_user")
public class User implements Serializable{

    @Id
    private String id;//ID
    private String loginname;//登录名
    private String email;//E-Mail
    private String password;//密码
    private String nickname;//昵称
    private java.util.Date regdate;//注册日期
    private java.util.Date updatedate;//修改日期
    private java.util.Date lastdate;//最后登陆日期
//    private String sex;//性别
//    private java.util.Date birthday;//出生年月日
//    private String avatar;//头像
//    private String mobile;//手机号码
//    private Long online;//在线时长（分钟）
//    private String interest;//兴趣
//    private String personality;//个性
//    private Integer fanscount;//粉丝数
//    private Integer followcount;//关注数


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }
}