package com.fmf.mypoem.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fmf on 15/4/2.
 */
public class MyPoemDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MyPoem.db";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INT = " INT";
    private static final String TYPE_DATE = " DATE";
    private static final String TYPE_DATETIME = " DATETIME";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_POEM = new StringBuffer()
            .append("CREATE TABLE ").append(MyPoem.Poem.TABLE_NAME).append(" (")
            .append(MyPoem.Poem._ID).append(" INTEGER PRIMARY KEY,")
            .append(MyPoem.Poem.COLUMN_NAME_TITLE).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_SUBTITLE).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_AUTHOR).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_CREATE_DATE).append(TYPE_DATE).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_UPDATE_TIME).append(TYPE_DATETIME).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_CONTENT).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_STATUS).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Poem.COLUMN_NAME_TYPE).append(TYPE_TEXT)
            .append(" )").toString();

    private static final String SQL_DELETE_POEM =
            "DROP TABLE IF EXISTS " + MyPoem.Poem.TABLE_NAME;

    private static final String SQL_CREATE_RHYTHM = new StringBuffer()
            .append("CREATE TABLE ").append(MyPoem.Rhythm.TABLE_NAME).append(" (")
            .append(MyPoem.Poem._ID).append(" INTEGER PRIMARY KEY,")
            .append(MyPoem.Rhythm.COLUMN_NAME_NAME).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_ALIAS).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_INTRO).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_COUNT).append(TYPE_INT).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_METRE).append(TYPE_DATE).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_SAMPLE).append(TYPE_DATETIME).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_COMMENT).append(TYPE_TEXT).append(COMMA_SEP)
            .append(MyPoem.Rhythm.COLUMN_NAME_TYPE).append(TYPE_TEXT)
            .append(" )").toString();

    private static final String SQL_DELETE_RHYTHM =
            "DROP TABLE IF EXISTS " + MyPoem.Rhythm.TABLE_NAME;

    public MyPoemDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MyPoemDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POEM);
        db.execSQL(SQL_CREATE_RHYTHM);
//        db.execSQL(SQL_CREATE_POEM); // another table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库，删掉原来的表，重新建表
        db.execSQL(SQL_DELETE_POEM);
        db.execSQL(SQL_DELETE_RHYTHM);

        onCreate(db);
    }
}
