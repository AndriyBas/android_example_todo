package com.oyster.DBandContentProviderEx;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class TodoContentProvider extends ContentProvider {

    private TodoDatabaseHelper mTodoDatabaseHelper;

    private static final int TODOS = 7;
    private static final int TODO_ID = 47;

    private static final String BASE_PATH = "todos";
    private static final String AUTHORITY = "com.oyster.DBandContentProviderEx";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/todo";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/todo";

    public static final UriMatcher sUriMather = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMather.addURI(AUTHORITY, BASE_PATH, TODOS);
        sUriMather.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);

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

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        sqLiteQueryBuilder.setTables(TodoTable.TABLE_NAME);

        int uriType = sUriMather.match(uri);
        switch (uriType) {
            case TODOS:
                break;
            case TODO_ID:
                // adding the ID to the original query
                sqLiteQueryBuilder.appendWhere(TodoTable.COLUMN_ID + "="
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
        long id = 0L;

        switch (uriType) {
            case TODOS:
                id = sqLiteDatabase.insert(TodoTable.TABLE_NAME, null, values);


                break;
            default:
                throwEx(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Uri resUri = Uri.parse(CONTENT_URI + "/" + id);

        //*********************************************

        Intent i = new Intent("someAction", resUri, getContext(), ParseUploadService.class);
        getContext().startService(i);
        //*********************************************

        return resUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sUriMather.match(uri);

        SQLiteDatabase sqLiteDatabase = mTodoDatabaseHelper.getWritableDatabase();

        int rowsDeleted = 0;

        switch (uriType) {
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
                            TodoTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqLiteDatabase.delete(TodoTable.TABLE_NAME,
                            selection, selectionArgs);
                }
                break;
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
            case TODOS:
                rowsUpdated = sqLiteDatabase.update(
                        TodoTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case TODO_ID:
                String id = uri.getLastPathSegment();
                rowsUpdated = sqLiteDatabase.update(
                        TodoTable.TABLE_NAME,
                        values,
                        TodoTable.COLUMN_ID + "=" + id,
                        null);
                break;
            default:
                throwEx(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    /**
     * Check if projection contains only columns that are in the database
     *
     * @param projection Array of requested columns
     * @throws java.lang.IllegalArgumentException on failure
     */
    private void checkColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(TodoTable.AVAILABLE_COLUMNS));
            if (!availableColumns.containsAll(requestColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    private void throwEx(Uri uri) {
        throw new IllegalArgumentException("Unknown Uri : " + uri);
    }

}
