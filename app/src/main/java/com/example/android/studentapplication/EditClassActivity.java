package com.example.android.studentapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditClassActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private static final String TAG = EditClassActivity.class.getSimpleName();

    private static final int NO_ID = -99;
    private static final String NO_CLASS = "";
    private NetworkStateReceiver networkStateReceiver;
    private EditText mEditClassView;
    private LinearLayout editClass;
    private TextWatcher textWatcher;
    // Unique tag for the intent reply.
    public static final String EXTRA_REPLY = "com.example.android.studentapplication.REPLY";

    int mId = MainActivity.CLASS_ADD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));


        editClass = (LinearLayout) findViewById(R.id.edit_class_activity);
        if(MainActivity.light == 1) {
            editClass.getBackground().setColorFilter(null);
        }
        else {
            editClass.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
        }
        mEditClassView = (EditText) findViewById(R.id.edit_class);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String className;
                className = mEditClassView.getText().toString();
                if(className.length() > 0) {
                    mEditClassView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mEditClassView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                }
                else {
                    mEditClassView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_black_24dp, 0);
                }
            }
        };
        mEditClassView.addTextChangedListener(textWatcher);
        // Get data sent from calling activity.
        Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            int id = extras.getInt(ClassListAdapter.EXTRA_ID, NO_ID);
            String word = extras.getString(ClassListAdapter.EXTRA_WORD, NO_CLASS);
            if (id != NO_ID && word != NO_CLASS) {
                mId = id;
                mEditClassView.setText(word);
            }
        } // Otherwise, start with empty fields.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnReply(View view) {
        String word = ((EditText) findViewById(R.id.edit_class)).getText().toString();
        if(word.length() == 0)
        {
            mEditClassView.setError("Field can't be Empty");
        }
        else {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, word);
            replyIntent.putExtra(ClassListAdapter.EXTRA_ID, mId);
            setResult(RESULT_OK, replyIntent);
            finish();
            overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
        }
    }


    @Override
    public void networkAvailable() {
        Snackbar snackbar = Snackbar
                .make(editClass, "Internet connection available", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkUnavailable() {
        Snackbar snackbar = Snackbar
                .make(editClass, "Internet connection unavailable", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
