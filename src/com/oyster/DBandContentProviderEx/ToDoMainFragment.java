package com.oyster.DBandContentProviderEx;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoMainFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int MENU_DELETE_ID = Menu.FIRST + 1;
    private ToDoCursorAdapter mCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(getClass().getSimpleName(), " :  onCreate ");


        setRetainInstance(true);
        setHasOptionsMenu(true);

//        registerForContextMenu(getListView());
//        registerForContextMenu(getListView());
//        getListView().setDividerHeight(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.todo_list_layout, container, false);

        init();
        fillData();

        Log.i(getClass().getSimpleName(), " :  onCreateView ");


        return v;
    }

    private void init() {

//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setTitle(R.string.action_bar_title_main_fragment);


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
                                    Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI
                                            + "/" + ToDoApplication.getCurrentUserId()
                                            + "/" + listView.getItemIdAtPosition(i));
                                    getActivity().getContentResolver().delete(uri, null, null);
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

                Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI + "/" +
                        ToDoApplication.getCurrentUserId() + "/" + info.id);
                getActivity().getContentResolver().delete(uri, null, null);
                fillData();

                return true;
            default:
                return
                        super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_main_insert:

                createToDo(null);
                return true;

            case R.id.menu_main_sign_out:


                ParseUser.logOut();

                Intent i = new Intent(getActivity(), DispatchActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                getActivity().finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }


    public void createToDo(Uri uri) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(uri,
                (ToDoDetailFragment.OnSuicideListener) getActivity());
        transaction.replace(((TodoMainActivity) getActivity()).getFragmentContainerId(),
                fragment, TodoMainActivity.TAG_DETAIL_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // put Uri that refers to the id of the item, it's type is TodoContentProvider.CONTENT_ITEM_TYPE
        Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI +
                "/" + ToDoApplication.getCurrentUserId() +
                "/" + id);
        createToDo(uri);
    }


    private void fillData() {

//        String[] from = new String[]{TodoTable.COLUMN_SUMMARY};
//
//        int[] to = new int[]{R.id.row_textView};
//
        getLoaderManager().initLoader(0, null, this);

//        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.todo_row_layout, null, from, to, 0);

        mCursorAdapter = new ToDoCursorAdapter(getActivity());

        setListAdapter(mCursorAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY, TodoTable.COLUMN_CATEGORY
        };

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                Uri.parse(TodoContentProvider.CONTENT_URI + "/" + ToDoApplication.getCurrentUserId()),
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(getClass().getSimpleName(), " :  onViewCreated ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(getClass().getSimpleName(), " :  onDestroyView ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(getClass().getSimpleName(), " :  onStart ");
    }

    @Override
    public void onResume() {
        super.onResume();

        ((NavigationDrawerBaseActivity) getActivity()).closeAllDrawers();

        Log.i(getClass().getSimpleName(), " :  onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), " :  onPause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), " :  onStop ");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(getClass().getSimpleName(), " :  onAttach ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getSimpleName(), " :  onDestroy ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(getClass().getSimpleName(), " :  onDetach ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(getClass().getSimpleName(), " :  onActivityCreated ");
    }


    class ToDoCursorAdapter extends CursorAdapter {

        private LayoutInflater mLayoutInflater;

        public ToDoCursorAdapter(Context context) {
            super(context, null, 0);

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = mLayoutInflater.inflate(R.layout.todo_row_layout, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView textViewSummary = (TextView) view.findViewById(R.id.todo_row_textView);
            assert textViewSummary != null;

            String summary = cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY));
            textViewSummary.setText(summary);

            ImageView imageViewCategory = (ImageView) view.findViewById(R.id.todo_row_imageView);
            assert imageViewCategory != null;

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
            imageViewCategory.setImageDrawable(getResources().getDrawable(drawableResources));
        }
    }
}
