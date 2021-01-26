package com.openeuler.storage.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.openeuler.storage.dao.FileDao;
import com.openeuler.storage.pojo.FileInfo;
import com.openeuler.storage.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class S3FileService extends S3ClientService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private IdWorker idWorker;


    // Upload image to AWS S3.
    // TODO: Verify uploaded file extension
    // TODO: Connect file with user
    public String uploadFileToAmazon(MultipartFile multipartFile, FileInfo fileInfo) {
        fileInfo.setId(idWorker.nextId() + "");

        String url = uploadMultipartFile(multipartFile, fileInfo);
        fileInfo.setFilename(url);
        fileInfo.setUrl(getUrl().concat(url));
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

    public void removeFile(FileInfo fileInfo) {
        String fileName = fileInfo.getFilename();
        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        fileDao.delete(fileInfo);
    }

    public void removeFile(String id) {
        Optional<FileInfo> fileInfo = fileDao.findById(id);
        if(!fileInfo.isPresent()) return;
        String fileName = fileInfo.get().getFilename();
        getClient().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        fileDao.deleteById(id);
    }

    // Make upload to Amazon
    // TODO: add maven url
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
