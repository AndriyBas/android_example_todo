package com.oyster.DBandContentProviderEx.data.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.oyster.DBandContentProviderEx.data.database.TodoDatabaseHelper;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;
import com.oyster.DBandContentProviderEx.services.ToDoParseUploadService;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoContentProvider extends ContentProvider {

    private TodoDatabaseHelper mTodoDatabaseHelper;

    private static final int TODOS_PROJECT = 4;
    private static final int TODOS_PROJECT_toDoId = 7;

    private static final int PROJECTS = 47;
    private static final int PROJECTS_projectId = 74;


    private static final String BASE_PATH_TODO = "todos";
    private static final String BASE_PATH_PROJECT = "projects";

    private static final String AUTHORITY = "com.oyster.DBandContentProviderEx";

    public static final Uri CONTENT_TODO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_TODO);
    public static final Uri CONTENT_PROJECT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_PROJECT);

//    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
//            + "/todo_";
//    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
//            + "/todo_";

    private static final UriMatcher sUriMather = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMather.addURI(AUTHORITY, BASE_PATH_TODO + "/#", TODOS_PROJECT);
        sUriMather.addURI(AUTHORITY, BASE_PATH_TODO + "/#" + "/toDoId/#", TODOS_PROJECT_toDoId);

        sUriMather.addURI(AUTHORITY, BASE_PATH_PROJECT, PROJECTS);
        sUriMather.addURI(AUTHORITY, BASE_PATH_PROJECT + "/#", PROJECTS_projectId);
    }


    @Override
    public boolean onCreate() {
        mTodoDatabaseHelper = new TodoDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

//      Used only when recreating the table
//        TodoTable.onUpgrade(mTodoDatabaseHelper.getWritableDatabase(), 5, 6);

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();


        int uriType = sUriMather.match(uri);
        switch (uriType) {

            case TODOS_PROJECT:

                checkColumns(projection, TodoTable.AVAILABLE_COLUMNS);

                // search in TodoTable
                sqLiteQueryBuilder.setTables(TodoTable.TABLE_NAME);

                sqLiteQueryBuilder.appendWhere(TodoTable.COLUMN_PROJECT_ID + "="
                        + uri.getLastPathSegment() + "");
                break;

            case TODOS_PROJECT_toDoId:

                checkColumns(projection, TodoTable.AVAILABLE_COLUMNS);

                // also search in TodoTable
                sqLiteQueryBuilder.setTables(TodoTable.TABLE_NAME);

                // ToDos ID is unique, so no need to add constrain for PROJECT_ID
                sqLiteQueryBuilder.appendWhere(TodoTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;

            case PROJECTS:

                checkColumns(projection, ProjectTable.AVAILABLE_COLUMNS);

                // search in ProjectTable
                sqLiteQueryBuilder.setTables(ProjectTable.TABLE_NAME);

                // TODO need to add some logic so that current user have acess only to it's own projects

                break;

            case PROJECTS_projectId:

                checkColumns(projection, ProjectTable.AVAILABLE_COLUMNS);

                // search in ProjectTable
                sqLiteQueryBuilder.setTables(ProjectTable.TABLE_NAME);

                sqLiteQueryBuilder.appendWhere(ProjectTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;

            default:
                throwEx(uri);
        }


        SQLiteDatabase db = mTodoDatabaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteQueryBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sUriMather.match(uri);

        SQLiteDatabase sqLiteDatabase = mTodoDatabaseHelper.getWritableDatabase();

        // id of the inserted value in case of success
        long id = System.currentTimeMillis();
        Log.i("ololo", "Current time millis : " + id);
        // Uri to the new inserted row
        Uri resUri = null;

        switch (uriType) {

            case PROJECTS:

                values.put(ProjectTable.COLUMN_ID, id);
                values.put(ProjectTable.COLUMN_UPDATED_AT, System.currentTimeMillis());

                id = sqLiteDatabase.insert(ProjectTable.TABLE_NAME, null, values);
                resUri = Uri.parse(uri + "/" + id);
                Log.i("ololo", "id after insert : " + id);

//                runToDoService(ToDoParseUploadService.ACTION_INSERT, Uri.parse(uri + "/localId/" + id));
                break;

            case TODOS_PROJECT:

                values.put(TodoTable.COLUMN_ID, id);
                values.put(TodoTable.COLUMN_UPDATED_AT, System.currentTimeMillis());
                values.put(TodoTable.COLUMN_PROJECT_ID, Long.parseLong(uri.getLastPathSegment()));

                id = sqLiteDatabase.insert(TodoTable.TABLE_NAME, null, values);
                resUri = Uri.parse(uri + "/toDoId/" + id);
                Log.i("ololo", "id after insert : " + id);
//                runProjectService(ProjectParseUploadService.ACTION_INSERT, Uri.parse(uri + "/localId/" + id));
                break;

            default:
                throwEx(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Log.i(ToDoParseUploadService.TAG, String.valueOf(id));

        return resUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sUriMather.match(uri);

        SQLiteDatabase sqLiteDatabase = mTodoDatabaseHelper.getWritableDatabase();

        int rowsDeleted = 0;


        switch (uriType) {

            case TODOS_PROJECT_toDoId:
                String id = uri.getLastPathSegment();

//                if (TextUtils.isEmpty(selection)) {
                rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
                        TodoTable.COLUMN_ID + "=" + id, null);
//                } else {
//                    rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
//                            selection, selectionArgs);
//                }


//                Intent i = new Intent(ToDoParseUploadService.ACTION_DELETE, uri, getContext(), ToDoParseUploadService.class);
//                i.putExtra(ToDoParseUploadService.KEY_PARSE_ID, parseIdToDelete);
//                getContext().startService(i);


                break;

            // this better not be called
//            case PROJECTS_projectId:
//                String userId = uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
//                            TodoTable.COLUMN_USER_ID + "='" + userId + "'", null);
//                } else {
//                    rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
//                            selection, selectionArgs);
//                }
//                break;

            default:
                throwEx(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return rowsDeleted;
    }

    @Override

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {


        int uriType = sUriMather.match(uri);

        SQLiteDatabase sqLiteDatabase =
                mTodoDatabaseHelper.getWritableDatabase();
        assert sqLiteDatabase != null;

        int rowsUpdated = 0;


        switch (uriType) {

            case TODOS_PROJECT_toDoId:

                String toDoId = uri.getLastPathSegment();
                rowsUpdated = sqLiteDatabase.update(
                        TodoTable.TABLE_NAME,
                        values,
                        TodoTable.COLUMN_ID + "=" + toDoId,
                        null);

//                runToDoService(ToDoParseUploadService.ACTION_UPDATE, uri);

                break;

            case PROJECTS_projectId:

                String projectId = uri.getLastPathSegment();

                rowsUpdated = sqLiteDatabase.update(
                        ProjectTable.TABLE_NAME,
                        values,
                        ProjectTable.COLUMN_ID + "=" + projectId,
                        null);
                break;

            default:
                throwEx(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }


    public static boolean matchTODOS_PROJECT(Uri uri) {
        return sUriMather.match(uri) == TODOS_PROJECT;
    }

    /**
     * Check if projection contains only columns that are in the database
     *
     * @param projection Array of requested columns
     * @throws java.lang.IllegalArgumentException on failure
     */
    private void checkColumns(String[] projection, String[] columns) {
        if (projection != null) {
            HashSet<String> requestColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(columns));
            if (!availableColumns.containsAll(requestColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    private void throwEx(Uri uri) {
        throw new IllegalArgumentException("Unknown Uri : " + uri);
    }

    private void runToDoService(String action, Uri uri) {
        //*********************************************
        Intent i = new Intent(action, uri, getContext(), ToDoParseUploadService.class);
        getContext().startService(i);
        //*********************************************
    }

}
