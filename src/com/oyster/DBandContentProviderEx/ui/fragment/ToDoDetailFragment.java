package com.oyster.DBandContentProviderEx.ui.fragment;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.Category;
import com.oyster.DBandContentProviderEx.data.ToDo;

import java.util.ArrayList;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoDetailFragment extends Fragment {

    private ToDo mToDo;

    private EditText mEditTextSummary;
    private EditText mEditTextDescription;
    private Spinner mSpinnerCategory;
    private ArrayList<String> categories;

    private static final String KEY_TOD0 = "com.oyster.todo";

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

    public static ToDoDetailFragment newInstance(ToDo toDo, OnSuicideListener listener) {

        ToDoDetailFragment fragment = new ToDoDetailFragment();
//        fragment.setOnSuicideListener(listener);

        if (toDo == null) {
            toDo = new ToDo();
        }

        Bundle args = new Bundle();
        args.putSerializable(KEY_TOD0, toDo);
        fragment.setArguments(args);

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
            mToDo = (ToDo) args.getSerializable(KEY_TOD0);
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

        mEditTextSummary.setText(mToDo.getSummary());
        mEditTextDescription.setText(mToDo.getDescription());
        String category = mToDo.getCategory().toString();

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

        if (TextUtils.isEmpty(mEditTextSummary.getText())) {
            return false;
        }

        String category = (String) mSpinnerCategory.getSelectedItem();
        String summary = mEditTextSummary.getText().toString();
        String description = mEditTextDescription.getText().toString();


        mToDo.setSummary(summary);
        mToDo.setDescription(description);
        mToDo.setCategory(Category.valueOf(category));

        mToDo.save();

        mToDoChanged = false;


        return true;
    }


    private void deleteData() {
        mToDo.delete();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage("Delete note ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mToDoChanged = false;
                        deleteData();
                        Toast.makeText(getActivity(), "ParseToDo deleted ...", Toast.LENGTH_SHORT)
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

                Toast.makeText(getActivity(), "ParseToDo was not saved ...", Toast.LENGTH_SHORT);
//                mOnSuicideListener.onFragmentSuicide();
                mToDoChanged = false;
                getActivity().onBackPressed();
            }
        });
        builder.show();
    }

    private void saveAndExitIfNotEmptySummary() {
        if (saveData()) {
            Toast.makeText(getActivity(), "ParseToDo saved ...", Toast.LENGTH_SHORT)
                    .show();
//            mOnSuicideListener.onFragmentSuicide();
            mToDoChanged = false;
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(), "Give ParseToDo a summary ..", Toast.LENGTH_SHORT)
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
