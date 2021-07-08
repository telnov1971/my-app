package com.example.application.config;

import org.springframework.stereotype.Component;

@Component
public class AppEnv {
    private static String uploadPath;

    public AppEnv() {
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public static void setUploadPath(String Path) {
        uploadPath = Path;
    }
}
