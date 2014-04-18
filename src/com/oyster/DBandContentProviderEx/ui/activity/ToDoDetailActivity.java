package com.oyster.DBandContentProviderEx.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.google.common.primitives.Longs;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoMainFragment;

import java.io.Serializable;
import java.util.List;

/**
 * @author bamboo
 * @since 4/12/14 8:13 AM
 */

public class ToDoDetailActivity extends FragmentActivity {

    private ViewPager mViewPager;

    private ToDoDetailPagerAdapter mPagerAdapter;

    public static final String KEY_TODO_ID_LIST = "todo_id_list";
    public static final String KEY_TODO_POSITION_SELECTED = "todo_id_selected";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail_view_pager);

        List<Long> idList = Longs.asList(getIntent().getLongArrayExtra(KEY_TODO_ID_LIST));
        int positionSelected = getIntent().getIntExtra(KEY_TODO_POSITION_SELECTED, 0);

        mViewPager = (ViewPager) findViewById(R.id.toDoDetailViewPager);
        mPagerAdapter = new ToDoDetailPagerAdapter(getSupportFragmentManager(), idList);

        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setCurrentItem(positionSelected);
    }

 /*
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
    */

    public class ToDoDetailPagerAdapter extends FragmentPagerAdapter
            implements Serializable {

        List<Long> toDoIdList;

        public ToDoDetailPagerAdapter(FragmentManager fm, List<Long> list) {
            super(fm);
            toDoIdList = list;
        }

        @Override
        public Fragment getItem(int i) {

            return ToDoDetailFragment.newInstance(
                    ToDo.getById(
                            ToDoMainFragment.getProjectId(),
                            toDoIdList.get(i)),
                    null
            );
        }

        @Override
        public int getCount() {
            return toDoIdList.size();
        }
    }

}