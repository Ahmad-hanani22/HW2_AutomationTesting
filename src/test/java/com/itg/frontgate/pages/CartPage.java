package com.itg.frontgate.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import com.itg.frontgate.selectors.CartPageSelectors;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void openCartPage() {
        driver.get("https://www.frontgate.com/ShoppingCartView?storeId=10053&catalogId=10053&langId=-1");
        wait.until(ExpectedConditions.urlContains("ShoppingCartView"));
        System.out.println("🛒 Opened cart page successfully.");
    }

    public void clickContinueShopping() {
        try {
            closeCookiesBannerIfPresent();
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    CartPageSelectors.CONTINUE_SHOPPING_BUTTON
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("🛍️ Clicked 'Continue Shopping' button successfully (via JS).");
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("category"),
                    ExpectedConditions.urlContains("product")
            ));
            System.out.println("✅ Navigated to products page successfully.");
        } catch (TimeoutException e) {
            System.out.println("❌ 'Continue Shopping' button not found or not clickable.");
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected issue while clicking 'Continue Shopping': " + e.getMessage());
        }
    }

    public boolean findAndClickProduct(String productName) {
        try {
            closePopupsIfPresent();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            System.out.println("🔍 Searching for product: " + productName);
            for (int i = 0; i < 10; i++) {
                List<WebElement> products = driver.findElements(CartPageSelectors.PRODUCT_CARDS);
                for (WebElement product : products) {
                    String text = product.getText().trim().toLowerCase();
                    if (text.contains(productName.toLowerCase())) {
                        System.out.println("✅ Found product: " + text);
                        js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", product);
                        Thread.sleep(500);
                        js.executeScript("arguments[0].click();", product);
                        System.out.println("🛒 Clicked on product successfully: " + productName);
                        return true;
                    }
                }
                js.executeScript("window.scrollBy(0, 600);");
                Thread.sleep(800);
            }
            System.out.println("❌ Product not found: " + productName);
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ Error while finding product: " + e.getMessage());
            return false;
        }
    }

    public void selectColorIfProvided(String colorText) {
        if (colorText == null || colorText.trim().isEmpty() || colorText.equalsIgnoreCase("—")) {
            System.out.println("ℹ️ No color selection required.");
            return;
        }
        System.out.println("🎨 Attempting to select color: " + colorText);
        try {
            List<WebElement> colorButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(CartPageSelectors.COLOR_OPTION_BUTTONS)
            );
            boolean colorFoundAndClicked = false;
            for (WebElement btn : colorButtons) {
                String identifiedColorName = "";
                try {
                    WebElement hiddenSpan = btn.findElement(By.cssSelector("span.visually-hidden"));
                    String fullText = hiddenSpan.getAttribute("textContent");
                    if (fullText != null && !fullText.isEmpty()) {
                        identifiedColorName = fullText.split(" ")[0].trim();
                    }
                } catch (NoSuchElementException e) {
                }
                
                if (identifiedColorName.equalsIgnoreCase(colorText)) {
                    System.out.println("✅✅✅ MATCH FOUND! Clicking on color: " + colorText);
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", btn);
                    Thread.sleep(500);
                    js.executeScript("arguments[0].click();", btn);
                    System.out.println("🎨 Color selected successfully via JS: " + colorText);
                    colorFoundAndClicked = true;
                    break; 
                }
            }
            if (!colorFoundAndClicked) {
                System.out.println("❌ CRITICAL: Color not found on page: " + colorText);
            }
        } catch (Exception e) {
            System.out.println("❌ CRITICAL: An unexpected error occurred while selecting color: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void selectSizeIfProvided(String sizeText) {
        if (sizeText == null || sizeText.trim().isEmpty() || sizeText.equalsIgnoreCase("—")) {
            System.out.println("ℹ️ No size selection required.");
            return;
        }
        System.out.println("📏 Attempting to select size: " + sizeText);
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement dropdownTrigger = wait.until(
                ExpectedConditions.presenceOfElementLocated(CartPageSelectors.SIZE_DROPDOWN_TRIGGER)
            );
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", dropdownTrigger);
            js.executeScript("arguments[0].click();", dropdownTrigger);
            System.out.println("📏 Clicked 'Select Size' header to open dropdown (via JS).");
            wait.until(ExpectedConditions.visibilityOfElementLocated(CartPageSelectors.SIZE_OPTIONS_CONTAINER));
            System.out.println("✅ Size options container is now visible.");
            List<WebElement> sizeOptions = driver.findElements(CartPageSelectors.SIZE_OPTION_BUTTONS);
            System.out.println("✅ Found " + sizeOptions.size() + " size options.");
            boolean sizeFoundAndClicked = false;
            for (WebElement optionButton : sizeOptions) {
                String buttonText = optionButton.getText(); 
                System.out.println("    - Checking option: '" + buttonText + "'");
                if (buttonText.toLowerCase().contains(sizeText.toLowerCase())) {
                    System.out.println("✅ Match found! Clicking on size: " + sizeText);
                    js.executeScript("arguments[0].click();", optionButton);
                    sizeFoundAndClicked = true;
                    Thread.sleep(1000); 
                    break;
                }
            }
            if (!sizeFoundAndClicked) {
                System.out.println("❌ CRITICAL: Size option not found: " + sizeText);
            }
        } catch (Exception e) {
            System.out.println("❌ CRITICAL: An unexpected error occurred while selecting the size: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickAddToCart() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CartPageSelectors.ADD_TO_CART_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("🧺 Clicked Add to Cart button.");
        } catch (TimeoutException e) {
            System.out.println("⚠️ Add to Cart button not found or not clickable.");
        } catch (Exception e) {
            System.out.println("⚠️ Error clicking Add to Cart: " + e.getMessage());
        }
    }

    public void clickViewCart() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CartPageSelectors.VIEW_CART_BUTTON));
            btn.click();
            System.out.println("🛒 Clicked 'View Cart' button successfully.");
            wait.until(ExpectedConditions.visibilityOfElementLocated(CartPageSelectors.CHECKOUT_NOW_BUTTON));
            System.out.println("✅ Navigated to the main cart page.");
        } catch (Exception e) {
            System.out.println("❌ 'View Cart' button not found or not clickable: " + e.getMessage());
            throw e;
        }
    }

    public void clickCheckoutNow() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CartPageSelectors.CHECKOUT_NOW_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            System.out.println("💳 Clicked 'Checkout Now' button successfully.");
        } catch (Exception e) {
            System.out.println("❌ 'Checkout Now' button not found or not clickable: " + e.getMessage());
            throw e;
        }
    }

    public void closeCookiesBannerIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement cookieCloseBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(CartPageSelectors.COOKIE_CLOSE_BUTTON)
            );
            cookieCloseBtn.click();
            System.out.println("🍪 Cookie banner closed successfully.");
            wait.until(ExpectedConditions.invisibilityOf(cookieCloseBtn));
        } catch (Exception e) {
            // Ignore if not present
        }
    }
    
    public void closePopupsIfPresent() {
        try {
            closeCookiesBannerIfPresent();
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement popupCloseBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(CartPageSelectors.GENERIC_POPUP_CLOSE_BUTTON)
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", popupCloseBtn);
            System.out.println("📣 Generic popup closed successfully.");
            wait.until(ExpectedConditions.invisibilityOf(popupCloseBtn));
        } catch (TimeoutException e) {
            System.out.println("✅ No popups appeared.");
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected issue while closing popups: " + e.getMessage());
        }
    }

    public String getCartBadgeCount() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement badge = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(CartPageSelectors.CART_BADGE_COUNT)
            );
            return badge.getText().trim();
        } catch (Exception e) {
            System.out.println("⚠️ Could not find or read the cart badge count.");
            return "";
        }
    }

    public void loginOnCheckoutPageIfRequired(String email, String password) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement emailInput = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(CartPageSelectors.CHECKOUT_EMAIL_INPUT)
            );

            System.out.println("ℹ️ Login form detected on checkout page. Proceeding to log in...");
            
            emailInput.sendKeys(email);
            driver.findElement(CartPageSelectors.CHECKOUT_PASSWORD_INPUT).sendKeys(password);
            System.out.println("✅ Credentials entered on checkout page.");

            WebElement signInButton = driver.findElement(CartPageSelectors.SIGN_IN_TO_CHECKOUT_BUTTON);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signInButton);
            System.out.println("🚀 Clicked 'Sign In To Checkout' button.");

        } catch (TimeoutException e) {
            System.out.println("✅ User is already logged in. Skipping login on checkout page.");
        } catch (Exception e) {
            System.out.println("❌ An unexpected error occurred during login on the checkout page: " + e.getMessage());
            throw new RuntimeException("Failed during checkout page login.", e);
        }
    }
}