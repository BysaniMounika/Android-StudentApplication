package com.example.android.studentapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditStudentActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private static final String TAG = EditStudentActivity.class.getSimpleName();
    private NetworkStateReceiver networkStateReceiver;
    private static final int NO_ID = -99;
    private static final String NO_NAME = "";
    private static final String NO_PHN="";
    private static final int NO_CLASS_ID = -99;

    private EditText mEditStudentNameView;
    private EditText mEditStudentPhoneNumberView;
    Drawable errorIcon;

    public static final String EXTRA_REPLY_NAME= "com.example.android.studentapplication.REPLY_NAME";
    public static final String EXTRA_REPLY_PHN= "com.example.android.studentapplication.REPLY_PHN";

    int mId = StudentActivity.STUDENT_ADD;
    int mClassId = StudentActivity.CLASS_ID;
    private LinearLayout editStudent;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditStudentActivity.this,StudentActivity.class);
        intent.putExtra(ClassListAdapter.EXTRA_ID,mClassId);
        startActivity(intent);
        finish();
        overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                Intent intent = new Intent(EditStudentActivity.this,StudentActivity.class);
                intent.putExtra(ClassListAdapter.EXTRA_ID,mClassId);
                startActivity(intent);
                finish();

                return true;
            default : break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        mEditStudentNameView = (EditText) findViewById(R.id.edit_student_name);
        mEditStudentPhoneNumberView = (EditText) findViewById(R.id.edit_student_phone_number);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phn  = mEditStudentPhoneNumberView.getText().toString();
                String name = mEditStudentNameView.getText().toString();
                if(phn.length() == 10) {
                    mEditStudentPhoneNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mEditStudentPhoneNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                } else {
                    mEditStudentPhoneNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
                }
                if(name.length() > 0) {
                    mEditStudentNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mEditStudentNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                } else {
                    mEditStudentNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
                }
            }
        };

        mEditStudentPhoneNumberView.addTextChangedListener(textWatcher);
        mEditStudentNameView.addTextChangedListener(textWatcher);
        errorIcon  = getResources().getDrawable(R.drawable.ic_error_black_24dp);
        // Get data sent from calling activity.
        Bundle extras = getIntent().getExtras();
        editStudent = (LinearLayout) findViewById(R.id.edit_student_activity);
        if(MainActivity.light == 1) {
            editStudent.getBackground().setColorFilter(null);
        }
        else {
            editStudent.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
        }

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            int id = extras.getInt(StudentListAdapter.EXTRA_STUDENT_ID, NO_ID);
            String name = extras.getString(StudentListAdapter.EXTRA_STUDENT_NAME, NO_NAME);
            String phn = extras.getString(StudentListAdapter.EXTRA_STUDENT_PHONE_NUMBER,NO_PHN);
            int classId = extras.getInt(StudentListAdapter.EXTRA_CLASS_ID,NO_CLASS_ID);
            if (id != NO_ID && name != NO_NAME && phn != NO_PHN && classId != NO_CLASS_ID) {
                mId = id;
                mEditStudentNameView.setText(name);
                mEditStudentPhoneNumberView.setText(phn);
                mClassId = classId;

            }
        } // Otherwise, start with empty fields.
    }

    public void returnReply2(View view) {
        String name = ((EditText) findViewById(R.id.edit_student_name)).getText().toString();
        String phn = ((EditText) findViewById(R.id.edit_student_phone_number)).getText().toString();

        if(name.length() == 0) {
            mEditStudentNameView.setError("Name field cannot be empty");
        }
        else if(phn.length() == 0) {
            mEditStudentPhoneNumberView.setError("Field cannot be empty");
        }
        else if(phn.length() < 10 || phn.length() >10) {
            mEditStudentPhoneNumberView.setError("Phone number must be 10 digits long");
        }
        else {

            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY_NAME, name);
            replyIntent.putExtra(StudentListAdapter.EXTRA_STUDENT_ID, mId);
            replyIntent.putExtra(EXTRA_REPLY_PHN, phn);
            replyIntent.putExtra(StudentListAdapter.EXTRA_CLASS_ID, mClassId);
            setResult(RESULT_OK, replyIntent);
            finish();
            overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Log","Destroy2");
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Log","Stop2");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Log","pause2");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Log","restart2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Log","resume2");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Log","start2");
    }

    @Override
    public void networkAvailable() {
        Snackbar snackbar = Snackbar
                .make(editStudent, "Internet connection available", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void networkUnavailable() {
        Snackbar snackbar = Snackbar
                .make(editStudent, "Internet connection unavailable", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
