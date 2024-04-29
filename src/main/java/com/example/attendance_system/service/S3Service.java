package com.example.attendance_system.service;

import com.example.attendance_system.dto.S3Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3Service {
    private final S3Client s3Client;

    @Value("${amazon-s3.bucket}")
    private String bucketName;

    public void putObject(S3Request s3Request) {
        MultipartFile content = s3Request.content();
        String destinationKey = getDestinationKey(s3Request.id());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(destinationKey)
                .build();
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(content.getInputStream(), content.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] getObject(String key) {
        String destinationKey = getDestinationKey(key);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(destinationKey)
                .build();

        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        try {
            return object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDestinationKey(String key) {
        return "appeals/{key}".replace("{key}", key);
    }
}

