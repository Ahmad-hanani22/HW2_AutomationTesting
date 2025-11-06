package com.itg.frontgate.util;

import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.*;

public class ExcelUtil {

    private static void ensureFileExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            throw new RuntimeException("Excel file not found: " + path);
        }
    }

    @SuppressWarnings("deprecation")
	private static String getCellString(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private static Map<String, Integer> mapHeaderIndexes(Row header) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell c : header) {
            String name = c.getStringCellValue().trim().toLowerCase();
            map.put(name, c.getColumnIndex());
        }
        return map;
    }

    // ✅ قراءة شيت المنتجات (ProductName, Qty, Size, Color, RunFlag, Result)
    public static Object[][] readProducts(String path, String sheetName) {
    	System.out.println("🧾 Trying to read Excel: " + path + " | Sheet: " + sheetName);

        ensureFileExists(path);

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row header = sh.getRow(0);
            if (header == null) {
                throw new RuntimeException("Header row missing in sheet: " + sheetName);
            }

            Map<String, Integer> cols = mapHeaderIndexes(header);

            int nameCol  = cols.getOrDefault("productname", -1);
            int qtyCol   = cols.getOrDefault("qty", -1);
            int sizeCol  = cols.getOrDefault("size", -1);
            int colorCol = cols.getOrDefault("color", -1);
            int flagCol  = cols.getOrDefault("runflag", -1);

            if (nameCol < 0 || qtyCol < 0 || flagCol < 0)
                throw new RuntimeException("Required columns missing in sheet.");

            int lastRow = sh.getLastRowNum();
            List<Object[]> rows = new ArrayList<>();

            for (int r = 1; r <= lastRow; r++) {
                Row row = sh.getRow(r);
                if (row == null) continue;

                String productName = getCellString(row.getCell(nameCol));
                String qtyStr = getCellString(row.getCell(qtyCol));
                String size = (sizeCol >= 0) ? getCellString(row.getCell(sizeCol)) : "";
                String color = (colorCol >= 0) ? getCellString(row.getCell(colorCol)) : "";
                String runFlag = getCellString(row.getCell(flagCol));

                int qty = 1;
                try { qty = Integer.parseInt(qtyStr); } catch (Exception ignored) {}

                rows.add(new Object[]{productName, qty, size, color, runFlag, r});
            }

            Object[][] data = new Object[rows.size()][];
            for (int i = 0; i < rows.size(); i++) data[i] = rows.get(i);
            return data;

        } catch (Exception e) {
            throw new RuntimeException("Failed reading Products sheet: " + e.getMessage(), e);
        }
    }

 // ✅ قراءة شيت العناوين (Address Sheet)
    public static Object[][] readAddress(String path, String sheetName) {
        System.out.println("🧾 Trying to read Excel: " + path + " | Sheet: " + sheetName);
        ensureFileExists(path);

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Row header = sh.getRow(0);
            Map<String, Integer> cols = mapHeaderIndexes(header);

            // التأكد من وجود الأعمدة المطلوبة
            int firstNameCol = cols.getOrDefault("firstname", -1);
            int lastNameCol  = cols.getOrDefault("lastname", -1);
            int addressCol   = cols.getOrDefault("streetaddress", -1);
            int zipCol       = cols.getOrDefault("zipcode", -1);
            int cityCol      = cols.getOrDefault("city", -1);
            int stateCol     = cols.getOrDefault("state", -1);
            int phoneCol     = cols.getOrDefault("phone", -1);
            int flagCol      = cols.getOrDefault("runflag", -1);

            if (firstNameCol < 0 || lastNameCol < 0 || addressCol < 0 || zipCol < 0 || cityCol < 0 || stateCol < 0 || phoneCol < 0)
                throw new RuntimeException("Required address columns are missing in the sheet.");

            List<Object[]> rows = new ArrayList<>();
            for (int r = 1; r <= sh.getLastRowNum(); r++) {
                Row row = sh.getRow(r);
                if (row == null) continue;

                rows.add(new Object[]{
                    getCellString(row.getCell(firstNameCol)),
                    getCellString(row.getCell(lastNameCol)),
                    getCellString(row.getCell(addressCol)),
                    getCellString(row.getCell(zipCol)),
                    getCellString(row.getCell(cityCol)),
                    getCellString(row.getCell(stateCol)),
                    getCellString(row.getCell(phoneCol)),
                    getCellString(row.getCell(flagCol)),
                    r 
                });
            }
            return rows.toArray(new Object[0][]);

        } catch (Exception e) {
            throw new RuntimeException("Failed reading Address sheet: " + e.getMessage(), e);
        }
    }
    
    
 // ✅ قراءة شيت بسيط (مثل Users) — Email, Password, RunFlag
    public static Object[][] readSheet(String path, String sheetName) {
        ensureFileExists(path);

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row header = sh.getRow(0);
            if (header == null) {
                throw new RuntimeException("Header row missing in sheet: " + sheetName);
            }

            Map<String, Integer> cols = mapHeaderIndexes(header);

            int emailCol = cols.getOrDefault("email", -1);
            int passCol = cols.getOrDefault("password", -1);
            int flagCol = cols.getOrDefault("runflag", -1);

            if (emailCol < 0 || passCol < 0 || flagCol < 0)
                throw new RuntimeException("Required columns missing in sheet: Email, Password, RunFlag");

            int lastRow = sh.getLastRowNum();
            List<Object[]> rows = new ArrayList<>();

            for (int r = 1; r <= lastRow; r++) {
                Row row = sh.getRow(r);
                if (row == null) continue;

                String email = getCellString(row.getCell(emailCol));
                String password = getCellString(row.getCell(passCol));
                String runFlag = getCellString(row.getCell(flagCol));

                rows.add(new Object[]{email, password, runFlag, r});
            }

            Object[][] data = new Object[rows.size()][];
            for (int i = 0; i < rows.size(); i++) data[i] = rows.get(i);
            return data;

        } catch (Exception e) {
            throw new RuntimeException("Failed reading Users sheet: " + e.getMessage(), e);
        }
    }

    
    
    
    
    
    
    
    
    
    // ✅ كتابة النتيجة في عمود "Result"
    public static void writeResult(String path, String sheetName, int rowIndex, String result) {
        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) return;

            Row header = sh.getRow(0);
            Map<String, Integer> cols = mapHeaderIndexes(header);
            int resultCol = cols.getOrDefault("result", -1);
            if (resultCol < 0) resultCol = header.getLastCellNum();

            Row row = sh.getRow(rowIndex);
            if (row == null) row = sh.createRow(rowIndex);

            Cell c = row.getCell(resultCol);
            if (c == null) c = row.createCell(resultCol);
            c.setCellValue(result);

            try (FileOutputStream fos = new FileOutputStream(path)) {
                wb.write(fos);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Failed writing result: " + e.getMessage());
        }
    }
}
