package com.oyster.DBandContentProviderEx.data.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseRole;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class ProjectTable {

    public static final String TABLE_NAME = "projects";

    public static final String COLUMN_LOCAL_ID = "_id";
    public static final String COLUMN_PARSE_ID = "parseId";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_USER_ID = "userId";

    public static final String[] AVAILABLE_COLUMNS = new String[]{
            COLUMN_LOCAL_ID,
            COLUMN_PARSE_ID,
            COLUMN_SUMMARY,
            COLUMN_DESCRIPTION,
            COLUMN_CATEGORY,
            COLUMN_USER_ID
    };

    public static final String CREATE_TABLE = "create table " +
            TABLE_NAME + " ( " +
            COLUMN_LOCAL_ID + " integer primary key autoincrement, " +
            COLUMN_PARSE_ID + " text, " +
            COLUMN_USER_ID + " text not null, " +
            COLUMN_SUMMARY + " text not null, " +
            COLUMN_DESCRIPTION + " text not null, " +
            COLUMN_CATEGORY + " text not null " +
            ");";

    public static void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(ProjectTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

        ParseRole role = new ParseRole("abc");

        ParseACL parseACL = new ParseACL();



        ParseACL a = new ParseACL();
        a.setRoleReadAccess("", true);
//        parseACL.setRole


        ParseObject o = new ParseObject("ol");
        o.setACL(parseACL);

        parseACL.setRoleWriteAccess(role, true);

    }
}
