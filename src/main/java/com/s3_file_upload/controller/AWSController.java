package com.s3_file_upload.controller;

import com.s3_file_upload.service.AWSService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AWSController {

    private final AWSService service;

    @GetMapping("/{userId}")
    public ResponseEntity<?> listFiles(
            @PathVariable(name = "userId") Long userID
    ) {
        val body = service.listFiles(userID);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/upload")
    @SneakyThrows(IOException.class)
    public ResponseEntity<?> uploadFile(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "fileType") String fileType,
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        service.uploadFile(userId, fileType, file);
        return ResponseEntity.ok().body("File uploaded successfully");
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<?> downloadFile(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "fileType") String fileType,
            @RequestParam(name = "file") String fileName,
            @RequestParam(name = "localPath") String localPath
    ) {
        service.downloadFile(userId, fileType, fileName, localPath);
        return ResponseEntity.ok()
                .body("Done");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "fileType") String fileType,
            @RequestParam("file") String fileName
    ) {
        service.deleteFile(userId, fileType, fileName);
        return ResponseEntity.ok().build();
    }
}
