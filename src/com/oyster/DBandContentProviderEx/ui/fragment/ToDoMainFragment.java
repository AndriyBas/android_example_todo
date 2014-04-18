package com.oyster.DBandContentProviderEx.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.Category;
import com.oyster.DBandContentProviderEx.data.Project;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.data.contentprovider.TodoContentProvider;
import com.oyster.DBandContentProviderEx.data.table.TodoTable;
import com.oyster.DBandContentProviderEx.ui.activity.ToDoDetailActivity;
import com.oyster.DBandContentProviderEx.ui.activity.TodoMainActivity;

import java.io.Serializable;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoMainFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String TAG = "ToDoMainFragment";

    public static int CURRENT_PROJECT_ID = -1;

    private static final int MENU_DELETE_ID = Menu.FIRST + 1;

    transient private ToDoCursorAdapter mCursorAdapter;

    private static long mProjectId = -1;


    public final static String KEY_PROJECT_ID = "ToDoMAinFragment.projectId";


    public static ToDoMainFragment newInstance(long projectId) {

        ToDoMainFragment toDoMainFragment = new ToDoMainFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_PROJECT_ID, projectId);

        toDoMainFragment.setArguments(args);

        return toDoMainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(TAG, " :  onCreate ");


        setRetainInstance(true);
        setHasOptionsMenu(true);

        mProjectId = getArguments().getLong(KEY_PROJECT_ID);

//        registerForContextMenu(getListView());
//        registerForContextMenu(getListView());
//        getListView().setDividerHeight(3);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.todo_list_layout, container, false);

        init();
        fillData();


        Log.i(TAG, " :  onCreateView ");


        return v;
    }

    private void init() {

//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);


    }

    private void initListView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(getListView());
        } else {

            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    getActivity().getMenuInflater().inflate(R.menu.menu_context_main, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.menu_main_context_delete:

                            ToDoCursorAdapter adapter = (ToDoCursorAdapter) getListAdapter();
                            ListView listView = getListView();

                            for (int i = 0; i < adapter.getCount(); i++) {

                                if (listView.isItemChecked(i)) {
                                    ToDo.getById(getProjectId(), listView.getItemIdAtPosition(i))
                                            .delete();
                                }
                            }

                            mode.finish();
                            adapter.notifyDataSetChanged();
                            return true;

                        default:
                            return false;
                    }

                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_main, menu);
//        menu.add(0, MENU_DELETE_ID, 0, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_context_delete:

                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                ToDo.getById(getProjectId(), info.id)
                        .delete();
                return true;
            default:
                return
                        super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_main_new_toDo:

                createToDo();
                return true;

            case R.id.menu_main_sync:

                TodoMainActivity activity = ((TodoMainActivity) getActivity());
                if (activity != null) {
                    activity.synchronizeWithServer();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }


    public void showToDoInNewActivity(int position) {

        Intent intent = new Intent(getActivity(), ToDoDetailActivity.class);

        intent.putExtra(ToDoDetailActivity.KEY_PROJECT_ID, getProjectId());
        intent.putExtra(ToDoDetailActivity.KEY_TODO_POSITION_SELECTED, position);

        startActivity(intent);

/*
        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(toDo,
                (ToDoDetailFragment.OnSuicideListener) getActivity());

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
//                .setCustomAnimations(
//                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
//                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                .replace(((TodoMainActivity) getActivity()).getFragmentContainerId(),
                        fragment, TodoMainActivity.TAG_DETAIL_FRAGMENT)
                .addToBackStack(null)
                .commit();*/

    }

    private void createToDo() {
        ToDo toDo = new ToDo();
        toDo.setProjectId(getProjectId());

        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(toDo,
                (ToDoDetailFragment.OnSuicideListener) getActivity());

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
//                .setCustomAnimations(
//                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
//                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)

                .replace(((TodoMainActivity) getActivity()).getFragmentContainerId(),
                        fragment, TodoMainActivity.TAG_DETAIL_FRAGMENT)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        showToDoInNewActivity(position);
    }


    private void fillData() {

        Bundle args = getArguments();

        getLoaderManager().initLoader(0, args, this);


        mCursorAdapter = new ToDoCursorAdapter(getActivity());

        setListAdapter(mCursorAdapter);
    }


    //****************************                Loader Logic                ******************************************
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        long projectId = args.getLong(KEY_PROJECT_ID);

        String[] projection = new String[]{
                TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY, TodoTable.COLUMN_CATEGORY
        };

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                Uri.parse(TodoContentProvider.CONTENT_TODO_URI + "/" + projectId),
                projection,
                null,
                null,
                null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        initListView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    //******************************************************************************************************************


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, " :  onViewCreated ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, " :  onDestroyView ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, " :  onStart ");
    }

    @Override
    public void onResume() {
        super.onResume();

//        ((NavigationDrawerBaseActivity) getActivity()).closeAllDrawers();

        String name = Project.getProjectById(getProjectId()).getSummary();
        getActivity().getActionBar().setTitle("Project : " + name);

        Log.i(TAG, " :  onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, " :  onPause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, " :  onStop ");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, " :  onAttach ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " :  onDestroy ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, " :  onDetach ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, " :  onActivityCreated ");
    }

    public static long getProjectId() {
        return mProjectId;
    }

    /**
     * Class for effective use of ListAdapter (CursorAdapter here)
     * makes use of View.setTag(Object tag) method, does not calls findViewById()
     * each time the row is populated with data, reuses found ids
     */
    class RowViewHolder {
        public TextView mTextViewHolder;
        public ImageView mImageViewHolder;

        public RowViewHolder(TextView textView, ImageView imageView) {
            this.mTextViewHolder = textView;
            this.mImageViewHolder = imageView;
        }
    }


    /**
     * just nice CursorAdapter
     */
    public class ToDoCursorAdapter extends CursorAdapter implements Serializable {

        private LayoutInflater mLayoutInflater;

        public ToDoCursorAdapter(Context context) {
            super(context, null, 0);

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.todo_row_layout, parent, false);
            RowViewHolder holder = new RowViewHolder((TextView) v.findViewById(R.id.todo_row_textView),
                    (ImageView) v.findViewById(R.id.todo_row_imageView));
            v.setTag(holder);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            RowViewHolder holder = (RowViewHolder) view.getTag();

            String summary = cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY));
            holder.mTextViewHolder.setText(summary);

            Category category = Category.valueOf(
                    cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY)));

            int drawableResources = -1;
            switch (category) {
                case Urgent:
                    drawableResources = android.R.drawable.star_big_on;
                    break;
                case Remainder:
                    drawableResources = android.R.drawable.star_big_off;
                    break;
            }

            holder.mImageViewHolder.setImageDrawable(getResources().getDrawable(drawableResources));
        }
    }


}
