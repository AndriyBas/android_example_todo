package com.oyster.DBandContentProviderEx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.*;

import java.util.ArrayList;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoDetailFragment extends DebugLoggingFragment {

    private ToDo toDo;

    private EditText mEditTextSummary;
    private EditText mEditTextDescription;
    private Spinner mSpinnerCategory;

    private ArrayList<String> categories;

    private static final String KEY_TODO = "com.oyster.todo";

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
    private boolean mNewDataSaved = false;

    public static ToDoDetailFragment newInstance(ToDo toDo) {

        ToDoDetailFragment fragment = new ToDoDetailFragment();

        if (toDo != null) {
            Bundle args = new Bundle();
            args.putSerializable(KEY_TODO, toDo);
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

        super.onCreateView(null, null, null);

        init(v);

        Bundle args = getArguments();

        if (args != null) {
            toDo = (ToDo) args.getSerializable(KEY_TODO);
            fillData();
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


    private void fillData() {

        if (toDo == null) {
            return;
        }

        String category = toDo.getCategory().toString();

        mEditTextSummary.setText(toDo.getSummary());
        mEditTextDescription.setText(toDo.getDescription());

        for (int i = 0; i < mSpinnerCategory.getCount(); i++) {
            if (category.equals(mSpinnerCategory.getItemAtPosition(i))) {
                mSpinnerCategory.setSelection(i);
                break;
            }
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

        if (toDo == null) {
            toDo = new ToDo();
            toDo.setUser(ParseUser.getCurrentUser());
            ParseACL parseACL = new ParseACL();
            parseACL.setReadAccess(ParseUser.getCurrentUser(), true);
            parseACL.setWriteAccess(ParseUser.getCurrentUser(), true);
            toDo.setACL(parseACL);
        }

        toDo.setCategory(Category.valueOf(category));
        toDo.setSummary(summary);
        toDo.setDescription(description);

        toDo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        });

        mToDoChanged = false;
        mNewDataSaved = true;

        return true;
    }


    private int deleteData() {


        if (toDo == null) {
            return 0;
        }

        toDo.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        });

        mToDoChanged = false;

        return 1;
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

                mToDoChanged = false;
                Toast.makeText(getActivity(), "ToDo was not saved ...", Toast.LENGTH_SHORT);
//                mOnSuicideListener.onFragmentSuicide();
                getActivity().onBackPressed();
            }
        });
        builder.show();
    }

    private void saveAndExitIfNotEmptySummary() {

        mToDoChanged = false;

        if (saveData()) {
            Toast.makeText(getActivity(), "ToDo saved ...", Toast.LENGTH_SHORT)
                    .show();
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

        // for debug mode
        super.onOptionsItemSelected(item);

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

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isNewDataSaved() {
        return mNewDataSaved;
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
