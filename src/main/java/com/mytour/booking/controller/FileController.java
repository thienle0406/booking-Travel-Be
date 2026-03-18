package com.mytour.booking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    // Lấy đường dẫn từ application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn một file để upload.");
        }

        try {
            // Validate file type (only images)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file ảnh (PNG, JPG, GIF, etc.)");
            }

            // 1. Tạo tên file DUY NHẤT (UUID) để tránh trùng lặp
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                // Nếu không có extension, dùng extension từ content type
                if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                    extension = ".jpg";
                } else if (contentType.contains("png")) {
                    extension = ".png";
                } else if (contentType.contains("gif")) {
                    extension = ".gif";
                } else {
                    extension = ".jpg"; // Default
                }
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // 2. Xác định sub-folder theo loại (nếu có), ví dụ: "Biển đảo" -> "biendao"
            String safeFolder = null;
            if (folder != null && !folder.trim().isEmpty()) {
                safeFolder = slugify(folder);
            }

            // 3. Tạo đường dẫn lưu vật lý
            Path baseUploadPath = Paths.get(uploadDir);
            Path uploadPath = (safeFolder != null) ? baseUploadPath.resolve(safeFolder) : baseUploadPath;
            Files.createDirectories(uploadPath); // Đảm bảo thư mục tồn tại
            Path copyLocation = uploadPath.resolve(fileName);

            // 4. Lưu file
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            // 5. Trả về URL công khai (URL mà Frontend sẽ dùng để hiển thị)
            // Khớp với spring.mvc.static-path-pattern: /uploads/images/**
            String publicUrl;
            if (safeFolder != null) {
                publicUrl = "/uploads/images/" + safeFolder + "/" + fileName;
            } else {
                publicUrl = "/uploads/images/" + fileName;
            }

            return ResponseEntity.ok(publicUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi lưu file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi upload file: " + e.getMessage());
        }
    }

    /**
     * Chuyển chuỗi Unicode có dấu thành slug không dấu, chỉ giữ [a-z0-9].
     * Ví dụ: "Biển đảo" -> "biendao"
     */
    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String lower = withoutDiacritics.toLowerCase();
        // Chỉ giữ a-z, 0-9
        String alnum = lower.replaceAll("[^a-z0-9]", "");
        // Fallback
        return alnum.isEmpty() ? "others" : alnum;
    }
}