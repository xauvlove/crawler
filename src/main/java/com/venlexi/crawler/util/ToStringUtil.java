package com.venlexi.crawler.util;

import java.lang.reflect.Field;

public class ToStringUtil<T> {

    public String toEfficientString(T t) throws IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            stringBuilder.append(fields[i].get(t));
            if(i < fields.length - 1) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
