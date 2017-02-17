package com.huotu.huotao;

import com.huotu.huotao.util.DriverSetup;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 1.7
 * <p>
 * 2 17 15天
 *
 * @author CJ
 */
public class DoWx {

    @Test
    public void testGoogleSearch() throws InterruptedException, IOException {
        DriverSetup.setup();
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.baidu.com");
        Thread.sleep(5000);  // Let the user actually see something!
//        WebElement searchBox = driver.findElement(By.name("q"));
//        searchBox.sendKeys("ChromeDriver");
//        searchBox.submit();
        Thread.sleep(5000);  // Let the user actually see something!
        driver.quit();
    }

    @Test
    public void go() throws IOException {
        DriverSetup.setup();
        ChromeDriverService service = new ChromeDriverService.Builder()
                .withSilent(true)
                .usingAnyFreePort()
                .build();

        service.start();

        WebDriver driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());

        driver.get("https://wx.qq.com");

        driver.quit();
        service.stop();
    }

}
