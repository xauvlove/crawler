package com.venlexi.crawler.core;

import lombok.Data;

public class University {
    private String name;
    private String location;
    private String ownTo;
    private Boolean hasPostgInstitute;
    private Boolean isSelfGivenFractLine;
    private String questionSite;
    private String recruitmentBrochureSite;
    private String dispensingRuleSite;

    public String getName() {
        return name;
    }

    public University setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public University setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getOwnTo() {
        return ownTo;
    }

    public University setOwnTo(String ownTo) {
        this.ownTo = ownTo;
        return this;
    }

    public Boolean getHasPostgInstitute() {
        return hasPostgInstitute;
    }

    public University setHasPostgInstitute(Boolean hasPostgInstitute) {
        this.hasPostgInstitute = hasPostgInstitute;
        return this;
    }

    public Boolean getSelfGivenFractLine() {
        return isSelfGivenFractLine;
    }

    public University setSelfGivenFractLine(Boolean selfGivenFractLine) {
        isSelfGivenFractLine = selfGivenFractLine;
        return this;
    }

    public String getQuestionSite() {
        return questionSite;
    }

    public University setQuestionSite(String questionSite) {
        this.questionSite = questionSite;
        return this;
    }

    public String getRecruitmentBrochureSite() {
        return recruitmentBrochureSite;
    }

    public University setRecruitmentBrochureSite(String recruitmentBrochureSite) {
        this.recruitmentBrochureSite = recruitmentBrochureSite;
        return this;
    }

    public String getDispensingRuleSite() {
        return dispensingRuleSite;
    }

    public University setDispensingRuleSite(String dispensingRuleSite) {
        this.dispensingRuleSite = dispensingRuleSite;
        return this;
    }

    @Override
    public String toString() {
        return "University{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", ownTo='" + ownTo + '\'' +
                ", hasPostgInstitute=" + hasPostgInstitute +
                ", isSelfGivenFractLine=" + isSelfGivenFractLine +
                ", questionSite='" + questionSite + '\'' +
                ", recruitmentBrochureSite='" + recruitmentBrochureSite + '\'' +
                ", dispensingRuleSite='" + dispensingRuleSite + '\'' +
                '}';
    }
}
