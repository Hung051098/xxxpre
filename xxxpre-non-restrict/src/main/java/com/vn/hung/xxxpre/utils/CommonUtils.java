package com.vn.hung.xxxpre.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class CommonUtils {
    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static long getLongUUID() {
        int min = 1000000;
        int max = 9999999;
        int random_int = (int)Math.floor(Math.random() * (double)(max - min + 1) + (double)min);
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddHHmmssZZZ");
        String datetime = ft.format(dNow);
        String var10000 = datetime.substring(0, 12);
        long data = Long.parseLong(var10000 + random_int);
        return data;
    }
}
