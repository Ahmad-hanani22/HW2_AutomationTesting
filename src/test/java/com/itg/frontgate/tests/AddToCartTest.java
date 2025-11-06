package com.itg.frontgate.tests;

import com.aventstack.extentreports.ExtentTest;
import com.itg.frontgate.base.BaseTest;
import com.itg.frontgate.pages.CartPage;
import com.itg.frontgate.pages.CheckoutPage; 
import com.itg.frontgate.util.ExcelUtil;
import com.itg.frontgate.util.ReportManager;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    private static final String EXCEL_PATH = "src/test/resources/testdata/loginData.xlsx";

    @DataProvider(name = "productsData")
    public Object[][] productsData() {
        return ExcelUtil.readProducts(EXCEL_PATH, "Products");
    }

    @DataProvider(name = "addressData")
    public Object[][] addressData() {
        return ExcelUtil.readAddress(EXCEL_PATH, "Address");
    }

    @Test(dataProvider = "productsData")
    public void endToEndProductCheckout(
            String productName, int qty, String size, String color, String runFlag, int productRowIndex
    ) {

        ExtentTest test = ReportManager.createTest(
                "E2E Checkout: " + productName,
                "Full flow → Product → Cart → Checkout → Shipping → Payment"
        );

        if (runFlag == null || runFlag.trim().isEmpty()) {
            throw new SkipException("RunFlag for product is empty, skipping row " + productRowIndex);
        }

        CartPage cartPage = new CartPage(driver);
        driver.get("https://www.frontgate.com/");

        boolean productFound = cartPage.findAndClickProduct(productName);
        Assert.assertTrue(productFound, "Product '" + productName + "' was not found.");

        cartPage.selectColorIfProvided(color);
        cartPage.selectSizeIfProvided(size);
        cartPage.clickAddToCart();
        cartPage.clickViewCart();
        cartPage.clickCheckoutNow();

        cartPage.loginOnCheckoutPageIfRequired("ahmadj7hanani0@gmail.com", "0569630981Aa$");

        Object[][] addressRows = ExcelUtil.readAddress(EXCEL_PATH, "Address");

        if (addressRows.length == 0) {
            Assert.fail("Address sheet is empty or could not be read.");
        }

        Object[] firstAddress = addressRows[0];
        String firstName      = (String) firstAddress[0];
        String lastName       = (String) firstAddress[1];
        String streetAddress  = (String) firstAddress[2];
        String zipCode        = (String) firstAddress[3];
        String city           = (String) firstAddress[4];
        String state          = (String) firstAddress[5];
        String phone          = (String) firstAddress[6];

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillShippingAddressAndContinue(
                firstName, lastName, streetAddress,
                zipCode, city, state, phone
        );

        checkoutPage.fillPaymentAndPlaceOrder();

        test.pass("✅✅✅ FULL FLOW COMPLETED SUCCESSFULLY! ✅✅✅");

        System.out.println("⏸️ Pausing for final page...");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(true, "E2E completed");
    }
}
