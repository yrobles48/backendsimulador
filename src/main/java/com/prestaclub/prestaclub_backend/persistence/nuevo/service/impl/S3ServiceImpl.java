package com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service; 

import com.prestaclub.prestaclub_backend.persistence.nuevo.service.IS3Service;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3ServiceImpl implements IS3Service {
    @Autowired
    private  S3Client s3;
    @Autowired
     private  S3Presigner presigner;

    @Autowired
    private AmazonS3 s3ClientAmazon;

    @Value("${aws_bucket_name}")
    private String bucketName;
    @Autowired
    private S3Client s3Client;

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3ClientAmazon.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public String uploadFile (byte[] fileBytes, String fileName) throws IOException {
        try { 
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3.putObject(putObjectRequest,RequestBody.fromBytes(fileBytes));
            return "Archivo subido correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al subir el archivo a S3";
        }
    }

    public byte[] obtenerContenido(String filePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            return s3Object.readAllBytes(); // Java 11+
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo desde S3", e);
        }
    }

    public String getFileUrl( String filePath) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            // Generar una URL presignada para el archivo
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(60)) // Tiempo de validez de la URL
                    .build();

            URL presignedUrl = presigner.presignGetObject(presignRequest).url();
            return presignedUrl.toString();
            

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al generar la URL del archivo en S3";
        }
    }

}