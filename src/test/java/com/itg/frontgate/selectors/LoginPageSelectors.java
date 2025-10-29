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

    //رسائل الخطأ الثلاثة الممكنة
    public static final By ERROR_EMAIL =
            By.cssSelector("#errorMessage-email"); // البريد غير صالح أو فارغ

    public static final By ERROR_PASSWORD =
            By.cssSelector("#errorMessage-password"); // الباسورد فارغ

    public static final By ERROR_GENERAL =
            By.cssSelector("div.u-color-error"); // البريد صحيح لكن الباسورد خطأ

    public static final By ACCOUNT_BUTTON =
            By.cssSelector("button.t-header__my-account, div.t-header__my-account, span.t-header__inner-button");

    public static final By MY_ACCOUNT_LINK =
    	    By.xpath("//*[contains(@class,'my-account') and contains(normalize-space(text()),'My Account')]");


    public static final By WELCOME_TEXT =
            By.xpath("//*[contains(text(),'Welcome,')]");




}
