package com.fxl.guetcoursetable.corsetable;

/**
 * Created by FXL-PC on 2017/2/20.
 */

public class Course {
    private int corseNum;
    private String corseName;
    private int startWeekNum;
    private int endWeekNum;
    private int corseWeek;
    private int corseSection;
    private String classRoom;
    private String corseID;
    private String teacher;

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getCorseNum() {
        return corseNum;
    }

    public void setCorseNum(int corseNum) {
        this.corseNum = corseNum;
    }

    public String getCorseName() {
        return corseName;
    }

    public void setCorseName(String corseName) {
        this.corseName = corseName;
    }

    public int getStartWeekNum() {
        return startWeekNum;
    }

    public void setStartWeekNum(int startWeekNum) {
        this.startWeekNum = startWeekNum;
    }

    public int getEndWeekNum() {
        return endWeekNum;
    }

    public void setEndWeekNum(int endWeekNum) {
        this.endWeekNum = endWeekNum;
    }

    public int getCourseWeek() {
        return corseWeek;
    }

    public void setCorseWeek(int corseWeek) {
        this.corseWeek = corseWeek;
    }

    public int getCourseSection() {
        return corseSection;
    }

    public void setCorseSection(int corseSection) {
        this.corseSection = corseSection;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getCorseID() {
        return corseID;
    }

    public void setCorseID(String corseID) {
        this.corseID = corseID;
    }
}
