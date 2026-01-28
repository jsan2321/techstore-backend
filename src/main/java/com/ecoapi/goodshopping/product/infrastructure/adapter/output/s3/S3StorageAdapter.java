package com.ecoapi.goodshopping.product.infrastructure.adapter.output.s3;

import com.ecoapi.goodshopping.product.application.port.out.S3StoragePort;
import com.ecoapi.goodshopping.product.domain.model.ImageUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

/**
 * S3 Storage Adapter
 * Implementation of S3StoragePort for AWS S3 operations
 */
@Component
public class S3StorageAdapter implements S3StoragePort {
    
    private static final Logger logger = LoggerFactory.getLogger(S3StorageAdapter.class);
    
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    public S3StorageAdapter(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }
    
    @Override
    public ImageUrl uploadImage(MultipartFile file, Long productId) {
        try {
            validateFile(file);
            
            String key = generateKey(productId, file.getOriginalFilename());
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String imageUrlString = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            ImageUrl imageUrl = ImageUrl.of(imageUrlString);
            logger.info("Successfully uploaded image for product {}: {}", productId, imageUrl.value());
            
            return imageUrl;
            
        } catch (IOException e) {
            logger.error("Failed to upload image for product {}", productId, e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        } catch (S3Exception e) {
            logger.error("S3 error while uploading image for product {}", productId, e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteImage(ImageUrl imageUrl) {
        try {
            if (imageUrl == null) {
                return;
            }
            
            String key = imageUrl.extractKey();
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Successfully deleted image: {}", imageUrl.value());
            
        } catch (S3Exception e) {
            logger.error("Failed to delete image: {}", imageUrl, e);
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generatePresignedUrl(ImageUrl imageUrl) {
        try {
            String key = imageUrl.extractKey();
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();
            
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            
            return presignedRequest.url().toString();
            
        } catch (S3Exception e) {
            logger.error("Failed to generate presigned URL for: {}", imageUrl.value(), e);
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Max 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }
    }
    
    private String generateKey(Long productId, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        return String.format("products/%d/%s%s", 
                productId, 
                UUID.randomUUID().toString(), 
                extension);
    }
}
