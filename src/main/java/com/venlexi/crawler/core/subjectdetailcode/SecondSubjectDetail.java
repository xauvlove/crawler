package com.venlexi.crawler.core.subjectdetailcode;

import lombok.Data;

import java.util.List;

@Data
public class SecondSubjectDetail {
    private String mc;
    private String dm;

    private List<ThirdSubjectWithRecruitmentNum> thirdSubWithRecruNumsList;
}