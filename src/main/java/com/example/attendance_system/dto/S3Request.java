package com.example.attendance_system.dto;

import com.example.attendance_system.model.Reason;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record S3Request(
        String id,

        Reason reason,
        MultipartFile content
) {

}
