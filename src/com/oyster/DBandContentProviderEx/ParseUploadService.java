package com.oyster.DBandContentProviderEx;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * @author bamboo
 * @since 4/2/14 9:34 AM
 */
public class ParseUploadService extends IntentService {

    public static final String KEY_TODO = "com.oyster.ParseUploadService.ToDo";

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
            return;
        }

        cursor.moveToFirst();

        if (cursor.isAfterLast() || cursor.isBeforeFirst()) {
            return;
        }

        Log.i("ololo_service", "start");

        ToDo toDo = new ToDo();
        toDo.setLocalID(String.valueOf(id));
        toDo.setUser(ParseUser.getCurrentUser());
        ParseACL parseACL = new ParseACL();
        parseACL.setReadAccess(ParseUser.getCurrentUser(), true);
        parseACL.setWriteAccess(ParseUser.getCurrentUser(), true);
        toDo.setACL(parseACL);
        toDo.setSummary(values.getAsString(TodoTable.COLUMN_SUMMARY));
        toDo.setDescription(values.getAsString(TodoTable.COLUMN_DESCRIPTION));
        toDo.setCategory(Category.valueOf(values.getAsString(TodoTable.COLUMN_CATEGORY)));


        toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        });

    }
}
