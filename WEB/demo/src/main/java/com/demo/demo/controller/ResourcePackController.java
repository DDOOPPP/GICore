package com.demo.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/resourcepack")
public class ResourcePackController {

    private static final String UPLOAD_DIR = "uploads/resourcepacks";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResourcePack(
            HttpServletRequest request,
            @RequestHeader("X-SHA1") String sha1,
            @RequestHeader("X-Filename") String filename) {
        log.info("Received resource pack upload request");
        log.info("SHA1: {}", sha1);
        log.info("Filename: {}", filename);
        try {
            // 1. 요청 본문(바이너리 파일) 읽기
            byte[] fileData = request.getInputStream().readAllBytes();

            // 2. SHA1 검증
            String calculatedSha1 = calculateSha1(fileData);
            if (!calculatedSha1.equals(sha1)) {
                return ResponseEntity.status(400)
                        .body("SHA1 mismatch. Expected: " + sha1 + ", Got: " + calculatedSha1);
            }

            // 3. 업로드 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            // 4. 파일 저장
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, fileData);

            System.out.println("Resource pack uploaded successfully");
            System.out.println("File: " + filePath.toAbsolutePath());
            System.out.println("Size: " + fileData.length + " bytes");
            System.out.println("SHA1: " + calculatedSha1);

            return ResponseEntity.ok("Upload successful");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Upload failed: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("SHA1 calculation failed: " + e.getMessage());
        }
    }

    private String calculateSha1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(data);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadResourcePack() {
        log.info("Received resource pack download request");

        try {
            Path filePath = Paths.get(UPLOAD_DIR,"resourcepack.zip");

            if (!Files.exists(filePath)){
                log.error("Resource pack file not found: {}", filePath);

                return ResponseEntity.status(404)
                        .body("Resource pack file not found".getBytes());
            }

            byte[] fileData = Files.readAllBytes(filePath);

            log.info("Resource pack file downloaded successfully");
            log.info("Size: {} bytes",fileData.length);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resourcepack.zip")
                    .header("Content-Disposition","attachment; filename=\"resourcepack.zip\"")
                    .body(fileData);
        } catch (IOException e) {
            log.error("Download failed: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(("Download failed: " + e.getMessage()).getBytes());
        }
    }
}
