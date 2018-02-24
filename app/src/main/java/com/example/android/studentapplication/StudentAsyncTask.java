package com.example.android.studentapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by admin1 on 23/2/18.
 */

public class StudentAsyncTask extends AsyncTask<String, String, String> {
    String student_name = null;
    int student_id = 0;
    int class_id = 0;
    String student_phn = null;
    HttpURLConnection conn;
    URL url = null;
    int requestCode;
    public StudentAsyncTask(int student_id, String student_name, String student_phn, int class_id, int requestCode) {
        this.student_id = student_id;
        this.student_phn = student_phn;
        this.class_id = class_id;
        this.student_name = student_name;
        this.requestCode = requestCode;
    }

    @Override
    protected void onPostExecute(String s) {
        if(requestCode != 3) Log.d("RESULT",s);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            if(requestCode == 3) {
                url = new URL("http://192.168.10.214:3000/section/student/delete"); // here is your URL path
            } else if(requestCode == 1) {
                url = new URL("http://192.168.10.214:3000/section/student/add"); // here is your URL path
            } else if(requestCode == 2) {
                url = new URL("http://192.168.10.214:3000/section/student/edit"); // here is your URL path
            }

            //URL url = new URL("http://192.168.10.214:3000/section/add"); // here is your URL path
            JSONObject postDataParams = new JSONObject();
            if(requestCode == 3) {
                postDataParams.put("student_id", student_id);
            } else if(requestCode == 1) {
                postDataParams.put("student_name", student_name);
                postDataParams.put("student_phn",student_phn);
                postDataParams.put("class_id",class_id);
            } else if(requestCode == 2){
                postDataParams.put("student_id",student_id);
                postDataParams.put("student_name", student_name);
                postDataParams.put("student_phn", student_phn);
            }
            Log.e("params",postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
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
