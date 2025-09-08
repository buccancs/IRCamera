package com.topdon.lib.core.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * des:
 * author: CaiSongL
 * date: 2024/9/13 18:35
 **/
/**
 * LocaleContextWrapper class.
 * 
 * Provides functionality for localecontextwrapper operations.
 */
public class LocaleContextWrapper extends ContextWrapper {

    public LocaleContextWrapper(Context base) {
        super(base);
    }

    /**
 * Wrap operation.
 * * @param context the context parameter
 * @param languageCode the languageCode parameter
 * @return the result
 */
public static ContextWrapper wrap(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        return new ContextWrapper(context.createConfigurationContext(config));
    }
}

