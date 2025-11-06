package com.itg.frontgate.selectors;

import org.openqa.selenium.By;

public class CartPageSelectors {

    public static final By CONTINUE_SHOPPING_BUTTON =
        By.cssSelector("button[data-analytics-name='continue_shopping']");

    public static final By COOKIE_CLOSE_BUTTON =
        By.cssSelector("div.c-cookie-banner button, button#onetrust-accept-btn-handler");

    public static final By GENERIC_POPUP_CLOSE_BUTTON =
        By.cssSelector("div[class*='granify-close-button'], [id*='close-button'], [aria-label*='close' i]");

    public static final By PRODUCT_CARDS =
        By.cssSelector(".c-universal-product-item-title, .c-product__title, a.c-universal-product-item-title-link");

    // Selectors for product options (Color, Size)
    public static final By COLOR_OPTION_BUTTONS =
        By.cssSelector("div.color-item button.c-universal-options__option-swatch");

    public static final By SIZE_DROPDOWN_TRIGGER =
        By.xpath("//div[normalize-space()='Select Size']");

    public static final By SIZE_OPTIONS_CONTAINER = 
        By.cssSelector("div.c-universal-options__accordion-option-list");

    public static final By SIZE_OPTION_BUTTONS =
        By.cssSelector("div.c-universal-options__accordion-option-list button.option-item");

    public static final By ADD_TO_CART_BUTTON =
        By.cssSelector("button[data-analytics-name='add_to_cart'], button.c-universal-add-to-cart");
    
    public static final By CART_BADGE_COUNT =
        By.cssSelector("span.t-header-badge");

    public static final By VIEW_CART_BUTTON =
        By.cssSelector("button.view-cart-btn.btn.btn-primary");

    public static final By CHECKOUT_NOW_BUTTON =
        By.cssSelector("button.c-checkout-buttons__checkout.btn.btn-primary");

    public static final By CHECKOUT_EMAIL_INPUT = By.cssSelector("form#signin-form input#email");

    public static final By CHECKOUT_PASSWORD_INPUT = By.cssSelector("form#signin-form input#password");

    public static final By SIGN_IN_TO_CHECKOUT_BUTTON = By.cssSelector("form#signin-form button[type='submit']");
}