package com.example.attendance_system.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record S3Request(
        String id,

        String reason,
        MultipartFile content
) {

}
