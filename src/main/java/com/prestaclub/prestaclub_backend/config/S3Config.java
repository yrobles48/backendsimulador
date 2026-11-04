package com.prestaclub.prestaclub_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner; 

@Configuration
public class S3Config {

    @Value("${aws_access_key_id}")
    private String awsAccessKeyId;

    @Value("${aws_secret_access_key}")
    private String awsSecretAccessKey;

   @Bean
    public S3Client s3Client() {
        Region region = Region.US_EAST_1;
        AwsCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        return s3Client;
    }

    @Bean
    public S3Presigner s3Presigner() {
        Region region = Region.US_EAST_1;
        AwsCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public AmazonS3 getS3Client() {

        Region region = Region.US_EAST_1;
        String region2 = Region.US_EAST_1.id();
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region2))
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }
}
