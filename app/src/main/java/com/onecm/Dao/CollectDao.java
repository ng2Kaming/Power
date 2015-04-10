package com.onecm.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.onecm.bean.Collect;
import com.onecm.bean.Discover;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/31 0031.
 */
public class CollectDao {
    private Context context;
    private DBHelper dbHelper;
    private final long EXIST = -1;

    public CollectDao(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String sql = "drop table " + DBHelper.TABLE_COLLECTS;
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    public void deleteCollects(String objectId) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        //String sql = "delete from " + DBHelper.TABLE_COLLECTS + " where objectId='"+objectId+"'";
        //Log.d("TAG",sql);
        sqLiteDatabase.delete(DBHelper.TABLE_COLLECTS,"objectId=?",new String[]{objectId});
        //sqLiteDatabase.execSQL(sql, null);
        sqLiteDatabase.close();
    }

    public void insertCollects(Discover discover) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String query = "select * from "+DBHelper.TABLE_COLLECTS + " where objectId='"+discover.getObjectId()+"'";
        Cursor c = sqLiteDatabase.rawQuery(query,null);
        if(c.moveToFirst()){
            return;
        }
        String insert = "insert into "+DBHelper.TABLE_COLLECTS + "(objectId,date,imgUrl,content,author) values('"
                +discover.getObjectId()+"','"+discover.getDate()+"','"+discover.getDisImg().getFileUrl(context)+"','"
                +discover.getContent()+"','"+discover.getAuthor()+"')";
        sqLiteDatabase.execSQL(insert);
        sqLiteDatabase.close();
    }


    public void insertCollectTag(String objectId, int i) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String query = "select * from " + DBHelper.TABLE_COLLECT + " where objectId='" + objectId + "'";
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        if (!c.moveToFirst()) {
            String insert = "insert into "+DBHelper.TABLE_COLLECT+"(objectId,collectTag) values('" + objectId + "'," + i + ")";
            sqLiteDatabase.execSQL(insert);
        } else {
            String update = "update " + DBHelper.TABLE_COLLECT + " set collectTag=" + i + " where objectId='" + objectId + "'";
            sqLiteDatabase.execSQL(update);
        }
        sqLiteDatabase.close();
    }


    public void insertLikeTag(String objectId, int i) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String query = "select * from " + DBHelper.TABLE_LIKE + " where objectId='" + objectId + "'";
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        if (!c.moveToFirst()) {
            String insert = "insert into "+DBHelper.TABLE_LIKE+"(objectId,likeTag) values('" + objectId + "'," + i + ")";
            sqLiteDatabase.execSQL(insert);
        } else {
            String update = "update " + DBHelper.TABLE_LIKE + " set likeTag=" + i + " where objectId='" + objectId + "'";
            sqLiteDatabase.execSQL(update);
        }
        sqLiteDatabase.close();
    }


    public int getCollectTag(String objectId) {
        int tag = 0;
        String sql = "select collectTag from " + DBHelper.TABLE_COLLECT + " where objectId=?";
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(sql, new String[]{objectId});
        if (c.moveToFirst()) {
            tag = c.getInt(0);
        }
        c.close();
        sqLiteDatabase.close();
        return tag;
    }

    public int getLikeTag(String objectId) {
        int tag = 0;
        String sql = "select likeTag from " + DBHelper.TABLE_LIKE + " where objectId=?";
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(sql, new String[]{objectId});
        if (c.moveToFirst()) {
            tag = c.getInt(0);
        }
        c.close();
        sqLiteDatabase.close();
        return tag;
    }


    public List<Collect> selectAll() {
        List<Collect> mList = new ArrayList<Collect>();
        String sql = "select objectId,date,imgUrl,content,author from " + DBHelper.TABLE_COLLECTS;
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(sql, null);
        Collect collect;
        while (c.moveToNext()) {
            collect = new Collect();
            collect.setObjectId(c.getString(0));
            collect.setDate(c.getString(1));
            collect.setImgUrl(c.getString(2));
            collect.setContent(c.getString(3));
            collect.setAuthor(c.getString(4));
            mList.add(collect);
        }
        c.close();
        sqLiteDatabase.close();
        return mList;
    }

}
