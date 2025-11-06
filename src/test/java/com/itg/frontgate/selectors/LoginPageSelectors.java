package com.itg.frontgate.selectors;

import org.openqa.selenium.By;

public final class LoginPageSelectors {
    private LoginPageSelectors() {}

    public static final By EMAIL_INPUT =
            By.cssSelector("input[type='email']");

    public static final By PASSWORD_INPUT =
            By.cssSelector("input[type='password']");

    public static final By SIGNIN_BUTTON =
            By.cssSelector("button[data-analytics-name='login']");

    public static final By ERROR_EMAIL =
            By.cssSelector("#errorMessage-email"); 

    public static final By ERROR_PASSWORD =
            By.cssSelector("#errorMessage-password");

    public static final By ERROR_GENERAL =
            By.cssSelector("div.u-color-error, div.c-login__submition-error"); 

    public static final By ACCOUNT_BUTTON =
            By.cssSelector("button.t-header__my-account, div.t-header__my-account, span.t-header__inner-button");

    public static final By MY_ACCOUNT_LINK =
    	    By.xpath("//*[contains(@class,'my-account') and contains(normalize-space(text()),'My Account')]");

    public static final By WELCOME_TEXT =
            By.xpath("//div[contains(@class, 'c-list-tile__content-welcome') and contains(text(), 'Welcome')]");
}