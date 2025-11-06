package com.itg.frontgate.tests;

import com.aventstack.extentreports.ExtentTest;
import com.itg.frontgate.base.BaseTest;
import com.itg.frontgate.pages.CheckoutPage;
import com.itg.frontgate.util.ExcelUtil;
import com.itg.frontgate.util.ReportManager;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    private static final String EXCEL_PATH = "src/test/resources/testdata/loginData.xlsx";
    private static final String SHEET_NAME = "Address";

    @DataProvider(name = "addressData")
    public Object[][] addressData() {
        try {
            String excelPath = System.getProperty("user.dir") + "/src/test/resources/testdata/loginData.xlsx";
            return ExcelUtil.readAddress(excelPath, SHEET_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    @Test(dataProvider = "addressData")
    public void fillAddressAndPay(
            String firstName, String lastName, String streetAddress,
            String zipCode, String city, String state, String phone,
            String runFlag, int rowIndex) {

        ExtentTest test = ReportManager.createTest(
                "FULL CHECKOUT",
                "Fill shipping → Continue → Fill Payment → Place Order"
        );

        if (runFlag == null || runFlag.trim().isEmpty()) {
            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "SKIPPED");
            test.skip("RunFlag empty, skipping row: " + rowIndex);
            throw new SkipException("RunFlag empty");
        }

        try {
            CheckoutPage checkoutPage = new CheckoutPage(driver);

            checkoutPage.fillShippingAddressAndContinue(
                    firstName, lastName, streetAddress,
                    zipCode, city, state, phone
            );

            checkoutPage.fillPaymentAndPlaceOrder();

            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "PASS");
            test.pass("✅ FULL CHECKOUT DONE!");
            Assert.assertTrue(true);

        } catch (Exception e) {
            ExcelUtil.writeResult(EXCEL_PATH, SHEET_NAME, rowIndex, "ERROR");
            test.fail("❌ CHECKOUT FAILED: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
