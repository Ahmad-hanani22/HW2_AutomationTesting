package com.itg.frontgate.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;
import java.time.Duration;
import com.itg.frontgate.util.ReportManager;

public class BaseTest {

    // جعل الدرايفر static ليتمكن الوصول إليه من كافة الكلاسات التي ترثه
    protected static WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ReportManager.initReport();
        System.out.println("📊 Extent Report initialized successfully.");
    }

    // 🔥 تغيير: سيتم تشغيله مرة واحدة فقط قبل أي <test> في الـ Suite
    @BeforeTest(alwaysRun = true)
    public void setUp() {
        System.out.println("🚀 Launching Chrome browser for the test run...");
        try {
            if (driver == null) { // ننشئ الدرايفر فقط إذا لم يكن موجودًا
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.setExperimentalOption("useAutomationExtension", false);
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

                driver = new ChromeDriver(options);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                if (driver != null) {
                    System.out.println("✅ Chrome launched and ready.");
                } else {
                    System.out.println("❌ ChromeDriver instance is null — failed to initialize!");
                    ReportManager.logFail("❌ ChromeDriver failed to initialize.");
                    throw new RuntimeException("ChromeDriver instance is null.");
                }
            }

        } catch (Exception e) {
            System.out.println("💥 Failed to launch Chrome browser: " + e.getMessage());
            e.printStackTrace();
            ReportManager.logFail("💥 Exception during browser setup: " + e.getMessage());
            throw new RuntimeException("💥 Browser setup failed.", e);
        }
    }

    // 🔥 تغيير: سيتم تشغيله مرة واحدة فقط بعد انتهاء كل الـ <test>
    @AfterTest(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                driver = null; // إعادة تعيينه لـ null لضمان إنشاء واحد جديد في الاختبار التالي
                System.out.println("🧹 Browser closed successfully.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to close browser: " + e.getMessage());
            ReportManager.logFail("⚠️ Browser close failed: " + e.getMessage());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ReportManager.flushReport();
        System.out.println("📘 Extent Report saved successfully.");
    }
}