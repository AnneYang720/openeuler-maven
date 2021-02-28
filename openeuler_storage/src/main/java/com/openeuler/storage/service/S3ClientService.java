package com.openeuler.storage.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class S3ClientService {

    // AmazonS3 Client, in this object you have all AWS API calls about S3.
    private AmazonS3 amazonS3;

    // Your bucket URL, this URL is https://{bucket-name}.s3-{region}.amazonaws.com/
    // If you don't know if your URL is ok, send one file to your bucket using AWS and
    // click on them, the file URL contains your bucket URL.
    @Value("${amazon.s3.url}")
    private String url;

    @Value("${amazon.s3.endpoint}")
    private String endpoint;

    @Value("${amazon.s3.region}")
    private String region;

    // Your bucket name.
    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    // The IAM access key.
    @Value("${amazon.s3.access-key}")
    private String accessKey;

    // The IAM secret key.
    @Value("${amazon.s3.secret-key}")
    private String secretKey;

    // Getters for parents.
    protected AmazonS3 getClient() {
        return amazonS3;
    }

    protected String getUrl() {
        return url;
    }

    protected String getBucketName() {
        return bucketName;
    }

    // This method are called after Spring starts AmazonClientService into your container.
    @PostConstruct
    private void init() {

        // Init your AmazonS3 credentials using BasicAWSCredentials.
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // Start the com.openeuler.share.client using AmazonS3ClientBuilder
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
    }

}