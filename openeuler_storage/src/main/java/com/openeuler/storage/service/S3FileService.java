package com.openeuler.storage.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.HttpMethod;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.storage.pojo.UrlInfo;
import com.openeuler.storage.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;
import io.jsonwebtoken.Claims;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;

@Service
public class S3FileService extends S3ClientService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private IdWorker idWorker;


    /**
     * Create pre-signed upload url.
     *
     */
    public String[] createUploadUrl(FileInfo fileInfo, String repo, String userId) {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        // Generate the pre-signed URL.
        System.out.println("Generating pre-signed URL.");
        String filename = fileInfo.getArtifactId() + "-" + fileInfo.getVersion()+".";
        String objectKey = userId+"/"+repo+"/"+fileInfo.getGroupId().replace(".", "/") + "/" + fileInfo.getArtifactId() + "/" + fileInfo.getVersion() + "/" + filename;

        String objectKeyJAR = objectKey + fileInfo.getPackaging();
        GeneratePresignedUrlRequest generatePresignedUrlRequestJAR = new GeneratePresignedUrlRequest(getBucketName(), objectKeyJAR)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        String uploadJARUrl = getClient().generatePresignedUrl(generatePresignedUrlRequestJAR).toString();

        String objectKeyPOM = objectKey + "pom";
        GeneratePresignedUrlRequest generatePresignedUrlRequestPOM = new GeneratePresignedUrlRequest(getBucketName(), objectKeyPOM)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        String uploadPOMUrl = getClient().generatePresignedUrl(generatePresignedUrlRequestPOM).toString();

        return new String[] {uploadJARUrl,uploadPOMUrl};
    }


    /**
     * 保存上传文件的信息
     *
     */
    public void saveInfo(FileInfo fileInfo, String repo, String userId) {

        fileInfo.setId(idWorker.nextId() + "");
        fileInfo.setUserId(userId);
        fileInfo.setUpdateDate(new Date());
        fileInfo.setRepo(repo);
        String filename = fileInfo.getArtifactId() + "-" + fileInfo.getVersion()+".";
        String objectKeyPOM = userId+"/"+repo+"/"+fileInfo.getGroupId().replace(".", "/") + "/" + fileInfo.getArtifactId() + "/" + fileInfo.getVersion() + "/" + filename;
        fileInfo.setJarUrl(getUrl().concat(objectKeyPOM+fileInfo.getPackaging()));
        fileInfo.setPomUrl(getUrl().concat(objectKeyPOM+"pom"));
        fileDao.save(fileInfo);
    }


    /**
     * 列举repo中的包信息
     * 带分页，从第 page 页开始的 size 个
     *
     */
    public Page<FileDao.ArtifactVersionList> getList(String repo, int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page-1, size);
        return this.fileDao.findVersionsGroupByArtifactAndGroupId(userId, repo, pageable);
    }


    /**
     * 根据用户列举repo中的包信息
     * 带分页，从第 page 页开始的 size 个
     *
     */
    public List<FileDao.ArtifactVersionList> getListById(String userId) {
        return this.fileDao.findVersionsGroupByArtifactAndGroupId(userId);
    }


    /**
     * 根据关键词搜索
     * 带分页，从第 page 页开始的 size 个
     *
     */
    public Page<FileDao.ArtifactVersionList> searchList(String repo, int page, int size, String keywords, String userId) {
        System.out.println(userId);
        keywords = "%"+keywords+"%";
        Pageable pageable = PageRequest.of(page-1, size);
        return this.fileDao.searchVersionsGroupByArtifactAndGroupId(userId, repo, keywords, pageable);
        //return this.fileDao.searchVersionsGroupByArtifactAndGroupId("1353720995084636160", repo, keywords,page * size, size);
    }


    /**
     * 根据用户和关键词搜索
     * 带分页，从第 page 页开始的 size 个
     *
     */
    public List<FileDao.ArtifactVersionList> searchListById(String userId, String keywords) {
        keywords = "%"+keywords+"%";
        return this.fileDao.searchVersionsGroupByArtifactAndGroupId(userId, keywords);
    }


    /**
     * 获得选中文件的下载地址
     *
     */
    public List<UrlInfo> getUrlList(String repo, String groupId, String artifactId, String version, String userId) {
        List<FileInfo> curInfo = fileDao.findByUserIdAndRepoAndGroupIdAndArtifactIdAndVersion(userId, repo, groupId, artifactId, version);
        if(curInfo.size()!=1){
            throw new RuntimeException("无法定位文件");
        }
        List<UrlInfo> res = new ArrayList<>();
        String filename = artifactId + "-" + version+".";
        res.add(new UrlInfo(filename+ curInfo.get(0).getPackaging(), curInfo.get(0).getJarUrl(), curInfo.get(0).getUpdateDate()));
        res.add(new UrlInfo(filename+"pom", curInfo.get(0).getPomUrl(), curInfo.get(0).getUpdateDate()));
        return res;
    }


    /**
     * 获得选中的被分享文件的下载地址
     *
     */
    public List<UrlInfo> getShareUrlList(String groupId, String artifactId, String version, String userId) {
        List<FileInfo> curInfo = fileDao.findByUserIdAndRepoAndGroupIdAndArtifactIdAndVersion(userId, "release", groupId, artifactId, version);
        if(curInfo.size()!=1){
            throw new RuntimeException("无法定位文件");
        }
        List<UrlInfo> res = new ArrayList<>();
        String filename = artifactId + "-" + version+".";
        res.add(new UrlInfo(filename+ curInfo.get(0).getPackaging(), curInfo.get(0).getJarUrl(), curInfo.get(0).getUpdateDate()));
        res.add(new UrlInfo(filename+"pom", curInfo.get(0).getPomUrl(), curInfo.get(0).getUpdateDate()));
        return res;
    }


    /**
     * 删除整包文件
     *
     */
    public void removeFileGroup(String repo, String groupId, String artifactId, String userId){
        List<FileInfo> filesInfo = fileDao.findByUserIdAndRepoAndGroupIdAndArtifactId(userId, repo, groupId, artifactId);
        if (filesInfo == null || filesInfo.isEmpty()) {
            throw new RuntimeException("无法定位文件包");
        }
        String baseUrl = repo+"/"+groupId.replace(".","/")+"/"+artifactId+"/";
        for(FileInfo info : filesInfo){
            String fileName = baseUrl+info.getVersion()+"/"+artifactId+"-"+info.getVersion()+ ".";
            getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName+"pom"));
            getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName+info.getPackaging()));
            fileDao.delete(info);
        }
    }


    /**
     * 删除某版本文件
     *
     */
    public void removeFile(String repo, String groupId, String artifactId, String version, String userId) {
        List<FileInfo> curInfo = fileDao.findByUserIdAndRepoAndGroupIdAndArtifactIdAndVersion(userId, repo, groupId, artifactId, version);
        if(curInfo.size()!=1){
            throw new RuntimeException("无法定位文件");
        }

        String fileName = artifactId+ "-" +version+ ".";
        String url = repo+"/"+groupId.replace(".","/")+"/"+artifactId+"/"+version+"/"+fileName;

        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), url+"pom"));
        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), url+curInfo.get(0).getPackaging()));
        fileDao.delete(curInfo.get(0));
    }

}
