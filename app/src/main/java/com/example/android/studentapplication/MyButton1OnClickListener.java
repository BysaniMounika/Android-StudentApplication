package com.example.android.studentapplication;

import android.view.View;
import android.content.Intent;

/**
 * Created by admin1 on 17/2/18.
 */

public class MyButton1OnClickListener implements View.OnClickListener {
    private static final String TAG = View.OnClickListener.class.getSimpleName();

    int id;
    String className;
    String name;
    String phn;
    int sid;

    public MyButton1OnClickListener(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public MyButton1OnClickListener(int sid,String name,String phn,int id) {
        this.sid = sid;
        this.name = name;
        this.phn = phn;
        this.id = id;
    }

    public void onClick(View v) {
        // Implemented in WordListAdapter
    }
}
