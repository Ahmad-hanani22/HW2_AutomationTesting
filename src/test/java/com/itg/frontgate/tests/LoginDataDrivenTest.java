package com.itg.frontgate.tests;

import com.itg.frontgate.base.BaseTest;
import com.itg.frontgate.pages.LoginPage;
import com.itg.frontgate.util.ExcelUtil;
import com.itg.frontgate.util.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class LoginDataDrivenTest extends BaseTest {

    private static final String EXCEL_PATH = "src/test/resources/testdata/loginData.xlsx";
    private static final String SHEET_NAME = "Users";

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        try {
            // 🔥 تعديل: استخدام المسار المطلق لضمان العثور على الملف
            String excelPath = System.getProperty("user.dir") + "/src/test/resources/testdata/loginData.xlsx";
            return ExcelUtil.readSheet(excelPath, SHEET_NAME); // SHEET_NAME is "Users"
        } catch (Exception e) {
            System.out.println("🔥🔥🔥 ERROR IN 'loginData' PROVIDER: " + e.getMessage());
            e.printStackTrace(); // هذه أهم طباعة، ستظهر الخطأ بالتفصيل
            return new Object[0][0];
        }
    }

    @Test(dataProvider = "loginData")
    public void loginCases(String email, String password, String runFlag, int rowIndex) {
        ExtentTest test = ReportManager.createTest("Login Test - Row " + rowIndex,
                "Email: " + email + " | Password: " + password);

        try {
            // ⏭️ تخطي في حال الـ RunFlag فارغ
            if (runFlag == null || runFlag.trim().isEmpty()) {
                ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "SKIPPED");
                test.skip("RunFlag is empty. Skipping row " + rowIndex);
                throw new SkipException("RunFlag empty");
            }

            driver.manage().deleteAllCookies();
            driver.get("https://www.frontgate.com/ShoppingCartView?storeId=10053&catalogId=10053&langId=-1");

            LoginPage login = new LoginPage(driver);
            login.verifyLoginPageLoaded();

            test.info("✅ Page loaded successfully");
            login.enterEmail(email);
            login.enterPassword(password);
            login.clickSignIn();

            String errorMessage = login.getErrorMessage();
            boolean loggedIn = login.isLoggedIn();

            if (!errorMessage.isEmpty()) {
                test.info("⚠️ Error detected: " + errorMessage);
                if (errorMessage.equalsIgnoreCase("Error: Please enter Email Address in a valid format.")) {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                    test.pass("Valid error message verified.");
                } else if (errorMessage.equalsIgnoreCase("Error: Please enter Current Password.")) {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                    test.pass("Valid password error verified.");
                } else if (errorMessage.equalsIgnoreCase("Email/Password you entered is not correct. Please try again.")) {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                    test.pass("Valid incorrect credentials message verified.");
                } else {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "FAIL");
                    attachScreenshot(test);
                    test.fail("Unexpected error message: " + errorMessage);
                    Assert.fail("Unexpected error message: " + errorMessage);
                }

            } else if (loggedIn) {
                ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                test.pass("✅ Successful login detected for user: " + email);
            } else {
                if (email != null && email.contains("@@")) {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                    test.pass("Heuristic pass: Invalid email format handled.");
                } else {
                    ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "FAIL");
                    attachScreenshot(test);
                    test.fail("No feedback shown for " + email);
                    Assert.fail("No feedback shown for " + email);
                }
            }

        } catch (SkipException se) {
            test.skip("⏭️ Test skipped: " + se.getMessage());
            throw se;
        } catch (AssertionError ae) {
            attachScreenshot(test);
            test.fail("❌ Assertion failed: " + ae.getMessage());
            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "FAIL");
            throw ae;
        } catch (Exception e) {
            attachScreenshot(test);
            test.fail("💥 Unexpected error: " + e.getMessage());
            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "ERROR");
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // 🔹 دالة لتصوير الشاشة عند الفشل
    private void attachScreenshot(ExtentTest test) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = "test-output/screenshots/snap_" + System.currentTimeMillis() + ".png";
            FileUtils.copyFile(src, new File(path));
            test.addScreenCaptureFromPath(path);
        } catch (IOException e) {
            test.warning("⚠️ Failed to attach screenshot: " + e.getMessage());
        }
    }
}
