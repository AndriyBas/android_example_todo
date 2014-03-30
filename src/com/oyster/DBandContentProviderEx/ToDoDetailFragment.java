package com.oyster.DBandContentProviderEx;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

//    private OnSuicideListener mOnSuicideListener;

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mToDoChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private Boolean mToDoChanged = false;

    public static ToDoDetailFragment newInstance(Uri uri, OnSuicideListener listener) {

        ToDoDetailFragment fragment = new ToDoDetailFragment();
//        fragment.setOnSuicideListener(listener);

        if (uri != null) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_TODO_URI, uri);
            fragment.setArguments(args);
        }

        return fragment;
    }

    public boolean isToDoChanged() {
        return mToDoChanged;
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

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setTitle(R.string.action_bar_title_detail_fragment);

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

                if (todo == null) {
                    todo = new ToDo(summary, description, Category.valueOf(category));
                    todo.setID(todoUri.getLastPathSegment());
                }
            }

            cursor.close();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mEditTextSummary.addTextChangedListener(mTextWatcher);
        mEditTextDescription.addTextChangedListener(mTextWatcher);
    }


    @Override
    public void onPause() {

        mEditTextSummary.removeTextChangedListener(mTextWatcher);
        mEditTextDescription.removeTextChangedListener(mTextWatcher);

        if (mEditTextSummary != null) {
            hideSoftKeyboard(mEditTextSummary);
        }
        if (mEditTextDescription != null) {
            hideSoftKeyboard(mEditTextDescription);
        }
        super.onPause();
    }

    private boolean saveData() {

        String category = (String) mSpinnerCategory.getSelectedItem();
        String summary = mEditTextSummary.getText().toString();
        String description = mEditTextDescription.getText().toString();

        if (summary.trim().length() == 0) {
            return false;
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

        mToDoChanged = false;

        return true;
    }


    private int deleteData() {


        if (todoUri == null) {
            return 0;
        }
        Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI + "/" + todoUri.getLastPathSegment());

        int rowsDeleted = getActivity().getContentResolver().delete(uri, null, null);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage("Delete note ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData();
                        Toast.makeText(getActivity(), "ToDo deleted ...", Toast.LENGTH_SHORT)
                                .show();
                        getActivity().onBackPressed();
//                        mOnSuicideListener.onFragmentSuicide();
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public void showSaveConfirmationDialog() {


        mToDoChanged = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Save changes before exiting ?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveAndExitIfNotEmptySummary();
                    }
                });

        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getActivity(), "ToDo was not saved ...", Toast.LENGTH_SHORT);
//                mOnSuicideListener.onFragmentSuicide();
                getActivity().onBackPressed();
            }
        });
        builder.show();
    }

    private void saveAndExitIfNotEmptySummary() {
        if (saveData()) {
            Toast.makeText(getActivity(), "ToDo saved ...", Toast.LENGTH_SHORT)
                    .show();
//            mOnSuicideListener.onFragmentSuicide();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(), "Give ToDo a summary ..", Toast.LENGTH_SHORT)
                    .show();
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
            case android.R.id.home:

                if (mToDoChanged) {
                    showSaveConfirmationDialog();
                } else {
                    getActivity().onBackPressed();
                }

                return true;

            case R.id.menu_todo_detail_save:
                saveAndExitIfNotEmptySummary();
                return true;

            case R.id.menu_todo_detail_delete:

                showDeleteConfirmationDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*
    public OnSuicideListener getOnSuicideListener() {
        return mOnSuicideListener;
    }

    public void setOnSuicideListener(OnSuicideListener onSuicideListener) {
        mOnSuicideListener = onSuicideListener;
    }
*/


    public interface OnSuicideListener {
        public void onFragmentSuicide();
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

/*
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
*/
}
