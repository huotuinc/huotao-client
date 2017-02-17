package com.huotu.huotao;

import com.google.common.base.Predicate;
import com.huotu.huotao.util.DriverSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

/**
 * 微信实例
 *
 * @author CJ
 */
public class WeixinInstance {

    private WebDriver driver;

    static {
        try {
            DriverSetup.setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WeixinInstance() {
        this.driver = new ChromeDriver();
        driver.get("https://wx.qq.com");
    }

    public void login() {
        try {
            driver.findElement(By.className("login_box"));
            new WebDriverWait(driver, 300).until(new Predicate<WebDriver>() {
                @Override
                public boolean apply(WebDriver input) {
                    if (input == null)
                        return false;
                    return input.findElement(By.className("main")).isDisplayed();
                }
            });
            System.out.println("login success");
        } catch (NoSuchElementException ex) {
            // already loggined.
        }
    }

    @Override
    protected void finalize() throws Throwable {
        driver.quit();
    }
}
