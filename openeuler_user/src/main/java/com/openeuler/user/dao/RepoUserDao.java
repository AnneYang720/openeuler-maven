package com.openeuler.user.dao;

import com.openeuler.user.pojo.RepoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 * @author AnneY
 *
 */

public interface RepoUserDao extends JpaRepository<RepoUser,String>, JpaSpecificationExecutor<RepoUser> {

    RepoUser findByOwnerIdAndRepoAndUserNameAndPassword(String ownerId, String repo, String userName, String password);

}
