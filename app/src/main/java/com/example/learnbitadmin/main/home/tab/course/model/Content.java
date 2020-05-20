package com.example.learnbitadmin.main.home.tab.course.model;

public class Content {

    private String sectionPart;
    private String sectionTopicName;
    private String sectionTopicType;

    public Content() {
    }

    public Content(String sectionPart, String sectionTopicName, String sectionTopicType) {
        this.sectionPart = sectionPart;
        this.sectionTopicName = sectionTopicName;
        this.sectionTopicType = sectionTopicType;
    }

    public String getSectionPart() {
        return sectionPart;
    }

    public void setSectionPart(String sectionPart) {
        this.sectionPart = sectionPart;
    }

    public String getSectionTopicName() {
        return sectionTopicName;
    }

    public void setSectionTopicName(String sectionTopicName) {
        this.sectionTopicName = sectionTopicName;
    }

    public String getSectionTopicType() {
        return sectionTopicType;
    }

    public void setSectionTopicType(String sectionTopicType) {
        this.sectionTopicType = sectionTopicType;
    }
}
