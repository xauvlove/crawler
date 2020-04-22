package com.venlexi.crawler.core.subjectdetailcode;

import lombok.Data;
import java.util.List;

@Data
public class FirstSubjectDetail {
    //一级学科，例如法学 工学
    private String name;
    //一级学科代码 工学是 08
    private String code;

    private List<SecondSubjectDetail> secondSubjectDetailList;
}
