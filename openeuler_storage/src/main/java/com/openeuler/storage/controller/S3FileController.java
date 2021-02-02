package com.openeuler.storage.controller;

import com.openeuler.storage.pojo.FileInfo;
import entity.Result;
import entity.StatusCode;
import com.openeuler.storage.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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
     * 创建直接上传的URL
     *
     * @param fileInfo
     */
    @PostMapping(value = "/save/{repo}")
    public Result saveInfo(@PathVariable String repo, @RequestBody FileInfo fileInfo) {
        s3FileService.saveInfo(fileInfo, repo);
        return new Result(true, StatusCode.OK, "文件信息保存成功");
    }

    /**
     * 增加
     * @param file
     * @param pomFile
     * @param fileInfo
     */
    @PostMapping("/uploadboth")
    public Result add(@RequestParam MultipartFile file, @RequestParam MultipartFile pomFile,
                      @RequestParam FileInfo fileInfo){

        String fileUrl = s3FileService.uploadFileToAmazon(file, fileInfo);
        fileInfo.setPackaging("pom");
        String pomFileUrl = s3FileService.uploadFileToAmazon(pomFile, fileInfo);
        return new Result(true, StatusCode.OK, "文件上传成功", fileUrl);
    }

    /**
     * 增加
     * @param file
     * @param fileInfo
     */
    @PostMapping("/upload")
    public Result add(@RequestParam MultipartFile file,
                      @RequestParam FileInfo fileInfo) {
        String fileUrl = s3FileService.uploadFileToAmazon(file, fileInfo);
        return new Result(true, StatusCode.OK, "文件上传成功", fileUrl);
    }

//    /**
//     * 通过URL直接上传
//     *
//     * @param fileInfo
//     */
//    @PostMapping("/directupload")
//    public Result directAdd(@RequestBody FileInfo fileInfo) {
//        String uploadUrl = s3FileService.createUploadUrl(fileInfo);
//        return new Result(true, StatusCode.OK, "预签名url创建成功", uploadUrl);
//    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        s3FileService.removeFile(id);
        return new Result(true, StatusCode.OK, "文件删除成功");
    }

    /**
     * 删除
     * @param fileInfo
     */
    @PostMapping (value="/delete")
    public Result delete(@RequestBody FileInfo fileInfo){
        s3FileService.removeFile(fileInfo);
        return new Result(true, StatusCode.OK, "文件删除成功");
    }
}
