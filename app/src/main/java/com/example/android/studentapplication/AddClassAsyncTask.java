package com.example.android.studentapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by admin1 on 22/2/18.
 */

public class AddClassAsyncTask extends AsyncTask<String,String, String> {

    String class_name = null;
    int class_id = 0;
    HttpURLConnection conn;
    URL url = null;
    int requestCode;
    Context mContext;
    public AddClassAsyncTask(String class_name, int class_id, int requestCode, Context context) {
        this.class_name = class_name;
        this.class_id = class_id;
        this.requestCode = requestCode;
        this.mContext = context;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("RESULT",s);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            if(requestCode == 3) {
                url = new URL("http://192.168.10.214:3000/section/delete"); // here is your URL path
            } else if(requestCode == 1) {
                url = new URL("http://192.168.10.214:3000/section/add"); // here is your URL path
            } else if(requestCode == 2) {
                url = new URL("http://192.168.10.214:3000/section/edit"); // here is your URL path
            }

            //URL url = new URL("http://192.168.10.214:3000/section/add"); // here is your URL path
            JSONObject postDataParams = new JSONObject();
            if(requestCode == 3) {
                postDataParams.put("class_id", class_id);
            } else if(requestCode == 1) {
                postDataParams.put("class_name", class_name);
            } else if(requestCode == 2) {
                postDataParams.put("class_name", class_name);
                postDataParams.put("class_id", class_id);
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
            return ("Connection refused");
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
