package com.abhi.android.themoviedatabase.Utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

/**
 * Saves data using the content provider to android
 */

public class SaveFavorites extends ContentProvider {

    //provider name used to define database
    public static final String PROVIDER_NAME = "com.abhi.android.moviedb.Utils.SaveFavorites";

    //URL location to our saved movies
    public static final String URL = "content://" + PROVIDER_NAME + "/favoriteMovies";

    //parse the location of our saved movies
    public static final Uri CONTENT_URL = Uri.parse(URL);

    //column names in out database
    public static final String title = "title";
    public static final String movieJson = "movieJson";
    public static final int uriCode = 1;

    //stores our movies in hash map with title as key, and json movie string as values
    private static HashMap<String,String> values;

    //Uri matcher matches unique Url with content provider
    static final UriMatcher uriMatcher;

    static {
        uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"favoriteMovies",uriCode);
    }

    private SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "myMovies";
    static final String TABLE_NAME = "favMovies";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = "CREATE TABLE " + TABLE_NAME + " (title TEXT NOT NULL, " + " movieJson TEXT NOT NULL);";


    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();

        return sqlDB != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case uriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default: throw new IllegalArgumentException("UNKNOWN URI " + uri);
        }

        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)){
            case uriCode:
                return "vnd.android.cursor.dir/favoriteMovies";
            default: throw new IllegalArgumentException("UNSUPPORTED URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = sqlDB.insert(TABLE_NAME, null, values);

        if(rowID > 0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }else
            return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = 0;

        switch (uriMatcher.match(uri)){
            case uriCode:
                rowsDeleted = sqlDB.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("UNKNOWN URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;

    }

    @Override

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = 0;

        switch (uriMatcher.match(uri)){
            case uriCode:
                rowsUpdated = sqlDB.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("UNKNOWN URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context,DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqlDB);
        }
    }
}

