package com.oyster.DBandContentProviderEx;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.parse.*;

/**
 * @author bamboo
 * @since 3/24/14 9:53 PM
 */
public class ToDoMainFragment extends ListFragment {


    private static final int MENU_DELETE_ID = Menu.FIRST + 1;

    private ToDoParseAdapter mToDoParseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(getClass().getSimpleName(), " :  onCreate ");


        setRetainInstance(true);
        setHasOptionsMenu(true);

        mToDoParseAdapter = new ToDoParseAdapter(getActivity());

//        mToDoParseAdapter.loadObjects();
//        mToDoParseAdapter.setAutoload(false);
//        mToDoParseAdapter.setPaginationEnabled(false);

        setListAdapter(mToDoParseAdapter);

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

                            ListView listView = getListView();

                            for (int i = 0; i < mToDoParseAdapter.getCount(); i++) {

                                if (listView.isItemChecked(i)) {

                                    mToDoParseAdapter.getItem(i).deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e(ToDoMainFragment.this.getClass().getSimpleName(),
                                                        e.getMessage());
                                            }
                                        }
                                    });

                                    ;
                                }
                            }

                            mode.finish();
                            mToDoParseAdapter.notifyDataSetChanged();
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
/*
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

                Uri uri = Uri.parse(TodoContentProvider.CONTENT_URI + "/" + info.id);
                getActivity().getContentResolver().delete(uri, null, null);
                fillData();

                return true;
            default:
                return
                        super.onContextItemSelected(item);
        }
    }*/

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
            case R.id.menu_main_logOut:

                ParseUser.logOut();
                Intent i = new Intent(getActivity(), LogInOrSignUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }


    public void createToDo(ToDo toDo) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(toDo);

        transaction.replace(((TodoMainActivity) getActivity()).getFragmentContainerId(),
                fragment, TodoMainActivity.TAG_DETAIL_FRAGMENT);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ToDo toDo = mToDoParseAdapter.getItem(position);

        createToDo(toDo);
    }


    private void fillData() {

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

        initListView();
        Log.i(getClass().getSimpleName(), " :  onActivityCreated ");
    }

    private ParseQueryAdapter.QueryFactory<ToDo> mToDoQueryFactory = new ParseQueryAdapter.QueryFactory<ToDo>() {
        @Override
        public ParseQuery<ToDo> create() {

            ParseQuery<ToDo> parseQuery = ParseQuery.getQuery(ToDo.class);
            parseQuery.whereEqualTo(ToDo.KEY_USER, ParseUser.getCurrentUser());
            parseQuery.include("user");
            parseQuery.orderByDescending("createdAt");

            parseQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);

            return parseQuery;
        }
    };

    public BaseAdapter getCustomAdapter() {
        return mToDoParseAdapter;
    }


    // for effective use of adapter
    class ViewHolder {
        TextView holderTextViewSummary;
        ImageView holderImageViewIcon;

        ViewHolder(TextView textView, ImageView imageView) {
            holderImageViewIcon = imageView;
            holderTextViewSummary = textView;

            DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_SHOW_TIME);
        }
    }

    class ToDoParseAdapter extends ParseQueryAdapter<ToDo> {

        public ToDoParseAdapter(Context context) {
            super(context, mToDoQueryFactory);

        }

        @Override
        public View getItemView(ToDo toDo, View v, ViewGroup parent) {


            if (v == null) {
                v = LayoutInflater.from(getActivity()).inflate(R.layout.todo_row_layout, parent, false);
                ViewHolder holder = new ViewHolder((TextView) v.findViewById(R.id.todo_row_textView),
                        (ImageView) v.findViewById(R.id.todo_row_imageView));
                v.setTag(holder);
            }

            Category category = toDo.getCategory();
            int drawableRes = 0;
            switch (category) {
                case Remainder:
                    drawableRes = android.R.drawable.star_big_off;
                    break;
                case Urgent:
                    drawableRes = android.R.drawable.star_big_on;
                    break;
            }


            ViewHolder holder = (ViewHolder) v.getTag();

            holder.holderImageViewIcon.setImageDrawable(getResources().getDrawable(drawableRes));

            holder.holderTextViewSummary.setText(toDo.getSummary());

            return v;
        }

    }


    public interface OnCallbacksListener {
        public void OnCallbackCalled();
    }

}
