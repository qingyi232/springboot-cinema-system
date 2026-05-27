package com.project.platform;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "files.uploads.path=test-uploads/"
        }
)
class FileUploadIntegrationTest {

    private static final Path TEST_UPLOAD_DIR = Path.of("test-uploads");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @AfterEach
    void cleanup() throws IOException {
        if (!Files.exists(TEST_UPLOAD_DIR)) {
            return;
        }
        try (var paths = Files.walk(TEST_UPLOAD_DIR)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (DirectoryNotEmptyException ignored) {
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
        }
    }

    @Test
    void upload_should_store_file_under_relative_upload_directory() throws IOException {
        ByteArrayResource fileResource = new ByteArrayResource("fake-image-content".getBytes()) {
            @Override
            public String getFilename() {
                return "avatar.png";
            }
        };

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.IMAGE_PNG);
        fileHeaders.setContentDisposition(ContentDisposition.formData().name("file").filename("avatar.png").build());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new HttpEntity<>(fileResource, fileHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "http://127.0.0.1:" + port + "/file/upload",
                new HttpEntity<>(body, headers),
                String.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() != null && response.getBody().contains("\"code\":200"));
        assertTrue(Files.exists(TEST_UPLOAD_DIR));
        try (var files = Files.list(TEST_UPLOAD_DIR)) {
            assertTrue(files.findAny().isPresent());
        }
    }
}
