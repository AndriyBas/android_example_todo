package com.oyster.DBandContentProviderEx.data.parse;

import com.oyster.DBandContentProviderEx.data.Category;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */

@ParseClassName("ToDo")
public class ToDo extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_LOCAL_ID = "localId";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_PROJECT_ID = "projectIdl";
    public static final String KEY_PROJECT = "project";

    public ToDo() {
    }

    public ToDo(String summary, String description, Category category) {
        setSummary(summary);
        setDescription(description);
        setCategory(category);
    }

    public long getLocalId() {
        return getLong(KEY_LOCAL_ID);
    }

    public void setLocalId(long localId) {
        put(KEY_LOCAL_ID, localId);
    }

    public void setProjectId(long projectId) {
        put(KEY_PROJECT_ID, projectId);
    }

    public long getProjectId() {
        return getLong(KEY_PROJECT_ID);
    }

    public void setProject(Project project) {
        put(KEY_PROJECT, project);
    }

    public ParseObject getProject() {
        return getParseObject(KEY_PROJECT);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getSummary() {
        return getString(KEY_SUMMARY);
    }

    public void setSummary(String summary) {
        put(KEY_SUMMARY, summary);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public Category getCategory() {
        return Category.valueOf(getString(KEY_CATEGORY));
    }

    public void setCategory(Category category) {
        put(KEY_CATEGORY, category.toString());
    }


}
