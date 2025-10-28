package com.itg.frontgate.tests;

import com.itg.frontgate.base.BaseTest;
import com.itg.frontgate.pages.LoginPage;
import com.itg.frontgate.util.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginDataDrivenTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        String path = "src/test/resources/testdata/loginData.xlsx";
        return ExcelUtil.readSheet(path, "Users");
    }

    @Test(dataProvider = "loginData")
    public void loginCases(String email, String password) {
        driver.manage().deleteAllCookies();
        driver.get("https://www.frontgate.com/ShoppingCartView?storeId=10053&catalogId=10053&langId=-1");

        LoginPage login = new LoginPage(driver);
        login.verifyLoginPageLoaded();
        login.enterEmail(email);
        login.enterPassword(password);
        login.clickSignIn();

        boolean errorVisible = login.isErrorVisible();
        boolean loggedIn = login.isLoggedIn();

        if (errorVisible) {
            System.out.println("❌ Invalid login detected for user: " + email);
            Assert.assertTrue(true);
        } else if (loggedIn) {
            System.out.println("✅ Successful login detected for user: " + email);
            Assert.assertTrue(true);
        } else {
            System.out.println("⚠️ No visible feedback for: " + email);
            Assert.fail("No feedback shown for " + email);
        }
    }
}
