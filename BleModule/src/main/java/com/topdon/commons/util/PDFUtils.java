package com.topdon.commons.util;

/**
 * @Desc PDF
 * @ClassName PDFUtils
 * @Email 616862466@qq.com
* @Author
 * @Date 2023/3/1 9:46
 */

public class PDFUtils {

 /**
 *
 * @param name pdfname
 * @return String
 */
 public static String getPdfName(String name) {
 name = name.replace('+', '-');
 name = name.replace(' ', '-');
 name = name.replace('/', '-');
 name = name.replace('?', '-');
 name = name.replace('%', '-');
 name = name.replace('#', '-');
 name = name.replace('&', '-');
 name = name.replace('=', '-');
 name = name.replace('\\', '-');
 name = name.replace(':', '-');
 name = name.replace('*', '-');
 name = name.replace('|', '-');
 name = name.replace('<', '-');
 name = name.replace('>', '-');
 name = name.replace('"', '-');
 return name;
 }
}
