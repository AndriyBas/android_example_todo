package com.oyster.DBandContentProviderEx.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.oyster.DBandContentProviderEx.data.Category;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * @author bamboo
 * @since 4/2/14 9:34 AM
 */
public class ParseUploadService extends IntentService {

    public static final String TAG = "ololo_tag_service";

    public static final String ACTION_INSERT = "insert";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_UPDATE = "update";


    public ParseUploadService() {
        super("ParseUploadService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ParseUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Uri toDoUri = intent.getData();

        Cursor cursor = getContentResolver().query(
                toDoUri, // uri
                null, // projection, null returns all values
                null, // selection
                null, // selection args
                null  // sort order
        );

        if (cursor == null) {
            Log.i(TAG, "cursor == null");
            return;
        }

        cursor.moveToFirst();

        if (cursor.isAfterLast() || cursor.isBeforeFirst()) {
            Log.i(TAG, "empty cursor");
            return;
        }

        Log.i(TAG, "start");

        ToDo toDo = new ToDo();

        toDo.setLocalID(cursor.getInt(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_ID)));

        toDo.setUser(ParseUser.getCurrentUser());

        ParseACL parseACL = new ParseACL();
        parseACL.setReadAccess(ParseUser.getCurrentUser(), true);
        parseACL.setWriteAccess(ParseUser.getCurrentUser(), true);
        toDo.setACL(parseACL);


        toDo.setSummary(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
        toDo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
        toDo.setCategory(Category.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow((TodoTable.COLUMN_CATEGORY)))));

        toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }
}
