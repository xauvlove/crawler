package com.venlexi.crawler.core.subjectcode;

import lombok.Data;
import java.util.List;

@Data
public class FirstSubject {

    //学硕 = 1
    private Integer type;
    private String name;
    private String code;

    private List<SecondSubject> secondSubjectList;

}
