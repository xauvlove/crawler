package com.venlexi.crawler.core;

import lombok.Data;

@Data
public class Condition {

    public static final String byClass = ".";
    public static final String byId = "#";
    public static final String byAttributionPrefix = "[";
    public static final String byAttributionSuffix = "]";
    public static final String byTagSpace = " ";

    private String desc;
    private String type;
}
