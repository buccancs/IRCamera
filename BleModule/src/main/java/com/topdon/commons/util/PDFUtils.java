package com.topdon.commons.util;

/**
 * PDFUtils class.
 * 
 * Provides functionality for pdfutils operations.
 */
public class PDFUtils {

    /**
     * 处理PDF特殊符号打不开的问题
     * // +，空格，/，?，%，#，&，=
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
