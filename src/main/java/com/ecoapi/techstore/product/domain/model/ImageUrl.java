package com.ecoapi.techstore.product.domain.model;

import java.util.Objects;

/**
 * Value Object representing an Image URL
 * Ensures URL is valid and properly formatted
 */
public class ImageUrl {
    
    private final String url;
    
    private ImageUrl(String url) {
        this.url = url;
    }
    
    public static ImageUrl of(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
        
        // Basic URL validation
        String trimmedUrl = url.trim();
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Image URL must start with http:// or https://");
        }
        
        if (trimmedUrl.length() > 500) {
            throw new IllegalArgumentException("Image URL cannot exceed 500 characters");
        }
        
        return new ImageUrl(trimmedUrl);
    }
    
    /**
     * Creates an ImageUrl without validation - use only when reconstituting from persistence
     */
    public static ImageUrl ofNullable(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        return new ImageUrl(url.trim());
    }
    
    public String value() {
        return url;
    }
    
    public boolean isS3Url() {
        return url.contains(".s3.amazonaws.com") || url.contains("s3.amazonaws.com");
    }
    
    public String extractKey() {
        if (!isS3Url()) {
            throw new IllegalStateException("Cannot extract key from non-S3 URL");
        }
        
        // Extract key from URL format: https://bucket-name.s3.amazonaws.com/key
        if (url.contains(".s3.amazonaws.com/")) {
            int keyStart = url.indexOf(".s3.amazonaws.com/") + 18;
            return url.substring(keyStart);
        }
        
        throw new IllegalStateException("Invalid S3 URL format");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageUrl imageUrl = (ImageUrl) o;
        return Objects.equals(url, imageUrl.url);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
    
    @Override
    public String toString() {
        return url;
    }
}
