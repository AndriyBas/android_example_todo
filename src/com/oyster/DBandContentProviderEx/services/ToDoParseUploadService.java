package com.oyster.DBandContentProviderEx.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import com.oyster.DBandContentProviderEx.ToDoApp;
import com.oyster.DBandContentProviderEx.data.Category;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
import com.oyster.DBandContentProviderEx.data.parse.ParseToDo;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;
import com.parse.*;

import java.util.Date;
import java.util.List;

/**
 * @author bamboo
 * @since 4/2/14 9:34 AM
 */
public class ToDoParseUploadService extends IntentService {

    public static final String TAG = "ololo_tag_service";

    public static final String ACTION_INSERT = "ToDoParseUploadService.insert";
    public static final String ACTION_DELETE = "ToDoParseUploadService.delete";
    public static final String ACTION_UPDATE = "ToDoParseUploadService.update";


    public static final String ACTION_PROJECT_INSERT = "ToDoParseUploadService.project_insert";
    public static final String ACTION_PROJECT_DELETE = "ToDoParseUploadService.project_delete";
    public static final String ACTION_PROJECT_UPDATE = "ToDoParseUploadService.project_update";


    public static final String ACTION_FETCH_NEW_ITEMS = "ToDoParseUploadService.fetch_new_items";


    public static final String KEY_PARSE_ID = "ToDoParseUploadService.parse_id";

    public ToDoParseUploadService() {
        super("ToDoParseUploadService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ToDoParseUploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        switch (intent.getAction()) {
            case ACTION_DELETE:

                deleteData(intent);
                break;

            case ACTION_INSERT:

                insertData(intent);
                break;

            case ACTION_UPDATE:

                updateData(intent);
                break;

            case ACTION_FETCH_NEW_ITEMS:

                // synchronize with server and update date of synchronization
                Log.i(TAG, "Started, time : " +
                        DateUtils.formatDateTime(
                                getApplicationContext(),
                                ToDoApp.getLastServerSynchronizeDate(),
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                        ));


                fetch_new_items();

                break;

            default:
                throw new IllegalArgumentException(TAG + ": Unknown action : " + intent.getAction());
        }
    }

    private void insertData(Intent intent) {

        Uri toDoUri = intent.getData();

        Cursor cursor = fetchCursor(toDoUri);

        Log.i(TAG, "start");

/*
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        String roleName = "todo_role_" + ToDoApp.getCurrentUserId();
        ParseRole role = new ParseRole(roleName, acl);
        role.getUsers().add(ParseUser.getCurrentUser());
        role.saveInBackground();
        */

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        String roleName = "todo_role_" + ToDoApp.getCurrentUserId();
        ParseRole role = new ParseRole(roleName, acl);
        role.getUsers().add(ParseUser.getCurrentUser());
        role.saveInBackground();

        ParseACL parseACL = new ParseACL();
        parseACL.setRoleReadAccess(roleName, true);
        parseACL.setRoleWriteAccess(roleName, true);

        ParseACL.setDefaultACL(parseACL, true);


        ParseToDo parseToDo = new ParseToDo();


        parseToDo.setUser(ParseUser.getCurrentUser());

//        ParseACL parseACL = new ParseACL();
//        parseACL.setRoleReadAccess(roleName, true);
//        parseACL.setRoleWriteAccess(roleName, true);
        parseToDo.setACL(parseACL);

        fillBasicAndSaveToDo(parseToDo, cursor, toDoUri, true);


    }

    private void updateData(Intent intent) {

/*
        final Uri toDoUri = intent.getData();

        final Cursor cursor = fetchCursor(toDoUri);

        Log.i(TAG, "start");

        ParseQuery<ParseToDo> toDoParseQuery = new ParseQuery<ParseToDo>("ParseToDo");
        toDoParseQuery.whereEqualTo("objectId",
                cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_PARSE_ID)));


        try {
            List<ParseToDo> parseToDos = toDoParseQuery.find();
            if (parseToDos == null || parseToDos.size() < 1) {
                Log.e(TAG, "empty list returned from Parse");
            }
            fillBasicAndSaveToDo(parseToDos.get(0), cursor, toDoUri, false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

    }


    /**
     * fetch cursor to current item
     */
    private Cursor fetchCursor(Uri toDoUri) {
        final Cursor cursor = getContentResolver().query(
                toDoUri, // uri
                null, // projection, null returns all values
                null, // selection
                null, // selection args
                null  // sort order
        );
        assert cursor != null;
        cursor.moveToFirst();
        assert !(cursor.isAfterLast() || cursor.isBeforeFirst());
        return cursor;
    }

    /**
     * fill current toDo_ with summary, description and category
     * and save it back in background
     *
     * @param parseToDo   item to fill data with
     * @param cursor holds data to fill
     */
    private void fillBasicAndSaveToDo(final ParseToDo parseToDo, final Cursor cursor, final Uri toDoUri, final boolean updateParseId) {

        parseToDo.setSummary(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
        parseToDo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
        parseToDo.setCategory(Category.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow((TodoTable.COLUMN_CATEGORY)))));

        parseToDo.getACL().setRoleReadAccess("__fuck_like_a_beast", true);

        cursor.close();

        parseToDo.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());

                    return;
                }

                if (updateParseId) {
                    updateParseIdLocally(toDoUri, parseToDo);
                }
            }
        });
    }

    private void updateParseIdLocally(Uri toDoUri, ParseToDo parseToDo) {
/*
        ContentValues values = new ContentValues();
        values.put(TodoTable.COLUMN_PARSE_ID, parseToDo.getObjectId());
        getContentResolver().update(
                toDoUri, // Uri
                values,  // ContentValues
                null,    // String where
                null);   // String[] selectionArgs
*/
    }

    private void deleteData(Intent intent) {


        final Uri toDoUri = intent.getData();

        String parseIdToDelete = intent.getStringExtra(KEY_PARSE_ID);

        Log.i(TAG, "start");

        ParseQuery<ParseToDo> toDoParseQuery = new ParseQuery<ParseToDo>("ParseToDo");
        toDoParseQuery.whereEqualTo("objectId", parseIdToDelete);

        try {
            List<ParseToDo> parseToDos = toDoParseQuery.find();
            // if no items found
            if (parseToDos == null || parseToDos.size() < 1) {
                Log.e(TAG, "empty list returned from Parse");
                return;
            }
            parseToDos.get(0).deleteEventually(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void fetch_new_items() {

        Log.i(TAG, "started sync");

        ParseQuery<ParseToDo> toDoParseQuery = new ParseQuery<ParseToDo>("ParseToDo");
        toDoParseQuery.whereGreaterThanOrEqualTo("updatedAt", new Date(ToDoApp.getLastServerSynchronizeDate()));

        try {
            List<ParseToDo> parseToDos = toDoParseQuery.find();
            // no parseToDos found
            if (parseToDos == null) {
                return;
            }
            updateAllItems(parseToDos);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void updateAllItems(List<ParseToDo> parseToDos) {

        Log.i(TAG, "started update, size : " + parseToDos.size());

        for (int i = 0; i < parseToDos.size(); i++) {
            ParseToDo t = parseToDos.get(i);

            Uri toDoUri = Uri.parse(TodoContentProvider.CONTENT_TODO_URI + "/"
                    + ToDoApp.getCurrentUserId() + "/parseId/" + t.getObjectId());

            ContentValues values = new ContentValues();
            values.put(TodoTable.COLUMN_SUMMARY, t.getSummary());
            values.put(TodoTable.COLUMN_DESCRIPTION, t.getDescription());
            values.put(TodoTable.COLUMN_CATEGORY, t.getCategory().toString());


            // TODO
//            values.put(TodoTable.COLUMN_USER_ID, ToDoApp.getCurrentUserId());

            Cursor c = fetchCursor(toDoUri);

            Log.i(TAG, " i : " + i + " Cursor.count : " + c.getCount());

            if (c.getCount() > 0) {
                getContentResolver().update(
                        toDoUri,
                        values, // ContentValues
                        null, // String where
                        null);  // String[] selectionArgs
            } else {
//                values.put(TodoTable.COLUMN_PARSE_ID, t.getObjectId());
//                getContentResolver().insert(toDoUri, values);
            }
            c.close();
        }

        ToDoApp.setLastServerSynchronizeDate(System.currentTimeMillis());
        Log.i(TAG, "finished, time : " +
                DateUtils.formatDateTime(
                        getApplicationContext(),
                        ToDoApp.getLastServerSynchronizeDate(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                ));
    }
}
