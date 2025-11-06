package com.itg.frontgate.pages;

import com.itg.frontgate.selectors.CheckoutPageSelectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.time.Duration;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private void clearAndSendKeysJS(WebElement element, String text) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].value='';", element);
            element.sendKeys(text);
        } catch (Exception e) {
            js.executeScript("arguments[0].value=arguments[1];", element, text);
        }
    }

    public void fillShippingAddressAndContinue(
            String firstName,
            String lastName,
            String streetAddress,
            String zipCode,
            String city,
            String state,
            String phone
    ) {
        try {
            System.out.println("ℹ️ Starting SHIPPING step...");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    CheckoutPageSelectors.SHIPPING_HEADER
            ));
            System.out.println("✅ SHIPPING area visible.");


            if (isSavedAddressPresent()) {
                System.out.println("✅ Saved address found → Skipping manual entry.");
                clickContinueToDelivery();
                Thread.sleep(1000);
                clickContinueToPayment();
                return;
            }


            clearAndSendKeysJS(wait.until(ExpectedConditions.elementToBeClickable(
                    CheckoutPageSelectors.FIRST_NAME_INPUT
            )), firstName);

            clearAndSendKeysJS(driver.findElement(CheckoutPageSelectors.LAST_NAME_INPUT), lastName);
            clearAndSendKeysJS(driver.findElement(CheckoutPageSelectors.STREET_ADDRESS_INPUT), streetAddress);
            clearAndSendKeysJS(driver.findElement(CheckoutPageSelectors.ZIP_CODE_INPUT), zipCode);
            clearAndSendKeysJS(driver.findElement(CheckoutPageSelectors.CITY_INPUT), city);
            clearAndSendKeysJS(driver.findElement(CheckoutPageSelectors.PHONE_INPUT), phone);

            try {
                System.out.println("📍 Selecting State...");
                WebElement stateSelectEl =
                        wait.until(ExpectedConditions.elementToBeClickable(CheckoutPageSelectors.STATE_DROPDOWN));

                Select stateSelect = new Select(stateSelectEl);
                stateSelect.selectByVisibleText(state);

                System.out.println("✅ State selected: " + state);
            } catch (Exception e) {
                System.out.println("⚠️ Could not select state: " + e.getMessage());
            }

            clickContinueToDelivery();
            Thread.sleep(1500);

            clickContinueToPayment();

        } catch (Exception e) {
            System.out.println("❌ FAILED during shipping step: " + e.getMessage());
            throw new RuntimeException("Failed during shipping address process.", e);
        }
    }


    private boolean isSavedAddressPresent() {
        try {
            WebElement savedBlock = driver.findElement(
                    By.xpath("//*[contains(text(),'Ship To')]/following::div[contains(@class,'t-address-widget')][1]")
            );
            return savedBlock.isDisplayed();
        } catch (Exception ex) {
            return false;
        }
    }


    private void clickContinueToDelivery() {
        System.out.println("▶️ Clicking 'Continue to Delivery Method'...");
        WebElement btn = wait.until(
                ExpectedConditions.elementToBeClickable(CheckoutPageSelectors.CONTINUE_TO_DELIVERY_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        System.out.println("✅ Proceeded to Delivery.");
    }


    private void clickContinueToPayment() {
        System.out.println("▶️ Clicking 'Continue to Payment'...");

        try {
            WebElement btn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(CheckoutPageSelectors.CONTINUE_TO_PAYMENT_BUTTON)
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'center'});", btn);

            wait.until(ExpectedConditions.elementToBeClickable(btn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

            System.out.println("✅ Clicked → Continue to Payment");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    CheckoutPageSelectors.CARD_NUMBER_INPUT
            ));

            System.out.println("✅ PAYMENT fields detected");

        } catch (Exception e) {
            System.out.println("❌ FAILED to open PAYMENT section: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void fillPaymentAndPlaceOrder() {
        try {
            System.out.println("💳 Starting PAYMENT step...");

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(CheckoutPageSelectors.CARD_NUMBER_INPUT),
                    ExpectedConditions.visibilityOfElementLocated(CheckoutPageSelectors.CVV_INPUT)
            ));
            System.out.println("✅ Payment inputs visible (or at least one).");

            java.util.function.BiConsumer<WebElement, String> jsSetValue = (el, val) -> {
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));" +
                            "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                            el, val
                    );
                } catch (Exception ex) {
                }
            };

            WebElement cc = wait.until(ExpectedConditions.visibilityOfElementLocated(CheckoutPageSelectors.CARD_NUMBER_INPUT));
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cc);
                cc.click();
                cc.clear();
                cc.sendKeys("4000060000000006");
                System.out.println("✅ Card number entered via sendKeys.");
            } catch (Exception e1) {
                System.out.println("⚠️ sendKeys failed for card number, trying JS setValue: " + e1.getMessage());
                jsSetValue.accept(cc, "4000060000000006");
                System.out.println("✅ Card number set via JS.");
            }

            // 3) Expiry — try two formats (MM/YYYY then MM/YY)
            WebElement exp = wait.until(ExpectedConditions.visibilityOfElementLocated(CheckoutPageSelectors.EXP_INPUT));
            boolean expOk = false;
            String[] expCandidates = new String[] {"03/2030", "03/30"};
            for (String candidate : expCandidates) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", exp);
                    exp.click();
                    exp.clear();
                    exp.sendKeys(candidate);
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", exp);
                    System.out.println("✅ Expiry tried with: " + candidate);
                    Thread.sleep(300);
                    String got = exp.getAttribute("value");
                    if (got != null && !got.trim().isEmpty()) {
                        expOk = true;
                        break;
                    }
                } catch (Exception ex) {
                    System.out.println("⚠️ expiry attempt failed for " + candidate + " -> " + ex.getMessage());
                }
            }
            if (!expOk) {
                System.out.println("⚠️ Expiry not set by normal attempts — forcing via JS.");
                jsSetValue.accept(exp, "03/2030");
                Thread.sleep(300);
            }

            WebElement cvv = null;
            try {
                cvv = wait.until(ExpectedConditions.presenceOfElementLocated(CheckoutPageSelectors.CVV_INPUT));
            } catch (Exception e) {
                System.out.println("⚠️ CVV field not found by selector: " + e.getMessage());
            }

            boolean cvvFilled = false;
            if (cvv != null) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cvv);
                    cvv.click();
                    Thread.sleep(150);
                    cvv.clear();
                    cvv.sendKeys("7373");
                    Thread.sleep(200);
                    String val = cvv.getAttribute("value");
                    if (val != null && val.replaceAll("\\D","").length() >= 3) {
                        cvvFilled = true;
                        System.out.println("✅ CVV entered via sendKeys, value=" + val);
                    } else {
                        System.out.println("⚠️ sendKeys wrote '" + val + "' (insufficient), trying JS set.");
                    }
                } catch (Exception ex1) {
                    System.out.println("⚠️ sendKeys to CVV failed: " + ex1.getMessage());
                }

                if (!cvvFilled) {
                    try {
                        jsSetValue.accept(cvv, "7373");
                        Thread.sleep(200);
                        String val2 = cvv.getAttribute("value");
                        if (val2 != null && val2.replaceAll("\\D","").length() >= 3) {
                            cvvFilled = true;
                            System.out.println("✅ CVV set via JS, value=" + val2);
                        } else {
                            System.out.println("⚠️ JS set left value='" + val2 + "'.");
                        }
                    } catch (Exception ex2) {
                        System.out.println("⚠️ JS setValue for CVV failed: " + ex2.getMessage());
                    }
                }

                if (!cvvFilled) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", cvv);
                        ((JavascriptExecutor) driver).executeScript("document.activeElement.value = '';");
                        ((JavascriptExecutor) driver).executeScript(
                                "document.activeElement.dispatchEvent(new Event('focus'));");
                        cvv.sendKeys("7373");
                        Thread.sleep(200);
                        String val3 = cvv.getAttribute("value");
                        if (val3 != null && val3.replaceAll("\\D","").length() >= 3) {
                            cvvFilled = true;
                            System.out.println("✅ CVV entered via focus/sendKeys, value=" + val3);
                        } else {
                            System.out.println("⚠️ focus/sendKeys wrote '" + val3 + "' (still insufficient).");
                        }
                    } catch (Exception ex3) {
                        System.out.println("⚠️ focus/sendKeys failed for CVV: " + ex3.getMessage());
                    }
                }
            } else {
                // possible the cvv input is inside an iframe — try scanning iframes for a matching element
                System.out.println("ℹ️ CVV element not present in main DOM — scanning iframes...");
                java.util.List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
                System.out.println("ℹ️ Found " + iframes.size() + " iframes. Trying each...");
                for (int i = 0; i < iframes.size() && !cvvFilled; i++) {
                    try {
                        driver.switchTo().frame(i);
                        java.util.List<WebElement> possible = driver.findElements(CheckoutPageSelectors.CVV_INPUT);
                        if (!possible.isEmpty()) {
                            WebElement fcvv = possible.get(0);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fcvv);
                            fcvv.click();
                            fcvv.clear();
                            fcvv.sendKeys("7373");
                            Thread.sleep(200);
                            String fv = fcvv.getAttribute("value");
                            if (fv != null && fv.replaceAll("\\D","").length() >= 3) {
                                cvvFilled = true;
                                System.out.println("✅ CVV entered inside iframe index " + i + ", value=" + fv);
                            }
                        }
                    } catch (Exception ife) {
                        System.out.println("⚠️ iframe[" + i + "] attempt failed: " + ife.getMessage());
                    } finally {
                        driver.switchTo().defaultContent();
                    }
                }
            }

            if (!cvvFilled) {
                try {
                    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    String path = "test-output/screenshots/cvv_fail_" + System.currentTimeMillis() + ".png";
                    org.apache.commons.io.FileUtils.copyFile(src, new File(path));
                    System.out.println("❌ CVV still not filled — screenshot saved: " + path);
                } catch (Exception ioe) {
                    System.out.println("⚠️ Failed to take screenshot: " + ioe.getMessage());
                }
                throw new RuntimeException("CVV field could not be filled by any strategy.");
            }

            try {
                Thread.sleep(300);
                System.out.println("🛒 Clicking Place Order...");
                WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(CheckoutPageSelectors.PLACE_ORDER_BUTTON));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", placeOrderBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", placeOrderBtn);
                System.out.println("✅✅✅ ORDER SUBMITTED ✅✅✅");
            } catch (Exception pex) {
                System.out.println("❌ Failed clicking Place Order: " + pex.getMessage());
                throw new RuntimeException("Failed clicking Place Order: " + pex.getMessage(), pex);
            }

        } catch (Exception e) {
            System.out.println("❌ FAILED during payment step: " + e.getMessage());
            throw new RuntimeException("Payment process failed.", e);
        }
    }


}
