package com.oyster.DBandContentProviderEx.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.oyster.DBandContentProviderEx.data.database.TodoDatabaseHelper;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;

/**
 * @author bamboo
 * @since 4/11/14 5:11 PM
 */
public class Project {

    private long mId = -1;
    private String mSummary = "";
    private String mDescription = "";
    private Category mCategory = Category.Remainder;
    private long mUpdatedAt = 0;

    public Project() {
    }

    public Project(long id, String summary, String description, Category category) {
        this();
        mId = id;
        mSummary = summary;
        mDescription = description;
        mCategory = category;
    }

    public Project(long id, String summary, String description, Category category, long updatedAt) {
        this(id, summary, description, category);
        mUpdatedAt = updatedAt;
    }

    /**
     * Convert data from cursor to new Project
     *
     * @param c
     * @return
     */
    private static Project fromCursor(Cursor c) {

        if (c == null) {
            return null;
        }

        Project project = new Project(
                c.getLong(c.getColumnIndexOrThrow(ProjectTable.COLUMN_ID)),
                c.getString(c.getColumnIndexOrThrow(ProjectTable.COLUMN_SUMMARY)),
                c.getString(c.getColumnIndexOrThrow(ProjectTable.COLUMN_DESCRIPTION)),
                Category.valueOf(c.getString(c.getColumnIndexOrThrow(ProjectTable.COLUMN_CATEGORY))),
                c.getLong(c.getColumnIndexOrThrow(ProjectTable.COLUMN_UPDATED_AT))
        );

        return project;
    }

    /**
     * puts all  values int ContentValues class
     *
     * @return
     */
    private ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(ProjectTable.COLUMN_SUMMARY, getSummary());
        values.put(ProjectTable.COLUMN_DESCRIPTION, getDescription());
        values.put(ProjectTable.COLUMN_CATEGORY, getCategory().toString());
        values.put(ProjectTable.COLUMN_UPDATED_AT, getUpdatedAt());

        return values;
    }

    /**
     * find Project by it's id in the database
     *
     * @param id
     * @return
     */
    public static Project getById(long id) {

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getReadableDatabase();
        assert sqLiteDB != null;

        Cursor c = sqLiteDB.query(
                ProjectTable.TABLE_NAME,               // TableName
                null,                               // String[] projection
                ProjectTable.COLUMN_ID + "= ?",        // String selection
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
     * saves current Project to the database, or updates if it was saved before
     */
    public void save() {

        onUpdated();

        SQLiteDatabase sqLiteDB = TodoDatabaseHelper.getInstance().getWritableDatabase();
        assert sqLiteDB != null;

        if (getId() == -1) {
            long id = sqLiteDB.insert(
                    ProjectTable.TABLE_NAME,  // String tableName
                    null,                  // String nullColumnHuck
                    toContentValues());    // ContentValues
            setId(id);
        } else {
            sqLiteDB.update(
                    ProjectTable.TABLE_NAME,                       // String tableName
                    toContentValues(),                          // ContentValues
                    ProjectTable.COLUMN_ID + "= ?",                // String selection
                    new String[]{String.valueOf(getId())}       // String[] selectionArgs
            );
        }
    }

    /**
     * deletes the Project from the database
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
                ProjectTable.TABLE_NAME,                   // String tableName
                ProjectTable.COLUMN_ID + "= ?",            // String selection
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
}
