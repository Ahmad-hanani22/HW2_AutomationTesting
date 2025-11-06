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
        this.driver = (WebDriver) this;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // زيادة مدة الانتظار العام
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
        if(email == null || email.trim().isEmpty()) return;
        WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.EMAIL_INPUT));
        e.clear();
        e.sendKeys(email);
    }

    public void enterPassword(String pass) {
        if(pass == null || pass.trim().isEmpty()) return;
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

    public String getErrorMessage() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement errorElement = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.ERROR_GENERAL)
            );
            String errorText = errorElement.getText().trim();
            System.out.println("⚠️ Error message detected: " + errorText);
            return errorText;
        } catch (Exception e) {
            return ""; 
        }
    }

    public boolean isLoggedIn() {
        try {
            System.out.println("▶️ Verifying login success...");
            
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30)); // انتظار أطول للدخول
            WebElement accountBtn = longWait.until(
                    ExpectedConditions.elementToBeClickable(LoginPageSelectors.ACCOUNT_BUTTON));
            
            System.out.println("✅ Account icon is ready. Clicking to open menu...");
            
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accountBtn);

            System.out.println("⏳ Waiting for 'Welcome' message to appear...");
            WebElement welcome = longWait.until(
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.WELCOME_TEXT));

            String welcomeMsg = welcome.getText().trim();
            System.out.println("✅✅✅ SUCCESS! Welcome message found: " + welcomeMsg);
            
            return welcome.isDisplayed();

        } catch (TimeoutException te) {
            System.out.println("❌ FAILED: Welcome message was not found within the time limit.");
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected error while checking login status: " + e.getMessage());
            return false;
        }
    }
}