package ru.omel.po.config;

import org.springframework.stereotype.Component;

@Component
public class AppEnv {
    private static String uploadPath;
    private static String dbName;
    private static Boolean newMode = true;

    public AppEnv() {
    }

    public static String getUploadPath() {
        return uploadPath;
    }
    public static void setUploadPath(String Path) {
        uploadPath = Path;
    }
    public static String getDbName() {
        return dbName;
    }
    public static void setDbName(String dbName) {
        AppEnv.dbName = dbName;
    }
    public static Boolean getNewMode() {
        return newMode;
    }
    public static void setNewMode(Boolean newMode) {
        AppEnv.newMode = newMode;
    }
}
