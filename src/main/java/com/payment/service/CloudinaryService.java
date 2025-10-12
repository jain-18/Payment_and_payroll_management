package com.payment.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(byte[] fileBytes, String originalFilename) throws IOException {
        // Check for null or empty byte array
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File bytes are null or empty! Cannot upload: " + originalFilename);
        }

        // Log info to verify
        System.out.println("Uploading file: " + originalFilename + ", size: " + fileBytes.length + " bytes");

        try {
            Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                    "resource_type", "auto",
                    "public_id", originalFilename));

            String url = uploadResult.get("secure_url").toString();
            System.out.println("Upload successful! URL: " + url);
            return url;

        } catch (Exception e) {
            System.out.println("Upload failed for file: " + originalFilename);
            e.printStackTrace();
            throw new IllegalStateException("Cloudinary upload failed: " + e.getMessage());
        }
    }

}
