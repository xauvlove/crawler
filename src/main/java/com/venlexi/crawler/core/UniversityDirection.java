package com.venlexi.crawler.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UniversityDirection {
    //大学名
    private String name;
    //大学 school id
    private String id;
    //大方向名
    private List<String> bigDirection;
    //<大方向, 小方向名>
    private Map<String, List<String>> smallDirectionNameMap;
    //<大方向, 小方向代码>
    private Map<String, List<String>> smallDirectionCodeMap;
    //<大方向, 小方向名 - 代码>
    private Map<String, List<String>> smallDirectionNameAndCode;
}
