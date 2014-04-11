package com.oyster.DBandContentProviderEx.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;
import com.oyster.DBandContentProviderEx.utils.Utils;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static TodoDatabaseHelper sTodoDatabaseHelper;

    private static final String DATABASE_NAME = "todo_database.db";
    private static final int DATABASE_VERSION = 1;

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TodoDatabaseHelper getInstance() {
        if (sTodoDatabaseHelper == null) {
            sTodoDatabaseHelper = new TodoDatabaseHelper(Utils.getAppContext());
        }
        return sTodoDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TodoTable.onCreate(db);
        ProjectTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TodoTable.onUpgrade(db, oldVersion, newVersion);
        ProjectTable.onUpgrade(db, oldVersion, newVersion);
    }


}
