package com.project.platform.service.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.project.platform.exception.CustomException;
import com.project.platform.service.FileService;
import com.project.platform.vo.FileInfoVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${server.ip}")
    private String serverIp;
    @Value("${server.port}")
    private int serverPort;
    @Value("${files.uploads.path}")
    private String basePath;

    @Value("${files.uploads.baseUrl:}")
    private String fileBaseUrl;


    public FileInfoVO upload(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException("上传文件不能为空");
        }
        String fix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (StringUtils.isBlank(fix)) {
            throw new CustomException("文件扩展名不能为空");
        }
        String md5 = getMD5Checksum(multipartFile);
        String newFileName = md5 + "." + fix;
        File newFile = createFile(newFileName);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setUrl(getServer() + "/" + newFileName);
        fileInfoVO.setName(newFileName);
        return fileInfoVO;
    }


    private File createFile(String fileName) throws IOException {
        Path baseDirectory = resolveBaseDirectory();
        Path filePath = baseDirectory.resolve(fileName).normalize();
        if (!filePath.startsWith(baseDirectory)) {
            throw new CustomException("文件路径不合法");
        }
        Files.createDirectories(filePath.getParent());
        return filePath.toFile();
    }


    private String getServer() {
        String resolvedBaseUrl = StringUtils.trimToEmpty(fileBaseUrl);
        if (StringUtils.isNotEmpty(resolvedBaseUrl)) {
            return StringUtils.removeEnd(resolvedBaseUrl, "/");
        }
        try {
            String currentBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/file")
                    .toUriString();
            if (StringUtils.isNotBlank(currentBaseUrl)) {
                return StringUtils.removeEnd(currentBaseUrl, "/");
            }
        } catch (Exception ignored) {
        }
        return UrlBuilder.create()
                .setScheme("http")
                .setHost(StringUtils.defaultIfBlank(serverIp, "localhost"))
                .setPort(serverPort)
                .addPath("file")
                .build();
    }


    private String getFilePath(String fileName) {
        return resolveBaseDirectory().resolve(fileName).normalize().toString();
    }

    public File getFile(String fileName) throws IOException {
        validateFileName(fileName);
        return new File(getFilePath(fileName));
    }

    private String getMD5Checksum(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        byte[] fileBytes = file.getBytes();
        md5Digest.update(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : md5Digest.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Path resolveBaseDirectory() {
        String resolvedBasePath = StringUtils.defaultIfBlank(basePath, "uploads");
        return Paths.get(resolvedBasePath).toAbsolutePath().normalize();
    }

    private void validateFileName(String fileName) {
        if (StringUtils.isBlank(fileName)
                || fileName.contains("..")
                || fileName.contains("/")
                || fileName.contains("\\")) {
            throw new CustomException("文件名不合法");
        }
    }
}
