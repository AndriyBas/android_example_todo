package com.oyster.DBandContentProviderEx.data.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoTable {

    public static final String TABLE_NAME = "todo";

    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_PARSE_ID = "todo_parseId";
    public static final String COLUMN_SUMMARY = "todo_summary";
    public static final String COLUMN_DESCRIPTION = "todo_description";
    public static final String COLUMN_CATEGORY = "todo_category";
    public static final String COLUMN_PROJECT_ID = "todo_projectId";
    public static final String COLUMN_UPDATED_AT = "todo_updatedAt";

    public static final String[] AVAILABLE_COLUMNS = new String[]{
            COLUMN_ID,
//            COLUMN_PARSE_ID,
            COLUMN_SUMMARY,
            COLUMN_DESCRIPTION,
            COLUMN_CATEGORY,
            COLUMN_PROJECT_ID,
            COLUMN_UPDATED_AT
    };

    public static final String CREATE_TABLE = "create table " +
            TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key, " +
//            COLUMN_PARSE_ID + " text, " +
            COLUMN_PROJECT_ID + " integer, " +
            COLUMN_SUMMARY + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " +
            COLUMN_CATEGORY + " text not null, " +
            COLUMN_UPDATED_AT + " integer not null " +
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
