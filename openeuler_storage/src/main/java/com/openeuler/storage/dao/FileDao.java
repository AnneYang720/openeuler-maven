package com.openeuler.storage.dao;

import com.openeuler.storage.pojo.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 * @author Administrator
 *
 */

public interface FileDao extends JpaRepository<FileInfo,String>, JpaSpecificationExecutor<FileInfo> {
}