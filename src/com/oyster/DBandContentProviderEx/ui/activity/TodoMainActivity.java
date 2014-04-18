package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.ToDoApp;
import com.oyster.DBandContentProviderEx.data.Project;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
import com.oyster.DBandContentProviderEx.data.table.ProjectTable;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoMainFragment;
import com.oyster.DBandContentProviderEx.utils.NavigationDrawerBaseActivity;
import com.parse.ParseUser;

public class TodoMainActivity extends NavigationDrawerBaseActivity
        implements ToDoDetailFragment.OnSuicideListener, LoaderManager.LoaderCallbacks<Cursor> {


    public static final String TAG_DETAIL_FRAGMENT = "detail_fragment";

    private ProjectCursorAdapter mProjectCursorAdapter;
    private ListView mNavigationDrawerListView;


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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        /*if (getFragmentContainerId() == R.id.navigation_drawer_fragment_container) {
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.navigation_drawer_fragment_container);
            if (fragment == null) {
                fm.beginTransaction()
                        .add(getFragmentContainerId(), createFragment())
                        .commit();
            }
        }*/

        mNavigationDrawerListView = (ListView) findViewById(R.id.navigation_drawer_left_layout);

        mProjectCursorAdapter = new ProjectCursorAdapter(this);
        mNavigationDrawerListView.setAdapter(mProjectCursorAdapter);

        mNavigationDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onProjectClicked(position, id);
            }
        });

        mNavigationDrawerListView.callOnClick();

    }

    private void onProjectClicked(int position, long id) {

        FragmentManager fm = getSupportFragmentManager();
        while (fm.popBackStackImmediate()) ;

        fm.beginTransaction()

                .replace(getFragmentContainerId(), ToDoMainFragment.newInstance(id), TAG_DETAIL_FRAGMENT)
                .commit();

        closeAllDrawers();

        // set the title to the name of the project
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_main_new_project:

                showNewProjectDialog(null);
                return true;


            case R.id.menu_main_sign_out:

                // keep track of time user was last log in in order to effectively fetch data later
                // from server's database
                ToDoApp.setLastUserSessionDate(System.currentTimeMillis());

                ParseUser.logOut();

                Intent i = new Intent(this, DispatchActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                this.finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewProjectDialog(final Project p) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View v = LayoutInflater.from(this).inflate(R.layout.dialog_new_project, null, false);

        final TextView textViewSummary = (TextView) v.findViewById(R.id.dialog_new_project_summary);
        final TextView textViewDescription = (TextView) v.findViewById(R.id.dialog_new_project_desc);

        // populate fields if project was clicked
        if (p != null) {
            textViewSummary.setText(p.getSummary());
            textViewDescription.setText(p.getDescription());
            builder.setTitle("Project : " + p.getSummary());
        } else {
            builder.setTitle("New Parse Project");
        }
        builder.setView(v);


        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (p != null) {
                    Toast.makeText(TodoMainActivity.this, "Project : " + p.getSummary() + " deleted !", Toast.LENGTH_SHORT)
                            .show();
//                    p.delete();
                }

            }
        });


        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String summary = textViewSummary.getText().toString();

                String desc = textViewDescription.getText().toString();

                if (TextUtils.isEmpty(summary)) {
                    Toast.makeText(getApplicationContext(), "give project a summary", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                Project proj = p;

                if (proj == null) {
                    proj = new Project();
                }
                proj.setSummary(summary);
                proj.setDescription(desc);

                proj.save();

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

        FragmentManager fm = getSupportFragmentManager();
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
//        Intent i = new Intent(
//                ToDoParseUploadService.ACTION_FETCH_NEW_ITEMS, // action
//                null,                                          // uri
//                this,                                 // context
//                ToDoParseUploadService.class);                 // Service
//        startService(i);
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

        setListView();
    }

    private void setListView() {


        if (mNavigationDrawerListView.getCount() == 0) {
            Project p = new Project();
            p.setSummary("Default Project");
            p.setDescription("Auto generated ");
            p.setCategory("Default");
            p.save();
        }

        onProjectClicked(0, mNavigationDrawerListView.getItemIdAtPosition(0));
        mNavigationDrawerListView.setSelection(0);

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

            ImageButton imageButton = (ImageButton) v.findViewById(R.id.project_row_img_edit);
            assert imageButton != null;

            long projectId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(ProjectTable.COLUMN_ID));

            // the tag must be unique so it has to be created in resources files to guarantee uniqueness
            imageButton.setTag(R.integer.project_image_edit_btn, projectId);

            imageButton.setOnClickListener(mProjectEditOnClickListener);

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

    private final View.OnClickListener mProjectEditOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            long id = (long) v.getTag(R.integer.project_image_edit_btn);

            showNewProjectDialog(Project.getProjectById(id));
//            Toast.makeText(TodoMainActivity.this, "Project : " + id.toString(), Toast.LENGTH_SHORT)
//                    .show();


        }
    };


}
