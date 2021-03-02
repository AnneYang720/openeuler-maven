package com.openeuler.storage.controller;

import com.openeuler.storage.dao.FileDao;
import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.storage.pojo.UrlInfo;
import com.openeuler.user.pojo.User;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import com.openeuler.storage.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/maven")
public class S3FileController {
    @Autowired
    private S3FileService s3FileService;

    /**
     * 创建直接上传的URL
     *
     * @param fileInfo
     */
    @PostMapping(value = "/{repo}")
    public Result createURL(@PathVariable String repo, @RequestBody FileInfo fileInfo) {
        String[] result = s3FileService.createUploadUrl(fileInfo, repo);
        Map<String, String> map = new HashMap<>();
        map.put("uploadJARUrl", result[0]);
        map.put("uploadPOMUrl", result[1]);
        return new Result(true, StatusCode.OK, "预签名url创建成功", map);
    }

    /**
     * 列举repo中的包信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param repo
     * @param page
     * @param size
     */
    @GetMapping("/{repo}/getlist/{page}/{size}")
    public Result getListPage(@PathVariable String repo, @PathVariable int page, @PathVariable int size) {
        List<FileDao.ArtifactVersionList> data = s3FileService.getList(repo, page, size);
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 根据用户列举repo中的包信息
     * 带分页，从第 page 页开始的 size 个
     *
     * @param userId
     */
    @RequestMapping(value="/getlist/byid", method = RequestMethod.POST)
    public List<FileDao.ArtifactVersionList> getListById(@RequestBody String userId) {
        System.out.println("getListById: "+userId);
        return s3FileService.getListById(userId);
    }

    /**
     * 根据关键词搜索
     * 带分页，从第 page 页开始的 size 个
     *
     * @param repo
     * @param page
     * @param size
     */
    @PostMapping("/{repo}/search/{page}/{size}")
    public Result searchList(@PathVariable String repo, @PathVariable int page, @PathVariable int size, @RequestBody String keywords) {
        List<FileDao.ArtifactVersionList> data = s3FileService.searchList(repo, page, size, keywords);
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 获得选中文件的下载地址
     *
     * @param repo
     * @param groupId
     * @param artifactId
     * @param chosenVersion
     */
    @GetMapping("/{repo}/geturl/{groupId}/{artifactId}/{chosenVersion}")
    public Result getUrl(@PathVariable String repo, @PathVariable String groupId, @PathVariable String artifactId,  @PathVariable String chosenVersion) {
        System.out.println(repo + groupId + artifactId + chosenVersion);
        List<UrlInfo> data = s3FileService.getUrlList(repo, groupId, artifactId, chosenVersion);
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 获得选中被分享文件的下载地址
     *
     * @param userId
     * @param groupId
     * @param artifactId
     * @param chosenVersion
     */
    @GetMapping("/getshareurl/{groupId}/{artifactId}/{chosenVersion}/{userId}")
    public Result getShareUrl(@PathVariable String groupId, @PathVariable String artifactId,  @PathVariable String chosenVersion, @PathVariable String userId) {
        List<UrlInfo> data = s3FileService.getShareUrlList(groupId, artifactId, chosenVersion, userId);
        return new Result(true, StatusCode.OK, "列举成功", data);
    }

    /**
     * 保存文件信息
     *
     * @param fileInfo
     */
    @PostMapping(value = "/save/{repo}")
    public Result saveInfo(@PathVariable String repo, @RequestBody FileInfo fileInfo) {
        s3FileService.saveInfo(fileInfo, repo);
        return new Result(true, StatusCode.OK, "文件信息保存成功");
    }

    /**
     * 删除整包文件
     *
     * @param repo
     * @param groupId
     * @param artifactId
     */
    @RequestMapping(value = "/{repo}/{groupId}/{artifactId}", method = RequestMethod.DELETE)
    public Result deleteByGroup(@PathVariable String repo, @PathVariable String groupId, @PathVariable String artifactId) {
        s3FileService.removeFileGroup(repo,groupId,artifactId);
        return new Result(true, StatusCode.OK, "文件删除成功");
    }

    /**
     * 删除某版本文件
     *
     * @param repo
     * @param groupId
     * @param artifactId
     */
    @RequestMapping(value = "/{repo}/{groupId}/{artifactId}/{chosenVersion}", method = RequestMethod.DELETE)
    public Result deleteVersion(@PathVariable String repo, @PathVariable String groupId, @PathVariable String artifactId,  @PathVariable String chosenVersion) {
        s3FileService.removeFile(repo,groupId,artifactId,chosenVersion);
        return new Result(true, StatusCode.OK, "文件删除成功");
    }



}
