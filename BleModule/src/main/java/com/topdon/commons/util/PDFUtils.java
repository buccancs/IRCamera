package com.topdon.commons.util;

/**
 * PDFUtils class.
 * 
 * Provides functionality for pdfutils operations.
 */
public class PDFUtils {

    /**
     * [Chinese text]PDF[Chinese text]
     * // +, [Chinese text], /, ?, %, #, &, =
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
