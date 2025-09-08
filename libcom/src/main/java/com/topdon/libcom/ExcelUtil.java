package com.topdon.libcom;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
import com.topdon.lib.core.common.SharedManager;
import com.topdon.lib.core.config.FileConfig;
import com.topdon.lib.core.db.entity.ThermalEntity;
import com.topdon.lib.core.tools.TimeTool;
import com.topdon.lib.core.tools.UnitTools;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.blankj.utilcode.util.ImageUtils.save;

/**
 * @author: CaiSongL
 * @date: 2023/5/11 15:58
 */
public class ExcelUtil {

    @NonNull
    private static String getTemperature(int index, @NonNull byte[] norTempData, boolean isShowC) {
        int tempValue = (norTempData[2 * index + 1] << 8 & 0xff00) | (norTempData[2 * index] & 0xff);
        float value = tempValue / 64f - 273.15f;
        return UnitTools.showC(value,isShowC);
    }
    @Nullable
    public static String exportExcel(@NonNull String name, int width, int height, @NonNull byte[] norTempData, @Nullable Callback callback) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        boolean isShowC = SharedManager.INSTANCE.getTemperature() == 1;
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        long time = System.currentTimeMillis();
        for (int i = 0; i < height; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                sheet.setColumnWidth(j, 9 * width);
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(getTemperature(index, norTempData,isShowC));
                if (index % 100 == 0 && callback != null) {
                    // [Chinese text]1[Chinese text]1[Chinese text], [Chinese text]100[Chinese text]1[Chinese text]
                    callback.onOneCell(index / 100, width * height / 100);
                }
            }
        }
        try {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File excel = new File(FileConfig.getExcelDir(), name + ".xlsx");
                FileOutputStream fos = new FileOutputStream(excel);
                workbook.write(fos);
                fos.flush();
                fos.close();
                return excel.getAbsolutePath();
            }else {
                String fileName = name + ".xlsx";
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getExcelDir());
                Uri contentUri = MediaStore.Files.getContentUri("external");
                Uri uri = Utils.getApp().getContentResolver().insert(contentUri, values);
                if (uri != null) {
                    OutputStream outputStream = Utils.getApp().getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                        workbook.write(bos);
                        bos.flush();
                        bos.close();
                    }
                    Log.w("[Chinese text]",UriUtils.uri2File(uri).getAbsolutePath());
                    return UriUtils.uri2File(uri).getAbsolutePath();
                }else {
                    return null;
                }
            }
        }catch (Exception e){
            return null;
        }
    }
    @FunctionalInterface
        /**
     * Callback class.
     *
     * Provides callback functionality.
     */
    public interface Callback {
        void onOneCell(int current, int total);
    }

        /**
     * @param listData
     * @return
     */
    public static String exportExcel(ArrayList<ThermalEntity> listData,boolean isPoint) {
        boolean isShowC = SharedManager.INSTANCE.getTemperature() == 1;
        try {
            // [Chinese text]excel xlsx[Chinese text]
            Workbook wb = new XSSFWorkbook();
            // [Chinese text]
            Sheet sheet = wb.createSheet();
            String[] title = {Utils.getApp().getString(R.string.detail_date), Utils.getApp().getString(R.string.chart_temperature_low), Utils.getApp().getString(R.string.chart_temperature_high)};
            if (isPoint){
                title = new String[]{Utils.getApp().getString(R.string.detail_date), Utils.getApp().getString(R.string.chart_temperature)};
            }
            // [Chinese text]
            Row row = sheet.createRow(0);
            // Settings[Chinese text]
            int colNum = title.length;
            CellStyle titleStyle = wb.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER); // [Chinese text]in progress
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font =  wb.createFont();
            font.setBold(true);// [Chinese text]
            titleStyle.setFont(font);
            CellStyle contentStyle = wb.createCellStyle();
            contentStyle.setAlignment(HorizontalAlignment.CENTER); // [Chinese text]in progress
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (int i = 0; i < colNum; i++) {
                sheet.setColumnWidth(i, 20 * 256);  // [Chinese text]20[Chinese text]
                Cell cell1 = row.createCell(i);
                cell1.setCellStyle(titleStyle);
                // [Chinese text]
                cell1.setCellValue(title[i]);
            }
            // [Chinese text]
            for (int rowNum = 0; rowNum < listData.size(); rowNum++) {

                // [Chinese text]rowNum + 1 [Chinese text]Settings[Chinese text]
                row = sheet.createRow(rowNum + 1);
                // Settings[Chinese text]
                row.setHeightInPoints(28f);

                ThermalEntity bean = listData.get(rowNum);

                for (int j = 0; j < title.length; j++) {
                    Cell cell = row.createCell(j);
                    // [Chinese text]title[][Chinese text]
                    if (isPoint){
                        switch (j) {
                            case 0:
                                // [Chinese text]
                                cell.setCellValue(bean.getTime());
                                break;
                            case 1:
                                // temperature
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMinTemp()));
                                break;
                        }
                    }else {
                        switch (j) {
                            case 0:
                                // [Chinese text]
                                cell.setCellValue(bean.getTime());
                                break;
                            case 1:
                                // [Chinese text]low[Chinese text]
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMinTemp()));
                                break;
                            case 2:
                                // [Chinese text]high[Chinese text]
                                cell.setCellStyle(contentStyle);
                                cell.setCellValue(UnitTools.showC(bean.getMaxTemp(),isShowC));
                                break;
                        }
                    }
                }
            }
            String timeStr = listData.isEmpty() ? TimeTool.INSTANCE.showDateSecond() : TimeUtils.millis2String(listData.get(0).getStartTime(), "yyyyMMddHHmmss");
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File excel = new File(FileConfig.getExcelDir(), "TCView_"+ timeStr + ".xlsx");
                FileOutputStream fos = new FileOutputStream(excel);
                wb.write(fos);
                fos.flush();
                fos.close();
                return excel.getAbsolutePath();
            }else {
                String fileName = "TCView_"+timeStr + ".xlsx";
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/xlsx");
//                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, FileConfig.getExcelDir());
                Uri contentUri = MediaStore.Files.getContentUri("external");
                Uri uri = Utils.getApp().getContentResolver().insert(contentUri, values);
                if (uri != null) {
                    OutputStream outputStream = Utils.getApp().getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                        wb.write(bos);
                        bos.flush();
                        bos.close();
                    }
                    Log.w("[Chinese text]",UriUtils.uri2File(uri).getAbsolutePath());
                    return UriUtils.uri2File(uri).getAbsolutePath();
                }else {
                    return null;
                }
            }
        } catch (IOException e) {
            Log.e("ExpressExcle", "exportExcel", e);
            return null;
        }

    }
}
