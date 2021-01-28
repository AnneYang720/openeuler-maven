package com.openeuler.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.openeuler.user.pojo.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface UserDao extends JpaRepository<User,String>,JpaSpecificationExecutor<User>{
    /**
     * 根据用户名查询用户
     *
     * @param loginname
     * @return
     */
    User findByLoginname(String loginname);
    User findByEmail(String mobile);

    /**
     * 查询数据库是否已经有相应的用户名或手机号
     *
     * @param loginname 用户名
     * @param email    邮箱
     * @return
     */
    List<User> findByLoginnameOrEmail(String loginname, String email);

}