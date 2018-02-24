package com.example.android.studentapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by admin1 on 17/2/18.
 */

class ClassListOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = ClassListOpenHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String CLASS_LIST_TABLE = "class_table";
    private static final String STUDENT_LIST_TABLE = "student_table";
    private static final String DATABASE_NAME = "mydb";
    //column names
    public static final String KEY_CLASS_ID = "class_id";
    public static final String KEY_CLASS_NAME = "class_name";
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_STUDENT_NAME = "student_name";
    public static final String KEY_STUDENT_PHONE_NUMBER = "student_phone_number";
    //String array of columns
    private static final String[] CLASS_COLUMNS = {KEY_CLASS_ID, KEY_CLASS_NAME};
    private static final String[] STUDENT_CLASS_COLUMNS = {KEY_STUDENT_ID, KEY_STUDENT_NAME, KEY_STUDENT_PHONE_NUMBER, KEY_CLASS_ID};
    //Buliding sql query to create database
    private static final String CLASS_LIST_TABLE_CREATE =
            "CREATE TABLE " + CLASS_LIST_TABLE + "(" +
                    KEY_CLASS_ID + " INTEGER PRIMARY KEY," +
                    KEY_CLASS_NAME + " TEXT )";
    private static final String STUDENT_LIST_TABLE_CREATE =
            "CREATE TABLE " + STUDENT_LIST_TABLE + "(" +
                    KEY_STUDENT_ID + " INTEGER PRIMARY KEY," +
                    KEY_STUDENT_NAME+" TEXT,"+
                    KEY_STUDENT_PHONE_NUMBER+" TEXT,"+
                    KEY_CLASS_ID + " INTEGER," +
                    "FOREIGN KEY("+KEY_CLASS_ID+") REFERENCES "+CLASS_LIST_TABLE+"("+KEY_CLASS_ID+") )";
    //instances for writeing and reading databases
    private SQLiteDatabase mReadableDB;
    private SQLiteDatabase mWritableDB;


    public ClassListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CLASS_LIST_TABLE_CREATE);
        db.execSQL(STUDENT_LIST_TABLE_CREATE);
    }

    //inserting data into database
   /* private void fillDataBasewithData(SQLiteDatabase db) {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask",
                "Android Studio", "SQLiteDatabase", "SQLOpenHelper",
                "Data model", "ViewHolder", "Android Performance",
                "OnClickListener"};
        ContentValues values = new ContentValues();
        for (int i = 0; i < words.length; i++) {
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(ClassListOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CLASS_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+STUDENT_LIST_TABLE);
        onCreate(db);
    }

    public ClassItem query(int position) {
        String query = "SELECT * FROM " + CLASS_LIST_TABLE + " ORDER BY " + KEY_CLASS_NAME + " ASC LIMIT " + position + ",2";
        Cursor cursor = null;
        ClassItem entry = new ClassItem();
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setmId(cursor.getInt(cursor.getColumnIndex(KEY_CLASS_ID)));
            entry.setmClass(cursor.getString(cursor.getColumnIndex(KEY_CLASS_NAME)));
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION : " + e);
        } finally {
            cursor.close();
            return entry;
        }
    }


    public long insert(String word) {
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_CLASS_NAME, word);
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(CLASS_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "Exception : " + e);
        }
        return newId;
    }

    public long insertStudent(String name,String phn,int classId) {
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_NAME, name);
        values.put(KEY_STUDENT_PHONE_NUMBER,phn);
        values.put(KEY_CLASS_ID,classId);
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(STUDENT_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "Exception : " + e);
        }
        Log.d("newID" ,Long.toString(newId));
        return newId;
    }

    public long count() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, CLASS_LIST_TABLE);
    }


    public int delete(int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(CLASS_LIST_TABLE, //table name
                    KEY_CLASS_ID + " =? ", new String[]{String.valueOf(id)});
            mWritableDB.delete(STUDENT_LIST_TABLE,
                    KEY_CLASS_ID+"=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION : " + e);
        }
        return deleted;
    }

    public int deleteStudent(int stu_id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(STUDENT_LIST_TABLE, //table name
                    KEY_STUDENT_ID + " =? ", new String[]{String.valueOf(stu_id)});
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION : " + e);
        }
        return deleted;
    }

    public int update(int id, String word) {
        int mNoOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_CLASS_NAME, word);
            mNoOfRowsUpdated = mWritableDB.update(CLASS_LIST_TABLE,
                    values,
                    KEY_CLASS_ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION :" + e.getMessage());
        }
        return mNoOfRowsUpdated;
    }

    public int updateStudent(int stu_id, String name,String phn) {
        int mNoOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_STUDENT_NAME, name);
            values.put(KEY_STUDENT_PHONE_NUMBER,phn);
            mNoOfRowsUpdated = mWritableDB.update(STUDENT_LIST_TABLE,
                    values,
                    KEY_STUDENT_ID + "=?",
                    new String[]{String.valueOf(stu_id)});
        } catch (Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION :" + e.getMessage());
        }
        return mNoOfRowsUpdated;
    }

    public Cursor retrieve(int classId) {
        Cursor cursor = null;
        String query = "SELECT * FROM " + STUDENT_LIST_TABLE + " WHERE "+KEY_CLASS_ID+"="+classId+" ORDER BY "+KEY_STUDENT_NAME;
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query,null);
        }catch (Exception e) {
            Log.d(" RETRIEVE EXCEPTION ",e.getMessage());
        }
        return cursor;
    }

}
