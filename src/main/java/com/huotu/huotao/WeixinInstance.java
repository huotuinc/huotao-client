package com.huotu.huotao;

import com.google.common.base.Predicate;
import com.huotu.huotao.util.DriverSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Optional;

/**
 * 微信实例
 *
 * @author CJ
 */
public class WeixinInstance {

    static {
        try {
            DriverSetup.setup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final WebDriver driver = new ChromeDriver();

    public WeixinInstance() {
        driver.get("https://wx.qq.com");
    }

    /**
     * @return 是否已登录
     */
    public boolean isLoggedIn() {
        return isLoggedIn(driver);
    }

    public void login() {
        try {
            new WebDriverWait(driver, 300).until((Predicate<WebDriver>) input
                    -> input != null && isLoggedIn(input));
            System.out.println("login success");
        } catch (NoSuchElementException ex) {
            // already loggined.
        }
    }

    private boolean isLoggedIn(WebDriver input) {
        return input.findElement(By.className("main")).isDisplayed();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("quiting..");
        driver.close();
        driver.quit();
    }

    /**
     * 发送文本消息
     *
     * @param to   收信人昵称
     * @param text 内容
     */
    public void sendTextMessage(String to, String text) {
        if (!isLoggedIn())
            login();
        chooseContact(to);

        WebElement editArea = driver.findElement(By.id("editArea"));

        WebElement sendButton = driver.findElement(By.cssSelector("div.action")).findElement(By.tagName("a"));

        editArea.clear();
        editArea.sendKeys(text);
        sendButton.click();
    }

    /**
     * 选择对话的名称
     *
     * @param name 名称
     */
    private void chooseContact(String name) {
        if (currentTitle(name, driver))
            return;
        WebElement bar = driver.findElement(By.id("search_bar"));

        WebElement searchInput = bar.findElement(By.tagName("input"));
//        contacts scrollbar-dynamic scroll-content scroll-scrolly_visible
        searchInput.clear();
        searchInput.sendKeys(name);

        new WebDriverWait(driver, 5).until((Predicate<WebDriver>) input
                -> input != null && findSearchResult(input).isPresent());

        WebElement listDiv = findSearchResult(driver)
                .orElseThrow(() -> new IllegalStateException("找不到搜索结果列表"));

        listDiv.findElements(By.className("ng-scope")).stream()
                .filter(element -> !element.findElements(By.tagName("h4")).isEmpty())
                .filter(element -> element.findElement(By.tagName("h4")).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到好友" + name))
                .click();

        new WebDriverWait(driver, 5)
                .until((Predicate<WebDriver>) input
                        -> input != null && currentTitle(name, input));
    }

    private boolean currentTitle(String title, WebDriver input) {
        return input.findElement(By.cssSelector("div.title")).getText().equals(title);
    }

    private Optional<WebElement> findSearchResult(WebDriver driver) {
        return driver.findElements(By.className("contacts")).stream()
//                .peek(System.out::println)
                .filter(element -> element.getAttribute("class").contains("scroll-content"))
                .findFirst();
    }

    public void sendFile(String to, Resource resource) throws IOException {
        if (!isLoggedIn())
            login();
        chooseContact(to);

        WebElement toolBar = driver.findElement(By.id("tool_bar"));
        // webuploader-pick
        WebElement uploader = toolBar.findElements(By.tagName("a")).stream()
                .peek(System.out::println)
                .filter(element -> !element.findElements(By.className("webuploader-pick")).isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到上传按钮"));
//        uploader.click();
        WebElement fileInput = uploader.findElement(By.cssSelector("input[type=file]"));

        // 保存这个Resource到临时文件
//        File file = File.createTempFile("",resource.get)
        fileInput.sendKeys(resource.getFile().getAbsolutePath());
        System.out.println(1);
    }
}
