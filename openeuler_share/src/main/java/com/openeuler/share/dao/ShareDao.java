package com.openeuler.share.dao;

import com.openeuler.share.pojo.ShareInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ShareDao extends JpaRepository<ShareInfo, String>, JpaSpecificationExecutor<ShareInfo> {

    @Query(value = "SELECT group_concat(version) AS versionList, count(version) as versionNum, " +
            "group_concat(id) AS idList, max(update_date) AS updateTime, " +
            "user_id AS userId, repo, group_id AS groupId, artifact_id AS artifactId, " +
            "substring_index(group_concat(version), ',', -1) AS latestVersion, user_name AS uploadUser " +
            "FROM tb_fileinfo WHERE user_id = :userId AND repo = 'release' " +
            "GROUP BY group_id, artifact_id, user_id, repo" +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<ShareArtifactVersionList> findShareVersionsGroupByArtifactAndGroupId(@Param("userId") String userId, int offset, int size);


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
