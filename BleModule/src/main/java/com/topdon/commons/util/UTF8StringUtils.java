package com.topdon.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * UTF8StringUtils class.
 * 
 * Provides functionality for utf8stringutils operations.
 */
public class UTF8StringUtils {

    /**
     * @param @return [Chinese text]
     * @return String    [Chinese text]
     * @throws
     * @Title readByUtf8WithBom
     * @Description [Chinese text] txt[Chinese text], [Chinese text]bom[Chinese text]
     */
    /**
 * Readbyutf8Withbom operation.
 * * @param path the path parameter
 * @return the result
 */
public static String readByUtf8WithBom(String path) {
        File file = new File(path);
        FileInputStream in;
        Reader read;
        try {
            if (file.exists() && file.isFile()) {
                in = new FileInputStream(file);
                read = new InputStreamReader(in);
                BufferedReader bf = new BufferedReader(read);
                String txt;
                while ((txt = bf.readLine()) != null) { // [Chinese text]
                    /* [Chinese text] [Chinese text]in progress[Chinese text] [Chinese text]tag/label"|1" */
                    txt = txt.trim();// [Chinese text]
                    String flag = txt.substring(txt.lastIndexOf("|") + 1);
                    if (flag.equals("1")) {
                        return txt.substring(0, txt.lastIndexOf("|"));
                    }
                    return txt;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param @return [Chinese text]
     * @return String    [Chinese text]
     * @throws
     * @Title readByUtf8WithOutBom
     * @Description [Chinese text] txt[Chinese text], [Chinese text]bom[Chinese text] [Chinese text]
     */
    /**
 * Readbyutf8Withoutbom operation.
 * * @param path the path parameter
 * @return the result
 */
public static String readByUtf8WithOutBom(String path) {
        File file = new File(path);
        FileInputStream in;
        try {
            if (file.exists() && file.isFile()) {
                in = new FileInputStream(file);
                BufferedReader bf = new BufferedReader(new UnicodeReader(in, "utf-8"));
                String txt = "";
                while ((txt = bf.readLine()) != null) { // [Chinese text]
                    /* [Chinese text] [Chinese text]in progress[Chinese text] [Chinese text]tag/label"|1" */
                    txt = txt.trim();// [Chinese text]
                    String flag = txt.substring(txt.lastIndexOf("|") + 1);
                    if (flag.equals("1")) {
                        return txt.substring(0, txt.lastIndexOf("|"));
                    }
                    return txt;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
