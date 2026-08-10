package com.ecoapi.techstore.product.infrastructure.adapter.output.s3;

import com.ecoapi.techstore.product.application.port.out.ImageFile;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3StorageAdapterTest {
    @Test
    void wrapsAnR2OrS3UploadFailureWithoutLeakingTheStorageExceptionType() {
        S3Client client = mock(S3Client.class);
        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("provider unavailable").build());
        S3StorageAdapter adapter = new S3StorageAdapter(client, mock(S3Presigner.class));
        ReflectionTestUtils.setField(adapter, "bucketName", "techstore-test");

        ImageFile image = new ImageFile() {
            public ByteArrayInputStream getInputStream() { return new ByteArrayInputStream(new byte[]{1}); }
            public String getOriginalFilename() { return "image.png"; }
            public long getSize() { return 1; }
            public String getContentType() { return "image/png"; }
            public boolean isEmpty() { return false; }
        };

        assertThatThrownBy(() -> adapter.uploadImage(image, 7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("S3 upload failed");
    }
}
