package com.topdon.lib.core.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.LanguageUtils;
import com.topdon.lib.core.BaseApplication;
import com.topdon.lib.core.BuildConfig;

import java.util.HashMap;
import java.util.Locale;


/**
 * @author YanLu
 * @since 17/5/12
 */

public class AppLanguageUtils {

    private static HashMap<String, Locale> mAllLanguages = new HashMap<String, Locale>(14) {{
        put(ConstantLanguages.ZH_CN, Locale.SIMPLIFIED_CHINESE);
        put(ConstantLanguages.ZH_TW, Locale.TRADITIONAL_CHINESE);
        put(ConstantLanguages.ENGLISH, Locale.ENGLISH);
        put(ConstantLanguages.GERMAN, Locale.GERMANY);
        put(ConstantLanguages.JA, Locale.JAPAN);
        put(ConstantLanguages.FR, Locale.FRANCE);
        put(ConstantLanguages.IT, Locale.ITALY);
        put(ConstantLanguages.RU, new Locale(ConstantLanguages.RU, "RU"));
        put(ConstantLanguages.PT, new Locale(ConstantLanguages.PT, "PT"));
        put(ConstantLanguages.ES, new Locale(ConstantLanguages.ES, "ES"));
        put(ConstantLanguages.PL, new Locale(ConstantLanguages.PL, "PL"));
        put(ConstantLanguages.CS, new Locale(ConstantLanguages.CS, "CS"));
        put(ConstantLanguages.UK, new Locale(ConstantLanguages.UK, "UK"));
        put(ConstantLanguages.NL, new Locale(ConstantLanguages.NL, "NL"));
    }};

    public static String getChineseSystemLanguage() {
        return ConstantLanguages.ZH_CN;
    }

    /**
     * 获取系统默认语言
     */
    public static String getSystemLanguage() {
        Locale locale = LanguageUtils.getSystemLanguage();
        String language = locale.getLanguage();
        if (TextUtils.equals(Locale.CHINA.getLanguage(), language)) {
            if (locale.getCountry().equals("TW") || locale.getCountry().equals("HK")) {
                return ConstantLanguages.ZH_TW;
            } else {
                return ConstantLanguages.ZH_CN;
            }
        } else if (TextUtils.equals(Locale.GERMAN.getLanguage(), language)) {
            return ConstantLanguages.GERMAN;
        } else if (TextUtils.equals(Locale.JAPAN.getLanguage(), language)) {
            return ConstantLanguages.JA;
        } else if (TextUtils.equals(Locale.FRANCE.getLanguage(), language)) {
            return ConstantLanguages.FR;
        } else if (TextUtils.equals(Locale.ITALY.getLanguage(), language)) {
            return ConstantLanguages.IT;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.RU, "RU").getLanguage(), language)) {
            return ConstantLanguages.RU;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.PT, "PT").getLanguage(), language)) {
            return ConstantLanguages.PT;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.ES, "ES").getLanguage(), language)) {
            return ConstantLanguages.ES;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.PL, "PL").getLanguage(), language)) {
            return ConstantLanguages.PL;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.CS, "CS").getLanguage(), language)) {
            return ConstantLanguages.CS;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.UK, "UK").getLanguage(), language)) {
            return ConstantLanguages.UK;
        } else if (TextUtils.equals(new Locale(ConstantLanguages.NL, "NL").getLanguage(), language)) {
            return ConstantLanguages.NL;
        } else {
            if (BaseApplication.instance.isDomestic()) {
                return ConstantLanguages.ZH_CN;
            } else {
                return ConstantLanguages.ENGLISH;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        // app locale
        Locale locale = getLocaleByLanguage(newLanguage);
        configuration.setLocale(locale);
        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    public static void getConf(){

    }


    private static boolean isSupportLanguage(String language) {
        return mAllLanguages.containsKey(language);
    }

    public static String getSupportLanguage(String language) {
        if (isSupportLanguage(language)) {
            return language;
        }

        return ConstantLanguages.ENGLISH;
    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在{@link #mAllLanguages}，返回本机语言，如果本机语言不是语言集合中的一种{@link #mAllLanguages}，返回英语
     *
     * @param language language
     * @return
     */
    public static Locale getLocaleByLanguage(String language) {
        if (isSupportLanguage(language)) {
            return mAllLanguages.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : mAllLanguages.keySet()) {
                if (TextUtils.equals(mAllLanguages.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }


    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = AppLanguageUtils.getLocaleByLanguage(language);

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }
}
