package com.oyster.DBandContentProviderEx;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tododatabase.db";
    private static final int DATABASE_VERSION = 3;

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TodoTable.onCreate(db);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TodoTable.onUpgrade(db, oldVersion, newVersion);
    }


}
