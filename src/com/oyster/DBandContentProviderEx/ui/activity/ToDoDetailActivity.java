package com.oyster.DBandContentProviderEx.ui.activity;

import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoMainFragment;

/**
 * @author bamboo
 * @since 4/12/14 8:13 AM
 */
public class ToDoDetailActivity extends FragmentActivity {

    private ToDoMainFragment.ToDoCursorAdapter mCursorAdapter;

    private ViewPager mViewPager;

    private ToDoDetailPagerAdapter mPagerAdapter;


    public static final String KEY_CURSOR_ADAPTER = "cursor_adapter";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail_view_pager);

        ToDoMainFragment.ToDoCursorAdapter adapter = (ToDoMainFragment.ToDoCursorAdapter) getIntent().getSerializableExtra(KEY_CURSOR_ADAPTER);
        if (savedInstanceState != null) {
            adapter = (ToDoMainFragment.ToDoCursorAdapter) savedInstanceState.getSerializable(KEY_CURSOR_ADAPTER);
        }
        mCursorAdapter = adapter;


        mViewPager = (ViewPager) findViewById(R.id.toDoDetailViewPager);
        mPagerAdapter = new ToDoDetailPagerAdapter(getFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(KEY_CURSOR_ADAPTER, mCursorAdapter);

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mCursorAdapter = (ToDoMainFragment.ToDoCursorAdapter) savedInstanceState
                    .getSerializable(KEY_CURSOR_ADAPTER);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        int currentItem = mViewPager.getCurrentItem();
        if (currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(currentItem - 1);
        }
    }

    public class ToDoDetailPagerAdapter extends FragmentPagerAdapter {


        public ToDoDetailPagerAdapter(android.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.app.Fragment getItem(int i) {

            return ToDoDetailFragment.newInstance(
                    ToDo.getById(
                            ToDoMainFragment.getProjectId(),
                            mCursorAdapter.getItemId(i)),
                    null
            );
        }

        @Override
        public int getCount() {
            return mCursorAdapter.getCount();
        }
    }

}