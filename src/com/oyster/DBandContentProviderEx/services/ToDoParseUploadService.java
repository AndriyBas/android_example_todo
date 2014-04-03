package com.oyster.DBandContentProviderEx.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.oyster.DBandContentProviderEx.ToDoApplication;
import com.oyster.DBandContentProviderEx.data.Category;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
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
    public static final String ACTION_FETCH_NEW_ITEMS = "ToDoParseUploadService.fetch_new_items";


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
            default:
                throw new IllegalArgumentException(TAG + ": Unknown action : " + intent.getAction());
        }
    }

    private void insertData(Intent intent) {

        Uri toDoUri = intent.getData();


        Cursor cursor = getContentResolver().query(
                toDoUri, // uri
                null, // projection, null returns all values
                null, // selection
                null, // selection args
                null  // sort order
        );

        assert cursor != null;

        cursor.moveToFirst();

        assert !(cursor.isAfterLast() || cursor.isBeforeFirst());

        Log.i(TAG, "start");

        ToDo toDo = new ToDo();

        Log.i(TAG, toDo.getObjectId());

        toDo.setUser(ParseUser.getCurrentUser());

        ParseACL parseACL = new ParseACL();
        parseACL.setReadAccess(ParseUser.getCurrentUser(), true);
        parseACL.setWriteAccess(ParseUser.getCurrentUser(), true);
        toDo.setACL(parseACL);

        fillBasicAndSaveToDo(toDo, cursor);
    }

    private void updateData(Intent intent) {

        final Uri toDoUri = intent.getData();

        ParseQuery<ToDo> toDoParseQuery = new ParseQuery<ToDo>("ToDo");
        toDoParseQuery.whereEqualTo("objectId", toDoUri.getLastPathSegment());

        toDoParseQuery.findInBackground(new FindCallback<ToDo>() {
            @Override
            public void done(List<ToDo> toDos, ParseException e) {
                // if exception occurred
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                // if no items found
                if (toDos == null || toDos.size() < 1) {
                    Log.e(TAG, "empty list returned from Parse");
                }

                fillBasicAndSaveToDo(toDos.get(0), fetchCursor(toDoUri));
            }
        });
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
     * @param _toDo  item to fill data with
     * @param cursor holds data to fill
     */
    private void fillBasicAndSaveToDo(ToDo _toDo, Cursor cursor) {

        _toDo.setSummary(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
        _toDo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
        _toDo.setCategory(Category.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow((TodoTable.COLUMN_CATEGORY)))));

        _toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

    private void deleteData(Intent intent) {

        Uri toDoUri = intent.getData();

        ParseQuery<ToDo> toDoParseQuery = new ParseQuery<ToDo>("ToDo");
        toDoParseQuery.whereEqualTo("objectId", toDoUri.getLastPathSegment());

        toDoParseQuery.findInBackground(new FindCallback<ToDo>() {
            @Override
            public void done(List<ToDo> toDos, ParseException e) {
                // if exception occurred
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                // if no items found
                if (toDos == null || toDos.size() < 1) {
                    Log.e(TAG, "empty list returned from Parse");
                }

                toDos.get(0).deleteEventually(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

    }


    private void fetch_new_items(Intent intent) {


        ParseQuery<ToDo> toDoParseQuery = new ParseQuery<ToDo>("ToDo");
        toDoParseQuery.whereGreaterThanOrEqualTo("updateAt", new Date(ToDoApplication.getLastUserSessionDate()));

        toDoParseQuery.findInBackground(new FindCallback<ToDo>() {
            @Override
            public void done(List<ToDo> toDos, ParseException e) {
                // error occurred
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                // no toDos found
                if (toDos == null) {
                    return;
                }


                updateAllItems(toDos);

            }
        });


    }

    private void updateAllItems(List<ToDo> toDos) {

        for (int i = 0; i < toDos.size(); i++) {
            ToDo t = toDos.get(i);

//            Uri toDoUri = Uri.parse(TodoContentProvider.CONTENT_URI + "/"
//                    + ToDoApplication.getCurrentUserId() + "/" + t.getLocalID());

            ContentValues values = new ContentValues();
            values.put(TodoTable.COLUMN_SUMMARY, t.getSummary());
            values.put(TodoTable.COLUMN_DESCRIPTION, t.getDescription());
            values.put(TodoTable.COLUMN_CATEGORY, t.getCategory().toString());

            getContentResolver().update(
                    Uri.parse(TodoContentProvider.CONTENT_URI + ""), // Uri
                    values, // ContentValues
                    null, // String where
                    null  // String[] selectionArgs
            );
        }

    }

}
