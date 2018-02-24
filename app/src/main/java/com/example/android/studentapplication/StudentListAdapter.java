package com.example.android.studentapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin1 on 19/2/18.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        public final TextView studentNameItemView;
        public final TextView studentPhoneNumberItemView;
        ImageView student_delete_button;
        ImageView student_edit_button;

        public StudentViewHolder(View itemView) {
            super(itemView);
            studentNameItemView = (TextView) itemView.findViewById(R.id.student_name);
            studentPhoneNumberItemView = (TextView) itemView.findViewById(R.id.student_phone_number);
            student_delete_button = (ImageView) itemView.findViewById(R.id.student_delete_button);
            student_edit_button = (ImageView) itemView.findViewById(R.id.student_edit_button);
        }
    }

    private static final String TAG = StudentListAdapter.class.getSimpleName();
    public static final String EXTRA_STUDENT_ID = "STU_ID";
    public static final String EXTRA_STUDENT_NAME = "STUDENT_NAME";
    public static final String EXTRA_STUDENT_PHONE_NUMBER = "STUDENT_PHONE_NUMBER";
    public static final String EXTRA_CLASS_ID = "CLASS_ID";
    public static final String EXTRA_STUDENT_POSITION = "STUDENT_POSITION";

    private final LayoutInflater mInflater;
    Context mContext;
    ClassListOpenHelper mDB;
    ArrayList<StudentItem> students = new ArrayList<>();
    List<StudentItem> data= Collections.emptyList();
    StudentItem current;
    int currentPos=0;

    public StudentListAdapter(Context context, List<StudentItem> data) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }
    @Override
    public StudentListAdapter.StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.student_list_item, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StudentListAdapter.StudentViewHolder holder, int position) {
        current = data.get(position);
        holder.studentNameItemView.setText(current.getmStudentName());
        holder.studentPhoneNumberItemView.setText(current.getmStudentPhn());
        final StudentListAdapter.StudentViewHolder h = holder;
        h.student_delete_button.setOnClickListener(new MyButton1OnClickListener(current.getmStudentId(),null,null,current.getmClassId()) {
            @Override
            public void onClick(View v) {
                Log.d (TAG + "onClick", "VHPos " + h.getAdapterPosition() + " ID " + id);
                AlertDialog.Builder myAlertBuilder = new
                        AlertDialog.Builder(mContext);
                myAlertBuilder.setTitle("Alert");
                myAlertBuilder.setMessage("Click OK to continue, or Cancel to stop:");
                myAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked OK button.
                        new StudentAsyncTask(sid, name, phn, id, 3).execute();
                        new StudentActivity().new FetchStudents(mContext,id).execute();

                    }
                });
                myAlertBuilder.setNegativeButton("Cancel", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // User cancelled the dialog.
                            }
                        });
                myAlertBuilder.show();

            }
        });

        h.student_edit_button.setOnClickListener(new MyButton1OnClickListener(current.getmStudentId(),current.getmStudentName(),current.getmStudentPhn(),current.getmClassId()) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EditStudentActivity.class);
                intent.putExtra(EXTRA_STUDENT_ID,sid);
                intent.putExtra(EXTRA_STUDENT_NAME,name);
                intent.putExtra(EXTRA_STUDENT_PHONE_NUMBER,phn);
                intent.putExtra(EXTRA_CLASS_ID,id);
                ((Activity)mContext).startActivityForResult(intent,StudentActivity.STUDENT_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
