package com.ecommerce.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveImage(MultipartFile image);

    void deleteOldImage(String oldImage);
}

