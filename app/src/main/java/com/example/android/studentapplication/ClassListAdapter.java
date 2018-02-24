package com.example.android.studentapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by admin1 on 17/2/18.
 */

class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ClassViewHolder> {

    public class ClassViewHolder extends RecyclerView.ViewHolder {

        public final TextView classItemView;
        ImageView delete_button;
        ImageView edit_button;
        ImageView show_button;

        public ClassViewHolder(View itemView) {
            super(itemView);
            classItemView = (TextView) itemView.findViewById(R.id.class_name);
            delete_button = (ImageView) itemView.findViewById(R.id.delete_button);
            edit_button = (ImageView) itemView.findViewById(R.id.edit_button);
            show_button = (ImageView) itemView.findViewById(R.id.show_button);
        }
    }

    private static final String TAG = ClassListAdapter.class.getSimpleName();

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    public static final String EXTRA_POSITION = "POSITION";

    private final LayoutInflater mInflater;
    Context mContext;
    ClassListOpenHelper mDB;
    List<ClassItem> data= Collections.emptyList();
    ClassItem current;
    int currentPos=0;

    public ClassListAdapter(Context context, List<ClassItem> data) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.data=data;
    }


    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.classlist_item, parent, false);
        return new ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        ClassItem current = data.get(position);
        holder.classItemView.setText(current.getmClass());
        final ClassViewHolder h = holder;
        h.delete_button.setOnClickListener(new MyButton1OnClickListener(current.getmId(),null) {
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
                        new AddClassAsyncTask("",id,3,mContext).execute();
                        new MainActivity().new FetchClasses(mContext).execute();
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

        h.edit_button.setOnClickListener(new MyButton1OnClickListener(current.getmId(),current.getmClass()) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EditClassActivity.class);
                intent.putExtra(EXTRA_ID,id);
                intent.putExtra(EXTRA_POSITION,h.getAdapterPosition());
                intent.putExtra(EXTRA_WORD,className);
                ((Activity)mContext).startActivityForResult(intent,MainActivity.CLASS_EDIT);

            }
        });

        h.show_button.setOnClickListener(new MyButton1OnClickListener(current.getmId(),null) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,StudentActivity.class);
                intent.putExtra(EXTRA_ID,id);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        //int c = (int) mDB.count();
        return data.size();
    }


}
