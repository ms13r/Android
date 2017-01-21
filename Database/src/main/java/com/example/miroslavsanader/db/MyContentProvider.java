package com.example.miroslavsanader.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    public final static String DBNAME = "Employees";
    public final static String TABLENAME = "employeetable";
    public final static String COLUMN_EMPLID = "emplid";
    public final static String COLUMN_NAME= "name";
    public final static String COLUMN_SEX = "sex";
    public final static String COLUMN_DEPT = "dept";
    public final static String COLUMN_ACCESS ="accesscode";
    public final static String COLUMN_AD = "ad";
    public final static String COLUMN_EMAIL = "email";
    public final static String AUTHORITY = "com.example.miroslavsanader.db.provider";
    public final static String URI = "content://" + AUTHORITY + "/" + DBNAME;
    public static final Uri CONTENT_URI = Uri.parse(URI);
    private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " +
            TABLENAME + "(" + "ID_KEY INTEGER PRIMARY KEY, "
            + COLUMN_EMPLID + " TEXT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_SEX + " TEXT,"
            + COLUMN_DEPT + " TEXT,"
            + COLUMN_ACCESS + " TEXT,"
            + COLUMN_AD + " TEXT)";
    private DBHelper OpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return OpenHelper.getWritableDatabase().delete(TABLENAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String[] column_emplid = {COLUMN_EMPLID};
        String select_statement = COLUMN_EMPLID + " =?";
        String[] args = {values.getAsString(COLUMN_EMPLID)};

        // Check for null values
        if(values.getAsString(COLUMN_EMPLID).equals("")){ return null; }
        if(values.getAsString(COLUMN_NAME).equals("")){ return null; }
        if(values.getAsString(COLUMN_SEX).equals("")){ return null; }
        if(values.getAsString(COLUMN_EMAIL).equals("")){return null; }
        if(values.getAsString(COLUMN_DEPT).equals("")){ return null; }
        if(values.getAsString(COLUMN_ACCESS).equals("")){ return null; }
        if(values.getAsString(COLUMN_AD).equals("")){ return null; }

        // Check if the user exists in the database
        // If so, return a null URI
        // If not, return the proper URI post-insertion
        Cursor res = query(uri, column_emplid, select_statement, args, null);
        if(res != null){
            if(res.getCount() > 0){
                res.close();
                return null;
            }
            else{
                res.close();
                long id = OpenHelper.getWritableDatabase().insert(TABLENAME, null, values);
                return Uri.withAppendedPath(CONTENT_URI, "" + id);
            }
        }
        res.close();
        return null;
    }

    @Override
    public boolean onCreate() {
        // Create the new database
        OpenHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return OpenHelper.getWritableDatabase().query(TABLENAME, projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return OpenHelper.getWritableDatabase().update(TABLENAME, values, selection, selectionArgs);
    }

    protected static final class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }
    }
}
