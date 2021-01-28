package com.openeuler.storage.dao;

import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.user.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 数据访问接口
 *
 * @author Administrator
 */

public interface FileDao extends JpaRepository<FileInfo, String>, JpaSpecificationExecutor<FileInfo> {
    /**
     * 查询是否存在此条数据
     *
     * @param userId
     * @param id
     * @return
     */
    public FileInfo findByUseridAndId(String userId, String id);

    List<FileInfo> findByUserid(String userId);

}