package com.example.demo.Ultils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.*;

public class ExcelBase {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    // --- EXPORT GENERIC ---
    public static <T> ByteArrayInputStream exportToExcel(
            List<T> data,
            LinkedHashMap<String, String> columnMap,
            Map<String, Map<Object, String>> valueMappings,
            String sheetName) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

            // 1. Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 2. Tạo Header
            Row headerRow = sheet.createRow(0);
            int colIdx = 0;
            for (String header : columnMap.keySet()) {
                Cell cell = headerRow.createCell(colIdx++);
                cell.setCellValue(header);
                cell.setCellStyle(headerStyle);
            }

            // 2b. Resolve field (reflection) MỘT LẦN cho mỗi cột, thay vì lặp lại cho từng cell của từng dòng
            List<String> fieldNames = new ArrayList<>(columnMap.values());
            List<Field[]> fieldChains = new ArrayList<>(fieldNames.size());
            if (!data.isEmpty()) {
                Class<?> itemClass = data.get(0).getClass();
                for (String fieldName : fieldNames) {
                    fieldChains.add(resolveFieldChain(itemClass, fieldName));
                }
            }

            // 3. Điền dữ liệu
            int rowIdx = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                int cellIdx = 0;
                for (String fieldName : columnMap.values()) {
                    Cell cell = row.createCell(cellIdx++);
                    Object value = getFieldValue(item, fieldName);

                    if (valueMappings != null && valueMappings.containsKey(fieldName)) {
                        String displayValue = "";
                        if (value != null) {
                            displayValue = valueMappings.get(fieldName).get(value);
                        }

                        cell.setCellValue(displayValue != null ? displayValue : "");
                    } else {
                        // ... (Logic xử lý Date, Number như cũ) ...
                        if (value instanceof Date) {
                            cell.setCellValue(sdf.format((Date) value));
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }
            }

            for (int i = 0; i < columnMap.size(); i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 15 * 256) sheet.setColumnWidth(i, 15 * 256);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi Export: " + e.getMessage());
        }
    }

    private static Field[] resolveFieldChain(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        String[] parts = fieldName.split("\\.");
        Field[] chain = new Field[parts.length];
        Class<?> currentClass = clazz;
        for (int i = 0; i < parts.length; i++) {
            Field field = getDeclaredField(currentClass, parts[i]);
            field.setAccessible(true); // Cho phép truy cập vào field private
            chain[i] = field;
            currentClass = field.getType();
        }
        return chain;
    }

    private static Object getFieldValue(Object obj, String fieldName) throws Exception {
        if (obj == null) return null;

        for (String part : fieldName.split("\\.")) {
            Field field = getDeclaredField(obj.getClass(), part);
            field.setAccessible(true); // Cho phép truy cập vào field private
            obj = field.get(obj);
            if (obj == null) return null;
        }
        return obj;
    }

    /**
     * Tìm kiếm Field trong Class, kể cả các Class cha (Inheritance)
     */
    private static Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // Nếu không tìm thấy ở class hiện tại, tìm ở class cha
            if (clazz.getSuperclass() != null) {
                return getDeclaredField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}
