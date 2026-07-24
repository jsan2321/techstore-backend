package com.ecoapi.techstore.product.application.port.out;

import java.io.IOException;
import java.io.InputStream;

/**
 * Domain abstraction for an image file
 * Decouples domain from web framework (MultipartFile)
 */
public interface ImageFile {
    
    /**
     * Get the file content as a stream
     * @throws IOException if an error occurs reading the file
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * Get the original filename
     */
    String getOriginalFilename();
    
    /**
     * Get the file size in bytes
     */
    long getSize();
    
    /**
     * Get the content type (MIME type)
     */
    String getContentType();
    
    /**
     * Check if the file is empty
     */
    boolean isEmpty();
}
