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
            String excelPath = System.getProperty("user.dir") + "/src/test/resources/testdata/loginData.xlsx";
            return ExcelUtil.readSheet(excelPath, SHEET_NAME);
        } catch (Exception e) {
            System.out.println("🔥 ERROR IN 'loginData' PROVIDER: " + e.getMessage());
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    @Test(dataProvider = "loginData")
    public void loginCases(String email, String password, String runFlag, int rowIndex) {
        ExtentTest test = ReportManager.createTest(
                "Login Test - Row " + rowIndex,
                String.format("Email: %s | Password: %s", email, password)
        );

        try {
            if (runFlag == null || runFlag.trim().isEmpty()) {
                ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "SKIPPED");
                test.skip("RunFlag empty → skipping row " + rowIndex);
                throw new SkipException("RunFlag empty");
            }

            driver.manage().deleteAllCookies();
            driver.get("https://www.frontgate.com/ShoppingCartView?storeId=10053&catalogId=10053&langId=-1");

            LoginPage login = new LoginPage(driver);
            login.verifyLoginPageLoaded();
            test.info("✅ Login page loaded successfully.");

            login.enterEmail(email);
            login.enterPassword(password);
            login.clickSignIn();

            String errorMessage = login.getErrorMessage().trim();
            boolean loggedIn = login.isLoggedIn();

            if (loggedIn) {
                test.pass("✅ Successful login detected for user: " + email);
                ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");

                Assert.assertTrue(loggedIn, "User should be logged in successfully.");
                return;
            }

            if (!errorMessage.isEmpty()) {
                String lower = errorMessage.toLowerCase();

                boolean isExpected =
                        lower.contains("email address") ||
                        lower.contains("valid format") ||
                        lower.contains("current password") ||
                        lower.contains("not correct");

                Assert.assertTrue(
                        isExpected,
                        "Unexpected error message: " + errorMessage
                );

                test.pass("⚠️ Valid error message displayed: " + errorMessage);
                ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
                return;
            }

            attachScreenshot(test);
            test.fail("❌ No feedback shown for user: " + email);
            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "FAIL");
            Assert.fail("No feedback shown for user: " + email);

        } catch (SkipException se) {
            test.skip("⏭️ Skipped: " + se.getMessage());
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
            throw new RuntimeException("Unexpected error during test execution.", e);
        }
    }

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
