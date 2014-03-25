package com.oyster.DBandContentProviderEx;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoMainFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private SimpleCursorAdapter mCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(getClass().getSimpleName(), " :  onCreate ");


        setRetainInstance(true);
        setHasOptionsMenu(true);

//        registerForContextMenu(getListView());
//        getListView().setDividerHeight(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.todo_list_layout, container, false);

        fillData();

        Log.i(getClass().getSimpleName(), " :  onCreateView ");


        return v;
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

                createToDo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    public void createToDo() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(null,
                (ToDoDetailFragment.OnSuicideListener) getActivity());
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // put Uri that refers to the id of the item, it's type is TodoContentProvider.CONTENT_ITEM_TYPE
        Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI + "/" + id);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(uri,
                (ToDoDetailFragment.OnSuicideListener) getActivity());

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void fillData() {

        String[] from = new String[]{TodoTable.COLUMN_SUMMARY};

        int[] to = new int[]{R.id.row_textView};

        getLoaderManager().initLoader(0, null, this);

        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.todo_row_layout, null, from, to, 0);

        setListAdapter(mCursorAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        String[] projection = new String[]{
                TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY
        };

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                TodoContentProvider.CONTENT_URI, projection,
                null,
                null,
                null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
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
}
