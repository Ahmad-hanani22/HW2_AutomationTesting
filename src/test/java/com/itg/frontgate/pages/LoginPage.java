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

    public String getErrorMessage() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement errorElement = null;

            if (isElementVisible(LoginPageSelectors.ERROR_EMAIL, shortWait)) {
                errorElement = driver.findElement(LoginPageSelectors.ERROR_EMAIL);
            } else if (isElementVisible(LoginPageSelectors.ERROR_PASSWORD, shortWait)) {
                errorElement = driver.findElement(LoginPageSelectors.ERROR_PASSWORD);
            } else if (isElementVisible(LoginPageSelectors.ERROR_GENERAL, shortWait)) {
                errorElement = driver.findElement(LoginPageSelectors.ERROR_GENERAL);
            }

            if (errorElement != null) {
                String errorText = errorElement.getText().trim();
                if (errorText.isEmpty()) {
                    errorText = errorElement.getAttribute("innerText").trim();
                }
                System.out.println("⚠️ Error message detected: " + errorText);
                return errorText;
            }

            return "";

        } catch (Exception e) {
            System.out.println("⚠️ No error message found or unexpected issue: " + e.getMessage());
            return "";
        }
    }

    private boolean isElementVisible(By locator, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isLoggedIn() {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement accountBtn = longWait.until(
                    ExpectedConditions.elementToBeClickable(LoginPageSelectors.ACCOUNT_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accountBtn);
            System.out.println("👆 Clicked on My Account after login.");

            WebElement welcome = longWait.until(
                    ExpectedConditions.visibilityOfElementLocated(LoginPageSelectors.WELCOME_TEXT));

            String welcomeMsg = welcome.getText().trim();
            System.out.println("✅ Welcome message found: " + welcomeMsg);

            return welcome.isDisplayed();

        } catch (TimeoutException te) {
            System.out.println("❌ Welcome message not found after clicking My Account.");
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected error checking login: " + e.getMessage());
            return false;
        }
    }
}
