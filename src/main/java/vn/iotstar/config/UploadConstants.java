package vn.iotstar.config;

import java.io.File;

public final class UploadConstants {
    public static final String DIR = AppProperties.get("app.upload.dir",
            System.getProperty("user.home") + File.separator + "uploads" + File.separator + "assignment03-user-profile");

    private UploadConstants() {
    }
}
