package com.topdon.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class UTF8StringUtils {

    /**
     * @param @return parameter
     * @return String    Returntype
     * @throws
     * @Title readByUtf8WithBom
     * @Description 普通方式读取 txtfile，如果用记事本save会存在bomformat
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
                while ((txt = bf.readLine()) != null) { // 读取file
                    /* 判断文本file里area的内容是否合法 平台系统中定义 每个敏感词后加上end标志“|1” */
                    txt = txt.trim();// 去掉收尾的空格
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
     * @param @return parameter
     * @return String    Returntype
     * @throws
     * @Title readByUtf8WithOutBom
     * @Description 读取 txtfile，如果存在bomformat 则去掉
     */
    public static String readByUtf8WithOutBom(String path) {
        File file = new File(path);
        FileInputStream in;
        try {
            if (file.exists() && file.isFile()) {
                in = new FileInputStream(file);
                BufferedReader bf = new BufferedReader(new UnicodeReader(in, "utf-8"));
                String txt = "";
                while ((txt = bf.readLine()) != null) { // 读取file
                    /* 判断文本file里area的内容是否合法 平台系统中定义 每个敏感词后加上end标志“|1” */
                    txt = txt.trim();// 去掉收尾的空格
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
