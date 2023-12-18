package com.example.crawler.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.crawler.cardComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class favoriteDB extends SQLiteOpenHelper {
    private String TableName;
    public favoriteDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        TableName=name;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String command = "Create Table if not Exists " + TableName + "( "+
                "_id Integer Primary Key autoIncrement," +
                "Title Text, "+
                "Deadline Text,"+
                "Location Text"+
                ");";
        sqLiteDatabase.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropCommand = "Drop Table "+TableName;
        sqLiteDatabase.execSQL(dropCommand);
    }

    public void checkTable(){
        Cursor cursor = getWritableDatabase().rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() == 0)
                getWritableDatabase().execSQL("Create Table if not Exists " + TableName + "( "+
                        "_id Integer Primary Key autoIncrement," +
                        "Title Text, "+
                        "Deadline Text,"+
                        "Location Text,"+
                        "ContentURL Text"+
                        ");");
            cursor.close();
        }
    }

    public Vector<Map<String,String>> allFavoriteData(){
        String SelectCommand = "Select * from "+TableName;
        Cursor cursor = getReadableDatabase().rawQuery(SelectCommand,null);
        Vector<Map<String,String>> allData = new Vector<>();
        while(cursor.moveToNext()) {
            Map<String,String> temp = new HashMap<>();
            temp.put("Title",cursor.getString(1));
            temp.put("Deadline",cursor.getString(2));
            temp.put("Location",cursor.getString(3));
            temp.put("ContentURL",cursor.getString(4));
            allData.add(temp);
        }
        cursor.close();
        return allData;
    }

    public void AddData(cardComponent addData){
        if(hasInData(addData))
            return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title",addData.textString);
        values.put("Deadline",addData.WhenStr);
        values.put("Location",addData.WhereStr);
        values.put("ContentURL",addData.ContentURL);
        db.insert(TableName,null,values);
    }
    public void deleteData(cardComponent Data){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TableName,"Title = '" + Data.textString+"'",null);
    }

    public void clearData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete From "+ TableName);
    }

    public boolean hasInData(cardComponent Data){
        String command = "select Title From " + TableName + " Where Title = '"+Data.textString+"'";
        Cursor cursor = getWritableDatabase().rawQuery(command,null);
        int count=0;
        while(cursor.moveToNext()){
            count++;
        }
        return count!=0;
    }
}
