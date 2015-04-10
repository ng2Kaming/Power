package com.onecm.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/3/31 0031.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "powers";
    public static final String TABLE_COLLECTS = "collects";
    public static final String TABLE_LIKE = "likes";
    public static final String TABLE_COLLECT = "collect";
    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String collectSql = "create table "+TABLE_COLLECTS+"(_id integer primary key autoincrement,objectId text unique,date text,imgUrl text,content text,author text);";
        String likeTagSql = "create table " + TABLE_LIKE + "(_id integer primary key autoincrement,objectId text unique,likeTag tinyint);";
        String collectTagSql = "create table " + TABLE_COLLECT + "(_id integer primary key autoincrement,objectId text unique,collectTag tinyint);";
        db.execSQL(collectTagSql);
        db.execSQL(collectSql);
        db.execSQL(likeTagSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
