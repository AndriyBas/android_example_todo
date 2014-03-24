package com.oyster.DBandContentProviderEx;

/**
 * @author bamboo
 * @since 3/24/14 5:42 PM
 */
public class ToDo {

    private int mID = -1;

    private String mSummary;

    private String mDescription;

    private Category mCategory;

    private ToDo() {
    }

    public ToDo(String summary, String description, Category category) {
        mSummary = summary;
        mDescription = description;
        mCategory = category;
    }

    public ToDo(int id, String summary, String description, Category category) {
        this(summary, description, category);
        mID = id;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
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
}
