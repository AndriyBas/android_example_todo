package com.oyster.DBandContentProviderEx.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.oyster.DBandContentProviderEx.data.database.TodoDatabaseHelper;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bamboo
 * @since 4/11/14 5:10 PM
 */
public class ToDo implements Serializable {

    private long mId = -1;
    private long mProjectId = -1;
    private String mSummary = "";
    private String mDescription = "";
    private Category mCategory = Category.Remainder;
    private long mUpdatedAt = 0;

    public ToDo() {
    }

    public ToDo(long id, long projectId, String summary, String description, Category category) {
        this();
        mId = id;
        mProjectId = projectId;
        mSummary = summary;
        mDescription = description;
        mCategory = category;
    }

    public ToDo(long id, long projectId, String summary, String description, Category category, long updatedAt) {
        this(id, projectId, summary, description, category);
        mUpdatedAt = updatedAt;
    }

    /**
     * Convert data from cursor to new _ToDo
     *
     * @param c
     * @return
     */
    private static ToDo fromCursor(Cursor c) {

        if (c == null) {
            return null;
        }

        ToDo toDo = new ToDo(
                c.getLong(c.getColumnIndexOrThrow(TodoTable.COLUMN_ID)),
                c.getLong(c.getColumnIndexOrThrow(TodoTable.COLUMN_PROJECT_ID)),
                c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)),
                c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)),
                Category.valueOf(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY))),
                c.getLong(c.getColumnIndexOrThrow(TodoTable.COLUMN_UPDATED_AT))
        );

        return toDo;
    }

    /**
     * puts all  values int ContentValues class
     *
     * @return
     */
    private ContentValues toContentValues() {
        ContentValues values = new ContentValues();

//        if (getId() != -1) {
//            values.put(TodoTable.COLUMN_ID, getId());
//        }
        values.put(TodoTable.COLUMN_SUMMARY, getSummary());
        values.put(TodoTable.COLUMN_DESCRIPTION, getDescription());
        values.put(TodoTable.COLUMN_CATEGORY, getCategory().toString());
        values.put(TodoTable.COLUMN_UPDATED_AT, getUpdatedAt());
        values.put(TodoTable.COLUMN_PROJECT_ID, getProjectId());

        return values;
    }

    /**
     * find cursor by it's id in the database
     *
     * @param id
     * @return
     */
    public static ToDo getById(long id) {

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getReadableDatabase();
        assert sqLiteDB != null;

        Cursor c = sqLiteDB.query(
                TodoTable.TABLE_NAME,               // TableName
                null,                               // String[] projection
                TodoTable.COLUMN_ID + "= ?",        // String selection
                new String[]{String.valueOf(id)},  // String[] selectionArgs
                null,                               // String groupBy
                null,                               // String having
                null);                              // String sortOrder

        if (c == null) {
            return null;
        }

        c.moveToFirst();

        if (c.isBeforeFirst() || c.isAfterLast()) {
            return null;
        }


        return fromCursor(c);
    }


    /**
     * find all ToDos in current project
     *
     * @param projectId
     * @return
     */
    public static List<ToDo> getByProjectId(long projectId) {

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getReadableDatabase();
        assert sqLiteDB != null;

        Cursor c = sqLiteDB.query(
                TodoTable.TABLE_NAME,                       // TableName
                null,                                       // String[] projection
                TodoTable.COLUMN_PROJECT_ID + "= ?",        // String selection
                new String[]{String.valueOf(projectId)},    // String[] selectionArgs
                null,                                       // String groupBy
                null,                                       // String having
                TodoTable.COLUMN_UPDATED_AT + " asc");

        ArrayList<ToDo> toDos = new ArrayList<ToDo>();

        if (c == null) {
            return toDos;
        }

        c.moveToFirst();

        while (!c.isAfterLast()) {
            toDos.add(fromCursor(c));
            c.moveToNext();
        }

        return toDos;
    }

    /**
     * saves current _ToDo to the database, or updates if it was saved before
     */
    public void save() {

        onUpdated();

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getWritableDatabase();
        assert sqLiteDB != null;

        if (getId() == -1) {
            long id = sqLiteDB.insert(
                    TodoTable.TABLE_NAME,  // String tableName
                    null,                  // String nullColumnHuck
                    toContentValues());    // ContentValues
            setId(id);
        } else {
            sqLiteDB.update(
                    TodoTable.TABLE_NAME,                       // String tableName
                    toContentValues(),                          // ContentValues
                    TodoTable.COLUMN_ID + "= ?",                // String selection
                    new String[]{String.valueOf(getId())}       // String[] selectionArgs
            );
        }
    }

    /**
     * deletes the _ToDo from the database
     */
    public void delete() {

        onUpdated();

        // if never saved before
        if (getId() == -1) {
            return;
        }

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getWritableDatabase();
        assert sqLiteDB != null;

        sqLiteDB.delete(
                TodoTable.TABLE_NAME,                   // String tableName
                TodoTable.COLUMN_ID + "= ?",            // String selection
                new String[]{String.valueOf(getId())}   // String[] selectionArgs
        );
    }

    private void onUpdated() {
        mUpdatedAt = System.currentTimeMillis();
    }


    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public long getProjectId() {
        return mProjectId;
    }

    public void setProjectId(long projectId) {
        mProjectId = projectId;
    }
}
