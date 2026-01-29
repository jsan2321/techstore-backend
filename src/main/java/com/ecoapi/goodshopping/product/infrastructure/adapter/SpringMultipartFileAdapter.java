package com.ecoapi.goodshopping.product.infrastructure.adapter;

import org.springframework.web.multipart.MultipartFile;

import com.ecoapi.goodshopping.product.application.port.out.ImageFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Adapter that wraps Spring's MultipartFile to conform to domain's ImageFile interface
 * This allows the domain and application layers to remain framework-agnostic
 */
public class SpringMultipartFileAdapter implements ImageFile {
    
    private final MultipartFile delegate;
    
    private SpringMultipartFileAdapter(MultipartFile delegate) {
        this.delegate = delegate;
    }
    
    public static ImageFile from(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }
        return new SpringMultipartFileAdapter(multipartFile);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }
    
    @Override
    public String getOriginalFilename() {
        return delegate.getOriginalFilename();
    }
    
    @Override
    public long getSize() {
        return delegate.getSize();
    }
    
    @Override
    public String getContentType() {
        return delegate.getContentType();
    }
    
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}
