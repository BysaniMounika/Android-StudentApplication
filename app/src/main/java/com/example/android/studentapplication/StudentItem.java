package com.example.android.studentapplication;

/**
 * Created by admin1 on 19/2/18.
 */

public class StudentItem {

    private int mStudentId;
    private String mStudentName;
    private String mStudentPhn;
    private int mClassId;

    public StudentItem() {}

    public int getmStudentId() {
        return mStudentId;
    }

    public String getmStudentName() {
        return mStudentName;
    }

    public String getmStudentPhn() {
        return mStudentPhn;
    }

    public int getmClassId() {
        return mClassId;
    }

    public void setmStudentId(int studentId) {
        mStudentId = studentId;
    }

    public void setmStudentName(String mStudentName) {
        this.mStudentName = mStudentName;
    }

    public void setmStudentPhn(String mStudentPhn) {
        this.mStudentPhn = mStudentPhn;
    }

    public void setmClassId(int mClassId) {
        this.mClassId = mClassId;
    }

}
