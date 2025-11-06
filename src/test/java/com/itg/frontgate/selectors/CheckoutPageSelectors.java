package com.itg.frontgate.selectors;

import org.openqa.selenium.By;

public class CheckoutPageSelectors {

    public static final By SHIPPING_HEADER =
            By.xpath("//span[contains(@class, 'c-checkout-accordion__header-step-text') and normalize-space()='Shipping']");

    public static final By FIRST_NAME_INPUT  = By.id("fName");
    public static final By LAST_NAME_INPUT   = By.id("lName");
    public static final By STREET_ADDRESS_INPUT = By.xpath("//input[@aria-label='Street Address*']");
    public static final By ZIP_CODE_INPUT    = By.id("zipbox");
    public static final By CITY_INPUT        = By.id("citybox");
    public static final By PHONE_INPUT       = By.id("phone1box");

    public static final By STATE_DROPDOWN    = By.cssSelector("select#region");

    public static final By CONTINUE_TO_DELIVERY_BUTTON = By.id("shipping-next-btn");
    public static final By CONTINUE_TO_PAYMENT_BUTTON  = By.id("nextBtn");

    // ✅ PAYMENT
    public static final By CARD_NUMBER_INPUT = By.id("account-cc");
    public static final By EXP_INPUT         = By.id("exp-date");
    public static final By CVV_INPUT         = By.id("cvv");

    public static final By PLACE_ORDER_BUTTON =
            By.cssSelector(".c-place-order__btn");
}
