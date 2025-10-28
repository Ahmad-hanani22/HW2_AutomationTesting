package com.itg.frontgate.util;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static Object[][] readSheet(String path, String sheetName) {
        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            int lastRow = sh.getLastRowNum();
            List<Object[]> rows = new ArrayList<>();
     
            
            
            for (int r = 1; r <= lastRow; r++) {
                Row row = sh.getRow(r);
                if (row == null) continue;

                String email = getCellString(row.getCell(0));
                String pass = getCellString(row.getCell(1));

                rows.add(new Object[]{email, pass});
            }

            Object[][] data = new Object[rows.size()][];
            for (int i = 0; i < rows.size(); i++) data[i] = rows.get(i);
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Failed reading Excel: " + e.getMessage(), e);
        }
    }

    private static String getCellString(Cell c) {
        if (c == null) return "";
        switch (c.getCellType()) {
            case STRING: return c.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long)c.getNumericCellValue()).trim();
            case BOOLEAN: return String.valueOf(c.getBooleanCellValue()).trim();
            default: return "";
        }
    }
}
