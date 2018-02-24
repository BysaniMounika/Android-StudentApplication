package com.example.android.studentapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class StudentActivity extends AppCompatActivity {

    public static final int STUDENT_EDIT = 1;
    public static final int STUDENT_ADD = -1;
    public static int CLASS_ID;
    private RecyclerView mRecyclerView2;
    private StudentListAdapter mAdapter2;
    private CoordinatorLayout studentActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Bundle extra = getIntent().getExtras();
        studentActivity = (CoordinatorLayout) findViewById(R.id.student_activity);
        if(MainActivity.light == 1) {
            studentActivity.getBackground().setColorFilter(null);
        }
        else {
            studentActivity.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
        }

        CLASS_ID = extra.getInt(ClassListAdapter.EXTRA_ID);

        new FetchStudents(this,CLASS_ID).execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditStudentActivity.class);
                startActivityForResult(intent, STUDENT_EDIT);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Log","Destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Log","Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Log","pause");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                Log.d("pre","dsfjsdjhfh");
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("Log","postResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Log","start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Log","resume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Log","restart");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add code to update the database.
        if(requestCode == STUDENT_EDIT) {
            if(resultCode == RESULT_OK) {
                String name = data.getStringExtra(EditStudentActivity.EXTRA_REPLY_NAME);
                String phn = data.getStringExtra(EditStudentActivity.EXTRA_REPLY_PHN);
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phn) && phn.length()==10) {
                    int id = data.getIntExtra(StudentListAdapter.EXTRA_STUDENT_ID,-99);
                    int classId = data.getIntExtra(StudentListAdapter.EXTRA_CLASS_ID, -99);
                    if(id == STUDENT_ADD) {
                        new StudentAsyncTask(id,name,phn,classId,1).execute();
                    }
                    if(id >= 0) {
                        new StudentAsyncTask(id,name,phn,classId,2).execute();
                    }
                    //mAdapter2.notifyDataSetChanged();
                    new FetchStudents(this,CLASS_ID).execute();
                }
                else if(TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phn)) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone Number is empty", Toast.LENGTH_SHORT).show();
                }
                else if(phn.length() < 10) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone number is less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else if(phn.length() > 10){
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone number is greater than 10 digits", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class FetchStudents extends AsyncTask<String, String, String> {

        Context mContext;
        Activity activity;
        ProgressDialog pdLoading ;
        HttpURLConnection conn;
        URL url = null;
        int class_id;

        public FetchStudents(Context context, int class_id) {
            mContext = context;
            pdLoading = new ProgressDialog(context);
            activity = (Activity) context;
            this.class_id = class_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL("http://192.168.10.214:3000/section/students"); // here is your URL path
                //URL url = new URL("http://192.168.10.214:3000/section/add"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("class_id", class_id);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //this method will be running on UI thread
            pdLoading.dismiss();
            List<StudentItem> data=new ArrayList<>();
            try {
                JSONArray jArray = new JSONArray(s);
                Log.d("err","1");

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    StudentItem newStudent = new StudentItem();
                    newStudent.setmClassId(json_data.getInt("class_id"));
                    newStudent.setmStudentId(json_data.getInt("student_id"));
                    newStudent.setmStudentName(json_data.getString("student_name"));
                    newStudent.setmStudentPhn(json_data.getString("student_phn"));
                    Log.d("err","2");
                    data.add(newStudent);
                }

                // Setup and Handover data to recyclerview
                Log.d("err","3 strt");
                mRecyclerView2 = (RecyclerView)activity.findViewById(R.id.student_recyclerview);
                Log.d("err","3");
                // Create an mAdapter and supply the data to be displayed.
                mAdapter2 = new StudentListAdapter(mContext,data);
                Log.d("err","4");
                // Connect the mAdapter with the recycler view.
                mRecyclerView2.setAdapter(mAdapter2);
                Log.d("err","5");
                // Give the recycler view a default layout manager.
                mRecyclerView2.setLayoutManager(new LinearLayoutManager(mContext));
                Log.d("err","6");

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("error",e.toString());
            }
        }
        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }

}
