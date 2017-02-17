package com.huotu.huotao.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author CJ
 */
public class DriverSetup {

    public static void setup() throws IOException {
        if (System.getProperty("webdriver.chrome.driver") != null)
            return;
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        // 平台判定
        String targetDriver;
        final String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            File file = File.createTempFile("chrome", "driver");
            try (InputStream inputStream = new ClassPathResource("/driver/mac/chromedriver").getInputStream()) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(inputStream, outputStream);
                }
            }
            file.deleteOnExit();
            assert file.setExecutable(true);
            targetDriver = file.getAbsolutePath();
        } else if (osName.contains("Win")) {
            File file = File.createTempFile("chrome", "driver.exe");
//            assert file.setExecutable(true);
            try (InputStream inputStream = new ClassPathResource("/driver/win32/chromedriver.exe").getInputStream()) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(inputStream, outputStream);
                }
            }
            file.deleteOnExit();
            targetDriver = file.getAbsolutePath();
        } else
            throw new RuntimeException("not support for:" + osName);
        System.setProperty("webdriver.chrome.driver", targetDriver);
    }

}
