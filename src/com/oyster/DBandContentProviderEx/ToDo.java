package com.oyster.DBandContentProviderEx;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */

@ParseClassName("ToDo")
public class ToDo extends ParseObject {

    public static final String KEY_ID = "_id";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";

    private ToDo() {
    }

    public ToDo(String summary, String description, Category category) {
        setSummary(summary);
        setDescription(description);
        setCategory(category);
    }


    public ToDo(String id, String summary, String description, Category category) {
        this(summary, description, category);
        setID(id);
    }

    public String getID() {
        return getString(KEY_ID);
    }

    public void setID(String ID) {
        put(KEY_ID, ID);
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
