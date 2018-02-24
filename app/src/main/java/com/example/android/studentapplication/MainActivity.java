package com.example.android.studentapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Object;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int CLASS_EDIT = 1;
    public static final int CLASS_ADD = -1;

    private  RecyclerView mRecyclerView;
    private ClassListAdapter mAdapter;
    private ClassListOpenHelper mDB;
    private CoordinatorLayout main;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    public static int  light = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = (CoordinatorLayout) findViewById(R.id.main_activity);

        //call to Async Task
        new FetchClasses(this).execute();
        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditClassActivity.class);
                startActivityForResult(intent, CLASS_EDIT);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem mode = (MenuItem) menu.findItem(R.id.night_mode);
        if(light ==1 ) {
            mode.setTitle("Night Mode");
        } else {
            mode.setTitle("Day Mode");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.night_mode) {
            Log.d("MOde",item.getTitle().toString());
            if(item.getTitle().toString()=="Day Mode") {
                main.getBackground().setColorFilter(null);
                light = 1;
            }
            else {
                main.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
                light = 0;
            }
        }
        recreate();
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add code to update the database.
        if(requestCode == CLASS_EDIT) {
            if(resultCode == RESULT_OK) {
                String word = data.getStringExtra(EditClassActivity.EXTRA_REPLY);
                if(!TextUtils.isEmpty(word)) {
                    int id = data.getIntExtra(ClassListAdapter.EXTRA_ID,-99);
                    if(id == CLASS_ADD) {
                        new AddClassAsyncTask(word,id,1,this).execute();
                    }
                    if(id >= 0) {
                        new AddClassAsyncTask(word,id,2,this).execute();
                    }
                    new FetchClasses(this).execute();
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.empty_not_saved, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public  class FetchClasses extends AsyncTask<String, String, String> {
        Context mContext;
        Activity activity;
        ProgressDialog pdLoading ;
        HttpURLConnection conn;
        URL url = null;

        public FetchClasses(Context context) {
            mContext = context;
            pdLoading = new ProgressDialog(context);
            activity = (Activity) context;
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

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = new URL("http://192.168.10.214:3000/sections");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return ("urlException");
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return ("connectException");
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return ("IOException");
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            //this method will be running on UI thread
            Log.d("result",s);
            /*if(s == "connectException") {
                Toast.makeText(getApplicationContext(),"Oops... Required Port Is Not Open On Server",Toast.LENGTH_LONG).show();
                finish();
            }*/

            pdLoading.dismiss();
            List<ClassItem> data=new ArrayList<>();
            try {
                JSONArray jArray = new JSONArray(s);
                Log.d("err","1");

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    ClassItem newClass = new ClassItem();
                    newClass.setmId(json_data.getInt("class_id"));
                    newClass.setmClass(json_data.getString("class_name"));
                    Log.d("err","2");
                    data.add(newClass);
                }

                // Setup and Handover data to recyclerview
                Log.d("err","3 strt");
                mRecyclerView = (RecyclerView)activity.findViewById(R.id.recyclerview);
                Log.d("err","3");
                // Create an mAdapter and supply the data to be displayed.
                mAdapter = new ClassListAdapter(mContext,data);
                Log.d("err","4");
                // Connect the mAdapter with the recycler view.
                mRecyclerView.setAdapter(mAdapter);
                Log.d("err","5");
                // Give the recycler view a default layout manager.
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                Log.d("err","6");

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("error",e.toString());
            }
        }
    }

}
