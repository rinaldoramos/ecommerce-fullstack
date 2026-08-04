package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("jpg", "jpeg", "png");

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    @Override
    public String saveImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains("."))
            throw new APIException("Invalid file type");

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!ALLOWED_IMAGE_TYPES.contains(extension))
            throw new APIException("Unsupported image type extension: :: " + extension);

        File file = new File(uploadDirectory);
        if (!file.exists())
            file.mkdirs();

        String newFileName = UUID.randomUUID() + "." + extension;
        Path filePath  = Paths.get(uploadDirectory, newFileName);

        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newFileName;
    }

    public void deleteOldImage(String oldImage) {
        if (oldImage == null || oldImage.isBlank())
            return;

        Path filePath = Paths.get(uploadDirectory, oldImage);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
