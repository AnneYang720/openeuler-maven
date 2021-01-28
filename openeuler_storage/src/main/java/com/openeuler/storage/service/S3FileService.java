package com.openeuler.storage.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.HttpMethod;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.storage.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.net.URL;

@Service
public class S3FileService extends S3ClientService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private HttpServletRequest request;


    // Upload image to AWS S3.
    // TODO: Verify uploaded file extension
    // TODO: Connect file with user
    public String uploadFileToAmazon(MultipartFile multipartFile, FileInfo fileInfo) {
        //验证是否登录，如果登录就获取当前登录用户的ID
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再上传文件");
        }

        fileInfo.setId(idWorker.nextId() + "");
        fileInfo.setUserId(claims.getId());

        String filename = uploadMultipartFile(multipartFile, fileInfo);
        fileInfo.setFilename(filename);
        fileInfo.setUrl(getUrl().concat(filename));
        fileDao.save(fileInfo);

        // Save image information and return them.
        //AmazonImage amazonImage = new AmazonImage();
        //amazonImage.setUrl(url);
        return fileInfo.getUrl();

//        // Valid extensions array, like jpeg/jpg and png.
//        List<String> validExtensions = Arrays.asList("jpeg", "jpg", "png");
//
//        // Get extension of MultipartFile
//        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
//        if (!validExtensions.contains(extension)) {
//            // If file have a invalid extension, call an Exception.
//            log.warn(MessageUtil.getMessage("invalid.image.extesion"));
//            throw new InvalidImageExtensionException(validExtensions);
//        } else {
//            // Upload file to Amazon.
//            return amazonImageDao.insert(amazonImage);
//        }

    }

    // Create pre-signed upload url.
    public String createUploadUrl(FileInfo fileInfo) {
        //验证是否登录，如果登录就获取当前登录用户的ID
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再上传文件");
        }

        fileInfo.setId(idWorker.nextId() + "");
        fileInfo.setUserId(claims.getId());

        // Set the pre-signed URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        // Generate the pre-signed URL.
        System.out.println("Generating pre-signed URL.");
        String filename = fileInfo.getArtifactId() + "-" + fileInfo.getVersion() + "." + fileInfo.getPackaging();
        String objectKey = fileInfo.getGroupId().replace(".", "/") + "/" + fileInfo.getArtifactId() + "/" + fileInfo.getVersion() + "/" + filename;
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(getBucketName(), objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        String uploadUrl = getClient().generatePresignedUrl(generatePresignedUrlRequest).toString();
        fileInfo.setFilename(objectKey);
        fileInfo.setUrl(getUrl().concat(filename));
        fileDao.save(fileInfo);
        return uploadUrl;
    }

    public void removeFile(FileInfo fileInfo) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再删除文件");
        }
        if (!claims.getId().equals(fileInfo.getUserId())) {
            throw new RuntimeException("不能删除其他用户的文件");
        }
        String fileName = fileInfo.getFilename();
        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        fileDao.delete(fileInfo);
    }

    public void removeFile(String id) {
        Claims claims = (Claims) request.getAttribute("claims_user");
        if (claims == null) {//说明当前用户没有user角色
            throw new RuntimeException("请登陆后再删除文件");
        }
        Optional<FileInfo> fileInfo = fileDao.findById(id);
        if (!fileInfo.isPresent()) return;
        if (!claims.getId().equals(fileInfo.get().getUserId())) {
            throw new RuntimeException("不能删除其他用户的文件");
        }
        String fileName = fileInfo.get().getFilename();
        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        fileDao.deleteById(id);
    }

    // Make upload to Amazon
    private String uploadMultipartFile(MultipartFile multipartFile, FileInfo fileInfo) {
        String fileUrl = "";
        String uploadUrl = "";

        try {
            // Get the file from MultipartFile.
            File file = FileUtils.convertMultipartToFile(multipartFile);

            // Extract the file name.
            //String fileName = FileUtils.generateFileName(multipartFile);
            String fileName = fileInfo.getArtifactId()+ "-" +fileInfo.getVersion()+ "." +fileInfo.getPackaging();
            uploadUrl = fileInfo.getGroupId().replace(".","/")+"/"+fileInfo.getArtifactId()+"/"+fileInfo.getVersion()+"/"+fileName;
            //Objects.requireNonNull(multipartFile.getOriginalFilename()).replace(" ", "_")

            // Upload file.
            uploadPublicFile(uploadUrl, file);

            // Delete the file and get the File Url.
            file.delete();
//            fileUrl = getUrl().concat(uploadUrl);
        } catch (IOException e) {

//            // If IOException on conversion or any file manipulation, call exception.
//            log.warn(MessageUtil.getMessage("multipart.to.file.convert.except"), e);
//            throw new FileConversionException();
        }

        return uploadUrl;
    }

    // Send image to AmazonS3, if have any problems here, the image fragments are removed from amazon.
    // Font: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3Client.html#putObject%28com.amazonaws.services.s3.model.PutObjectRequest%29
    private void uploadPublicFile(String fileName, File file) {
        getClient().putObject(new PutObjectRequest(getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
