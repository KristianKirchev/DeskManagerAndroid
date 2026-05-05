package com.deskmanager.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DISPLAY_FORMAT = "dd MMM yyyy";

    private DateUtils() {}

    public static String format(long epochMillis) {
        return new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault()).format(new Date(epochMillis));
    }

}
