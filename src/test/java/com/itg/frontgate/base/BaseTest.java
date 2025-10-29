package com.itg.frontgate.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import java.time.Duration;
import com.itg.frontgate.util.ReportManager;

public class BaseTest {
    protected WebDriver driver;

    // ✅ يتم تهيئة المتصفح والتقرير مرة واحدة عند بداية الـ Suite
    @BeforeClass
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();
        ReportManager.initReport(); // إنشاء تقرير جديد قبل كل suite
    }

    // ✅ يتم فتح المتصفح قبل كل اختبار
    @BeforeMethod
    public void setUpMethod() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    // ✅ يتم إغلاق المتصفح بعد كل اختبار
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {
        if (driver != null) driver.quit();
    }

    // ✅ يتم حفظ التقرير بعد انتهاء كل Suite
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        ReportManager.flushReport();
    }
}
