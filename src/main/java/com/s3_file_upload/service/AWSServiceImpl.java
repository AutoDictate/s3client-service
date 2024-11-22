package com.s3_file_upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public void uploadFile(Long userId, String fileType, MultipartFile file) {
        String key = generateKey(userId, fileType, file.getOriginalFilename());
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            System.out.println("File uploaded successfully to S3: " + key);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to upload file to S3", ex);
        }
    }

    @Override
    public void downloadFile(Long userId, String fileType, String fileName, String localPath) {
        String key = generateKey(userId, fileType, fileName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            Path path = Paths.get(localPath);
            Files.write(path, s3Client.getObjectAsBytes(getObjectRequest).asByteArray());
            System.out.println("File downloaded successfully to: " + localPath);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to download file from S3", ex);
        }
    }

    @Override
    public void deleteFile(Long userId, String fileType, String fileName) {
        String key = generateKey(userId, fileType, fileName);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            System.out.println("File deleted successfully from S3: " + key);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete file from S3", ex);
        }
    }

    @Override
    public List<S3Object> listFiles(Long userId) {
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(userId + "/") // List all files in the user's folder.
                    .build();
            ListObjectsV2Response result = s3Client.listObjectsV2(listObjectsRequest);
            return result.contents();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to list files in S3", ex);
        }
    }

    private String generateKey(Long userId, String fileType, String fileName) {
        return userId + "/" + fileType + "/" + fileName;
    }
}
