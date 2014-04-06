package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;
import com.oyster.DBandContentProviderEx.services.ToDoParseUploadService;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoMainFragment;
import com.oyster.DBandContentProviderEx.utils.NavigationDrawerBaseActivity;

public class TodoMainActivity extends NavigationDrawerBaseActivity
        implements ToDoDetailFragment.OnSuicideListener, LoaderManager.LoaderCallbacks<Cursor> {


    public static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    private ProjectCursorAdapter mProjectCursorAdapter;
    private ListView mNavigationDrowerListView;


    public int getFragmentContainerId() {
        return R.id.navigation_drawer_fragment_container;
    }

    //    @Override
    public Fragment createFragment() {
        return new ToDoMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();

        // upload all projects
        getLoaderManager().initLoader(47, null, this);


    }

    private void init() {

        if (getFragmentContainerId() == R.id.navigation_drawer_fragment_container) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.navigation_drawer_fragment_container);
            if (fragment == null) {
                fm.beginTransaction()
                        .add(getFragmentContainerId(), createFragment())
                        .commit();
            }
        }

        mNavigationDrowerListView = (ListView) findViewById(R.id.navigation_drawer_left_layout);

        mProjectCursorAdapter = new ProjectCursorAdapter(this);
        mNavigationDrowerListView.setAdapter(mProjectCursorAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_main_new_project:

                showNewProjectDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewProjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View v = LayoutInflater.from(this).inflate(R.layout.dialog_new_project, null, false);
        builder.setView(v);
        builder.setTitle("New Project");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String summary = ((TextView) v.findViewById(R.id.dialog_new_project_summary))
                        .getText().toString();

                String desc = ((TextView) v.findViewById(R.id.dialog_new_project_desc))
                        .getText().toString();

                if (TextUtils.isEmpty(summary)) {
                    Toast.makeText(getApplicationContext(), "give project a summary", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(ProjectTable.COLUMN_SUMMARY, summary);
                values.put(ProjectTable.COLUMN_DESCRIPTION, desc);

                Uri uri = TodoContentProvider.CONTENT_PROJECT_URI;

                getContentResolver().insert(uri, values);

            }
        });

        builder.show();

    }

    @Override
    public void onFragmentSuicide() {
//        FragmentManager fm = getFragmentManager();
//        while (fm.popBackStackImmediate()) ;
//
//        fm.beginTransaction()
//                .replace(R.id.fragmentContainer, createFragment())
//                .commit();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);

        if (fragment != null && fragment instanceof ToDoDetailFragment) {

            ToDoDetailFragment toDoDetailFragment = (ToDoDetailFragment) fragment;
            if (toDoDetailFragment.isToDoChanged()) {
                toDoDetailFragment.showSaveConfirmationDialog();
                return;
            }
        }
        super.onBackPressed();
    }

    public void synchronizeWithServer() {
        // synchronize with server
        Intent i = new Intent(
                ToDoParseUploadService.ACTION_FETCH_NEW_ITEMS, // action
                null,                                          // uri
                this,                                 // context
                ToDoParseUploadService.class);                 // Service
        startService(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                ProjectTable.COLUMN_ID,
                ProjectTable.COLUMN_SUMMARY,
                ProjectTable.COLUMN_DESCRIPTION
        };

        CursorLoader loader = new CursorLoader(
                this, // context
                TodoContentProvider.CONTENT_PROJECT_URI, // uri
                projection, //projection
                null, // selection
                null, // selection args
                null //sort order
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        assert mProjectCursorAdapter != null;
        mProjectCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        assert mProjectCursorAdapter != null;
        mProjectCursorAdapter.swapCursor(null);
    }


    class ProjectViewHolder {
        public TextView mTextViewSummary;
        public TextView mTextViewDesc;

        public ProjectViewHolder(TextView textViewSummary, TextView textViewDesc) {
            mTextViewSummary = textViewSummary;
            mTextViewDesc = textViewDesc;
        }
    }

    class ProjectCursorAdapter extends CursorAdapter {

        LayoutInflater mLayoutInflater;

        public ProjectCursorAdapter(Context context) {
            super(context, null, 0);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View v = mLayoutInflater.inflate(R.layout.project_row_layout, parent, false);

            ProjectViewHolder holder = new ProjectViewHolder(
                    (TextView) v.findViewById(R.id.project_row_summary),
                    (TextView) v.findViewById(R.id.project_row_desc)
            );

            v.setTag(holder);

            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ProjectViewHolder holder = (ProjectViewHolder) view.getTag();

            holder.mTextViewSummary.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectTable.COLUMN_SUMMARY)));

            holder.mTextViewDesc.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(ProjectTable.COLUMN_DESCRIPTION)));
        }
    }

}
