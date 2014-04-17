package com.oyster.DBandContentProviderEx.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;
import com.oyster.DBandContentProviderEx.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bamboo
 * @since 4/11/14 5:11 PM
 */
public class Project implements Serializable {

    private long mId = -1;
    private String mSummary = "";
    private String mDescription = "";
    private String mCategory = "";
    private long mUpdatedAt = 0;

    public Project() {
    }

    public Project(long id, String summary, String description, String category) {
        this();
        mId = id;
        mSummary = summary;
        mDescription = description;
        mCategory = category;
    }

    public Project(long id, String summary, String description, String category, long updatedAt) {
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
                c.getString(c.getColumnIndexOrThrow(ProjectTable.COLUMN_CATEGORY)),
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

//        if (getId() != -1) {
//            values.put(ProjectTable.COLUMN_ID, getId());
//        }
        values.put(ProjectTable.COLUMN_SUMMARY, getSummary());
        values.put(ProjectTable.COLUMN_DESCRIPTION, getDescription());
        values.put(ProjectTable.COLUMN_CATEGORY, getCategory());
        values.put(ProjectTable.COLUMN_UPDATED_AT, getUpdatedAt());

        return values;
    }

    /**
     * find cursor by it's id in the database
     *
     * @return
     */
    public static Project getProjectById(long projectId) {


        Uri uri = Uri.parse(TodoContentProvider.CONTENT_PROJECT_URI + "/" + projectId);

        Cursor c = Utils.getAppContext().getContentResolver().query(
                uri,  // Uri
                null, // projection
                null, // selection
                null, // selectionArgs
                null  // sortOrder
        );

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
     * @return
     */
    public static List<Project> getAllProjects() {

        Uri uri = TodoContentProvider.CONTENT_PROJECT_URI;

        Cursor c = Utils.getAppContext().getContentResolver().query(
                uri,  // Uri
                null, // projection
                null, // selection
                null, // selectionArgs
                null  // sortOrder
        );

        ArrayList<Project> projects = new ArrayList<Project>();

        if (c == null) {
            return projects;
        }

        c.moveToFirst();

        while (!c.isAfterLast()) {
            projects.add(fromCursor(c));
            c.moveToNext();
        }

        return projects;
    }

    /**
     * saves current _Project to the database, or updates if it was saved before
     */
    public void save() {

        onUpdated();

        if (getId() == -1) {

            Uri uri = TodoContentProvider.CONTENT_PROJECT_URI;
            Uri resUri = Utils.getAppContext().getContentResolver().insert(uri, toContentValues());
            setId(Long.parseLong(resUri.getLastPathSegment()));

        } else {

            Uri uri = Uri.parse(TodoContentProvider.CONTENT_PROJECT_URI + "/" + getId());

            Utils.getAppContext().getContentResolver().update(
                    uri,                // Uri
                    toContentValues(),  // ContentValues
                    null,               // selection
                    null                // selectionArgs
            );
        }
    }

    /**
     * deletes the _Project from the database
     */
    public void delete() {

        onUpdated();

        // if never saved before
        if (getId() == -1) {
            return;
        }

        Uri uri = Uri.parse(TodoContentProvider.CONTENT_PROJECT_URI + "/" + getId());

        Utils.getAppContext().getContentResolver().delete(
                uri,   // Uri
                null,  // selection
                null   // selectionArgs
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

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

}
