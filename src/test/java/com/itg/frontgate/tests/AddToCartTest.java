package com.itg.frontgate.tests;

import com.itg.frontgate.base.BaseTest;
import com.itg.frontgate.pages.CartPage;
import com.itg.frontgate.util.ExcelUtil;
import com.itg.frontgate.util.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    private static final String EXCEL_PATH = "src/test/resources/testdata/loginData.xlsx";

    @DataProvider(name = "productsData")
    public Object[][] productsData() {
        try {
            // 🔥 تعديل: استخدام المسار المطلق لضمان العثور على الملف
            String excelPath = System.getProperty("user.dir") + "/src/test/resources/testdata/loginData.xlsx";
            return ExcelUtil.readProducts(excelPath, "Products");
        } catch (Exception e) {
            System.out.println("🔥🔥🔥 ERROR IN 'productsData' PROVIDER: " + e.getMessage());
            e.printStackTrace(); // هذه أهم طباعة، ستظهر الخطأ بالتفصيل
            return new Object[0][0]; 
        }
    }


    @Test(dataProvider = "productsData")
    public void addProductsToCart(
            String productName,
            int qty,
            String size,
            String color,
            String runFlag,
            int rowIndex
    ) {
        ExtentTest test = ReportManager.createTest("Add to Cart - " + productName,
                "Verify product is added and cart badge updates.");

        if (runFlag == null || runFlag.trim().isEmpty()) {
            ExcelUtil.writeResult(EXCEL_PATH, "Products", rowIndex, "SKIPPED");
            test.skip("RunFlag is empty -> skipping row " + rowIndex);
            throw new SkipException("RunFlag is empty");
        }

        try {
            CartPage cart = new CartPage(driver);

            driver.get("https://www.frontgate.com/");
            boolean productFound = cart.findAndClickProduct(productName);
            Assert.assertTrue(productFound, "Product '" + productName + "' was not found.");
            
            cart.selectColorIfProvided(color);
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            cart.selectSizeIfProvided(size);
            cart.clickAddToCart();

            System.out.println("VERIFYING: Checking for success message...");
            String successMsg = cart.getSuccessMessage();
            boolean isSuccessMessageCorrect = !successMsg.isEmpty() && successMsg.toLowerCase().contains("item added to cart");
            Assert.assertTrue(isSuccessMessageCorrect, 
                "VALIDATION FAILED: The success message did not appear correctly. Found: '" + successMsg + "'");
            test.pass("✅ VERIFIED: Success message appeared correctly: '" + successMsg + "'");

            System.out.println("VERIFYING: Checking cart badge count...");
            String cartBadgeCount = cart.getCartBadgeCount();
            
            Assert.assertEquals(cartBadgeCount, "1",
                "VALIDATION FAILED: Cart badge count did not update to '1'. Found: '" + cartBadgeCount + "'");
            test.pass("✅ VERIFIED: Cart badge count updated correctly to '1'.");

            ExcelUtil.writeResult(EXCEL_PATH, "Products", rowIndex, "PASS");

        } catch (AssertionError ae) {
            test.fail("❌ ASSERTION FAILED: " + ae.getMessage());
            ExcelUtil.writeResult(EXCEL_PATH, "Products", rowIndex, "FAIL");
            throw ae; 
        } catch (Exception e) {
            test.fail("💥 UNEXPECTED ERROR: " + e.getMessage());
            ExcelUtil.writeResult(EXCEL_PATH, "Products", rowIndex, "ERROR");
            throw new RuntimeException("Test failed due to an unexpected error.", e);
        }
    }
}
