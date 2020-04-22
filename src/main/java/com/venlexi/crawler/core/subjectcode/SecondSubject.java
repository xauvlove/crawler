package com.venlexi.crawler.core.subjectcode;

import lombok.Data;

import java.util.List;

@Data
public class SecondSubject {
    private String name;
    private String code;
    private List<ThirdSubject> thirdSubjectList;
}
