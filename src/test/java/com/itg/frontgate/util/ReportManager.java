package com.itg.frontgate.util;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportManager {
    private static ExtentReports extent;
    private static ExtentTest test;

    // 🔹 إنشاء التقرير مرة واحدة فقط
    public static void initReport() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = "test-output/Frontgate_ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("Frontgate Automation Report");
            spark.config().setReportName("Frontgate Login Data-Driven Test Results");
            spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Tester", "Ahmad Hanani");
            extent.setSystemInfo("Project", "Frontgate QA Automation");
        }
    }

    // 🔹 إنشاء اختبار جديد داخل التقرير
    public static ExtentTest createTest(String testName, String description) {
        test = extent.createTest(testName, description);
        return test;
    }

    // 🔹 تسجيل نجاح
    public static void logPass(String message) {
        if (test != null) test.pass(message);
    }

    // 🔹 تسجيل فشل
    public static void logFail(String message) {
        if (test != null) test.fail(message);
    }

    // 🔹 تسجيل ملاحظة
    public static void logInfo(String message) {
        if (test != null) test.info(message);
    }

    // 🔹 إغلاق التقرير بعد نهاية كل Suite
    public static void flushReport() {
        if (extent != null) extent.flush();
    }
}
