package com.openeuler.storage.controller;

import com.openeuler.storage.pojo.FileInfo;
import entity.Result;
import entity.StatusCode;
import com.openeuler.storage.service.S3FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/storage")
public class S3FileController {
    @Autowired
    private S3FileService s3FileService;

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
                      @RequestParam FileInfo fileInfo){
        String fileUrl = s3FileService.uploadFileToAmazon(file, fileInfo);
        return new Result(true, StatusCode.OK, "文件上传成功", fileUrl);
    }

    /**
     * 删除
     * @param id
     */
    @RequestMapping (value="/{id}", method=RequestMethod.DELETE)
    public Result delete(@PathVariable String id ){
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
