package com.itg.frontgate.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.itg.frontgate.selectors.LoginPageSelectors;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void verifyLoginPageLoaded() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        System.out.println("⬇️ Scrolling down to make Sign In section visible...");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.EMAIL_INPUT));
            wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.PASSWORD_INPUT));
            System.out.println("✅ Login section loaded successfully.");
        } catch (TimeoutException e) {
            System.out.println("❌ Login section not visible — might still be loading or layout changed.");
            throw e;
        }
    }

    public void enterEmail(String email) {
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.EMAIL_INPUT));
        e.clear();
        e.sendKeys(email);
    }

    public void enterPassword(String pass) {
        WebElement p = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.PASSWORD_INPUT));
        p.clear();
        p.sendKeys(pass);
    }

    public void clickSignIn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(LoginPageSelectors.SIGNIN_BUTTON));
        try {
            btn.click();
            System.out.println("✅ Clicked on Sign In button.");
        } catch (Exception ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("⚙️ Clicked Sign In via JavaScript.");
        }
    }

    public boolean isErrorVisible() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.ERROR_EMAIL),
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.ERROR_PASSWORD),
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.ERROR_GENERAL)
            ));
            return true; 
        } catch (TimeoutException te) {
            return false; 
        }
    }


    public boolean isLoggedIn() {
        try {
            WebElement welcome = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.ACCOUNT_HEADER)
            );
            return welcome.isDisplayed();
        } catch (TimeoutException te) {
            return false;
        }
    }
}
