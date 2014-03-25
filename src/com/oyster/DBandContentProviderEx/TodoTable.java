package com.oyster.DBandContentProviderEx;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoTable {

    public static final String TABLE_NAME = "todo";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";

    public static final String[] AVAILABLE_COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_SUMMARY,
            COLUMN_DESCRIPTION,
            COLUMN_CATEGORY
    };

    public static final String CREATE_TABLE = "create table " +
            TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SUMMARY + " text not null," +
            COLUMN_DESCRIPTION + " text not null," +
            COLUMN_CATEGORY + " text not null" +
            ");";

    public static void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(TodoTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
