package com.itg.frontgate.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ExcelUtil {

    // يعيد مصفوفة: {email, password, runFlag, rowIndex}
    public static Object[][] readSheet(String path, String sheetName) {
        ensureFileExists(path);

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            // تحديد أعمدة العناوين ديناميكيًا
            Row header = sh.getRow(0);
            if (header == null) {
                throw new RuntimeException("Header row (row 0) is missing in sheet: " + sheetName);
            }

            Map<String, Integer> colIndex = mapHeaderIndexes(header);

            Integer emailCol = colIndex.getOrDefault("email", -1);
            Integer passCol  = colIndex.getOrDefault("password", -1);
            Integer flagCol  = colIndex.getOrDefault("runflag", -1);
            Integer resCol   = colIndex.get("result"); // قد يكون null، سننشئه عند الكتابة إن لزم

            if (emailCol < 0 || passCol < 0 || flagCol < 0) {
                throw new RuntimeException("Required columns not found. Expecting headers: Email, Password, RunFlag (Result optional).");
            }

            List<Object[]> rows = new ArrayList<>();
            int lastRow = sh.getLastRowNum();

            for (int r = 1; r <= lastRow; r++) { // يبدأ من الصف الثاني (بعد العناوين)
                Row row = sh.getRow(r);
                if (row == null) continue;

                String email    = getCellString(row.getCell(emailCol));
                String password = getCellString(row.getCell(passCol));
                String runFlag  = getCellString(row.getCell(flagCol));

                // نعيد كل الصفوف؛ التحكم بالتنفيذ يتم داخل @Test (Skip إذا flag فارغ)
                rows.add(new Object[]{ email, password, runFlag, r });
            }

            Object[][] data = new Object[rows.size()][];
            for (int i = 0; i < rows.size(); i++) data[i] = rows.get(i);
            return data;

        } catch (Exception e) {
            throw new RuntimeException("Failed reading Excel: " + e.getMessage(), e);
        }
    }

    public static void writeResult(String path, String sheetName, int rowIndex, String result) {
        ensureFileExists(path);

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            // تأكد أن صف العناوين موجود
            Row header = sh.getRow(0);
            if (header == null) header = sh.createRow(0);

            // ابحث عن عمود Result، وإن لم يوجد أنشئه كنهاية الأعمدة
            Integer resultCol = findColumnIndexIgnoreCase(header, "Result");
            if (resultCol == null) {
                resultCol = (int) (header.getLastCellNum() == -1 ? 0 : header.getLastCellNum());
                Cell resultHeaderCell = header.createCell(resultCol);
                resultHeaderCell.setCellValue("Result");
            }

            // اكتب النتيجة في الصف المحدد
            Row row = sh.getRow(rowIndex);
            if (row == null) row = sh.createRow(rowIndex);
            Cell cell = row.getCell(resultCol);
            if (cell == null) cell = row.createCell(resultCol);
            cell.setCellValue(result);

            // حفظ الملف
            try (FileOutputStream fos = new FileOutputStream(path)) {
                wb.write(fos);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed writing result to Excel (row " + rowIndex + "): " + e.getMessage(), e);
        }
    }

    // ----------------- مساعدات -----------------

    private static Map<String, Integer> mapHeaderIndexes(Row header) {
        Map<String, Integer> map = new HashMap<>();
        short last = header.getLastCellNum();
        for (int c = 0; c < last; c++) {
            Cell cell = header.getCell(c);
            String name = (cell == null) ? "" : cell.getStringCellValue();
            if (name != null) {
                String key = name.trim().toLowerCase(Locale.ROOT);
                if (!key.isEmpty()) {
                    map.put(key, c);
                }
            }
        }
        return map;
    }

    private static Integer findColumnIndexIgnoreCase(Row header, String colName) {
        String target = colName.trim().toLowerCase(Locale.ROOT);
        short last = header.getLastCellNum();
        for (int c = 0; c < last; c++) {
            Cell cell = header.getCell(c);
            String name = (cell == null) ? "" : cell.getStringCellValue();
            if (name != null && name.trim().toLowerCase(Locale.ROOT).equals(target)) {
                return c;
            }
        }
        return null;
    }

    private static String getCellString(Cell c) {
        if (c == null) return "";
        switch (c.getCellType()) {
            case STRING:  return c.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(c)) {
                    return c.getDateCellValue().toString();
                }
                // حفظ الأرقام بدون كسور إذا كانت صحيحة
                double v = c.getNumericCellValue();
                long lv = (long) v;
                return (v == lv) ? String.valueOf(lv).trim() : String.valueOf(v).trim();
            case BOOLEAN: return String.valueOf(c.getBooleanCellValue()).trim();
            case FORMULA:
                try { return c.getStringCellValue().trim(); }
                catch (Exception ignored) { return String.valueOf(c.getNumericCellValue()).trim(); }
            default:      return "";
        }
    }

    private static void ensureFileExists(String path) {
        if (!Files.exists(Paths.get(path))) {
            throw new RuntimeException("Excel file not found at: " + path);
        }
    }
}
