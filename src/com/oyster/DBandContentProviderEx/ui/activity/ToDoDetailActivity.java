package com.oyster.DBandContentProviderEx.ui.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.oyster.DBandContentProviderEx.R;
import com.oyster.DBandContentProviderEx.data.ToDo;
import com.oyster.DBandContentProviderEx.ui.fragment.ToDoDetailFragment;

import java.io.Serializable;
import java.util.List;

/**
 * @author bamboo
 * @since 4/12/14 8:13 AM
 */

public class ToDoDetailActivity extends FragmentActivity {

    private ViewPager mViewPager;

    private ToDoDetailPagerAdapter mPagerAdapter;

    List<ToDo> mToDos = null;

    public static final String KEY_PROJECT_ID = "project_id";
    public static final String KEY_TODO_POSITION_SELECTED = "todo_id_selected";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail_view_pager);


        // get arguments passed from caller
        long projectId = getIntent().getLongExtra(KEY_PROJECT_ID, 0);
        int positionSelected = getIntent().getIntExtra(KEY_TODO_POSITION_SELECTED, 0);

        init(projectId, positionSelected);

    }

    private void init(long projectId, int positionSelected) {

        mToDos = ToDo.getByProjectId(projectId);

        mViewPager = (ViewPager) findViewById(R.id.toDoDetailViewPager);
        mPagerAdapter = new ToDoDetailPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

                Log.i("ololo_", String.valueOf(i));
                getActionBar().setTitle("ToDo : " + mToDos.get(i).getSummary());

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mViewPager.setCurrentItem(positionSelected);

        if (positionSelected == 0) {
            getActionBar().setTitle("ToDo : " + mToDos.get(0).getSummary());
        }
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

        public ToDoDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return ToDoDetailFragment.newInstance(
                    mToDos.get(i),
                    null
            );
        }

        @Override
        public int getCount() {
            return mToDos.size();
        }


    }

}