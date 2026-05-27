package com.project.platform.controller;

import com.project.platform.service.FileService;
import com.project.platform.vo.FileInfoVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
@RestController
@RequestMapping("/file")
public class FilesController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseVO<FileInfoVO> upload(@RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException {
        return ResponseVO.ok(fileService.upload(file));
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> get(@PathVariable("fileName") String filename) throws IOException {
        File file = fileService.getFile(filename);
        if (file.exists() && !file.isDirectory()) {
            org.springframework.core.io.Resource resource = new FileSystemResource(file);
            MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(mediaType)
                    .contentLength(file.length())
                    .body(resource);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
