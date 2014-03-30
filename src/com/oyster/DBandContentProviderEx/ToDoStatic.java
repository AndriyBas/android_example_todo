package com.oyster.DBandContentProviderEx;

/**
 * @author bamboo
 * @since 3/30/14 8:50 PM
 */
public class ToDoStatic {


    private String mSummary;

    private String mDescription;

    private Category mCategory;


    public ToDoStatic(String summary, String description, Category category) {
        mSummary = summary;
        mDescription = description;
        mCategory = category;
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
