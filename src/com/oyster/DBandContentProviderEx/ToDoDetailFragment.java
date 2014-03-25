package com.oyster.DBandContentProviderEx;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoDetailFragment extends Fragment {

    private ToDo todo;

    private Uri todoUri;

    private EditText mEditTextSummary;
    private EditText mEditTextDescription;
    private Spinner mSpinnerCategory;
    private ArrayList<String> categories;

    private static final String KEY_TODO_URI = "com.oyster.todo_uri";

    private OnSuicideListener mOnSuicideListener;


    public static ToDoDetailFragment newInstance(Uri uri, OnSuicideListener listener) {

        ToDoDetailFragment fragment = new ToDoDetailFragment();
        fragment.setOnSuicideListener(listener);

        if (uri != null) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_TODO_URI, uri);
            fragment.setArguments(args);
        }

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.todo_detail_layout, container, false);

        init(v);

        Bundle args = getArguments();

        if (args != null) {
            todoUri = (Uri) args.getParcelable(KEY_TODO_URI);
            fillData(todoUri);
        }

        return v;
    }

    private void init(View v) {

        mEditTextSummary = (EditText) v.findViewById(R.id.todo_edit_summary);
        mEditTextDescription = (EditText) v.findViewById(R.id.todo_edit_description);
        mSpinnerCategory = (Spinner) v.findViewById(R.id.todo_edit_category);

        categories = new ArrayList<String>();
        for (Category c : Category.values()) {
            categories.add(c.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                categories);

        mSpinnerCategory.setAdapter(adapter);
    }


    private void fillData(Uri todoUri) {

        String[] projection = new String[]{
                TodoTable.COLUMN_SUMMARY,
                TodoTable.COLUMN_DESCRIPTION,
                TodoTable.COLUMN_CATEGORY
        };

        Cursor cursor = getActivity().getContentResolver().query(
                todoUri,
                projection,
                null,
                null,
                null);

        if (cursor != null) {

            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {


                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY));

                String summary = cursor.getString(
                        cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY));

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION));

                mEditTextSummary.setText(summary);
                mEditTextDescription.setText(description);

                for (int i = 0; i < mSpinnerCategory.getCount(); i++) {
                    if (category.equals(mSpinnerCategory.getItemAtPosition(i))) {
                        mSpinnerCategory.setSelection(i);
                        break;
                    }
                }

            }

            cursor.close();
        }
    }


    private void saveData() {

        String category = (String) mSpinnerCategory.getSelectedItem();
        String summary = mEditTextSummary.getText().toString();
        String description = mEditTextDescription.getText().toString();

        if (summary.trim().length() == 0) {
            // TODO nitify user and prevent from exiting
            return;
        }

        ContentValues values = new ContentValues();

        values.put(TodoTable.COLUMN_CATEGORY, category);
        values.put(TodoTable.COLUMN_DESCRIPTION, description);
        values.put(TodoTable.COLUMN_SUMMARY, summary);

        if (todoUri == null) {
            todoUri = getActivity().getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
        } else {
            getActivity().getContentResolver().update(todoUri, values, null, null);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_todo_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_todo_detail_save:

                saveData();
                hideSoftKeyboard(mEditTextSummary);
                hideSoftKeyboard(mEditTextDescription);
                mOnSuicideListener.onFragmentSuicide();

                return true;

            case R.id.menu_todo_detail_delete:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public OnSuicideListener getOnSuicideListener() {
        return mOnSuicideListener;
    }

    public void setOnSuicideListener(OnSuicideListener onSuicideListener) {
        mOnSuicideListener = onSuicideListener;
    }


    public interface OnSuicideListener {
        public void onFragmentSuicide();
    }

    private void hideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity != null) {
            try {
                mOnSuicideListener = (OnSuicideListener) activity;
            } catch (ClassCastException ex) {
                ex.printStackTrace();
            }
        }
    }
}
