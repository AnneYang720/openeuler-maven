package com.openeuler.share.dao;

import com.openeuler.share.pojo.ShareInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ShareDao extends JpaRepository<ShareInfo, String>, JpaSpecificationExecutor<ShareInfo> {

    List<ShareInfo> findByUserIdAndSharedUserId(String userId,String shareUserId);

    List<ShareInfo> findByUserId(String userId, Pageable pageable);

    List<ShareInfo> findBySharedUserId(String userId, Pageable pageable);


    public static interface ShareArtifactVersionList {
        String getGroupId();
        String getArtifactId();
        Date getUpdateTime();
        int getVersionNum();
        String getLatestVersion();
        String getUploadUser();

        @Value("#{target.versionList.split(\",\")}")
        List<String> getVersionList();

        @Value("#{target.idList.split(\",\")}")
        List<String> getIdList();
    }
}
