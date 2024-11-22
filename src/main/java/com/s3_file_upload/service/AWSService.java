package com.s3_file_upload.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public interface AWSService {

    void uploadFile(Long userId, String fileType, MultipartFile file);

    void downloadFile(Long userId, String fileType, String fileName, String localPath);

    void deleteFile(Long userId, String fileType, String fileName);

    List<S3Object> listFiles(Long userId);

}
