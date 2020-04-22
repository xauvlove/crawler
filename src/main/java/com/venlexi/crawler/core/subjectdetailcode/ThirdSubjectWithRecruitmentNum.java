package com.venlexi.crawler.core.subjectdetailcode;

import lombok.Data;

@Data
public class ThirdSubjectWithRecruitmentNum {
    //考试方式
    private String testMode;
    //院系所
    private String institute;
    //专业
    private String subject;
    //研究方向
    private String direction;
    //学习方式
    private String learningMode;
    //招生人数
    private String number;
    //考试范围
    private String testRangeSite;

    public boolean isSame(ThirdSubjectWithRecruitmentNum t) {
        if(testMode.equals(t.getTestMode()) && institute.equals(t.getInstitute())
                && subject.equals(t.getSubject()) && direction.equals(t.getDirection())
                && learningMode.equals(t.getLearningMode()) && number.equals(t.getNumber())
                && testRangeSite.equals(t.getTestRangeSite())) {
            return true;
        }
        return false;
    }
}
