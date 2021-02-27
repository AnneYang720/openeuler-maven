package com.openeuler.storage.dao;

import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.user.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collections;
import java.util.Date;
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
    public FileInfo findByUserIdAndId(String userId, String id);

    List<FileInfo> findByUserIdAndRepoAndGroupIdAndArtifactIdAndVersion(
            String userId,String repo,String groupId,String artifactId,String version);

    List<FileInfo> findByUserIdAndRepoAndGroupIdAndArtifactId(
            String userId,String repo,String groupId,String artifactId);



    List<FileInfo> findByUserId(String userId);

    @Query(value = "SELECT group_concat(version) AS versionList, count(version) as versionNum, " +
            "group_concat(id) AS idList, max(update_date) AS updateTime, " +
            "user_id AS userId, repo, group_id AS groupId, artifact_id AS artifactId, " +
            "substring_index(group_concat(version), ',', -1) AS latestVersion " +
            "FROM tb_fileinfo WHERE user_id = :userId AND repo = :repo " +
            "GROUP BY group_id, artifact_id, user_id, repo" +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<ArtifactVersionList> findVersionsGroupByArtifactAndGroupId(@Param("userId") String userId,
                                                                    @Param("repo") String repo, int offset, int size);


    @Query(value = "SELECT group_concat(version) AS versionList, count(version) as versionNum, " +
            "group_concat(id) AS idList, max(update_date) AS updateTime, " +
            "user_id AS userId, repo, group_id AS groupId, artifact_id AS artifactId, " +
            "substring_index(group_concat(version), ',', -1) AS latestVersion" +
            "FROM tb_fileinfo WHERE user_id = :userId AND repo = :repo " +
            "AND group_id LIKE :keywords OR artifact_id LIKE :keywords " +
            "GROUP BY group_id, artifact_id, user_id, repo " +
            "LIMIT :size OFFSET :offset", nativeQuery = true)
    List<ArtifactVersionList> searchVersionsGroupByArtifactAndGroupId(@Param("userId") String userId,
                   @Param("repo") String repo, @Param("keywords") String keywords, int offset, int size);


    public static interface ArtifactVersionList {
        String getGroupId();
        String getArtifactId();
        Date getUpdateTime();
        int getVersionNum();
        String getLatestVersion();

        @Value("#{target.versionList.split(\",\")}")
        List<String> getVersionList();

        @Value("#{target.idList.split(\",\")}")
        List<String> getIdList();
    }


}