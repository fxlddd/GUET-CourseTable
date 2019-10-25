package com.fxl.okhttptest.Score;

/**
 * Created by FXL-PC on 2017/2/11.
 */

public class ScoreModel {
    private String term;
    private String className;
    private String classID;
    private String grade;
    private String credit;
    private String classProperty;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classCode) {
        this.classID = classCode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCredit() {
        return credit;
    }

    public String getClassProperty() {
        return classProperty;
    }

    public void setClassProperty(String classProperty) {
        this.classProperty = classProperty;
    }

    public void setCredit(String credit) {
        this.credit = credit;

    }
}
