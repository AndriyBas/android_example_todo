package com.oyster.DBandContentProviderEx;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

/**
 * @author bamboo
 * @since 3/28/14 11:52 PM
 */
public class NavigationDrawerBaseActivity extends Activity {

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_drawer_base_layout);

        init();

    }

    private void init() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                                             /* host Activity */
                mDrawerLayout,                                    /* DrawerLayout object */
                R.drawable.ic_drawer,                             /* nav drawer icon to replace 'Up' caret */
                R.string.navigation_drawer_open_drawer_cont_desc, /* "open drawer" description */
                R.string.navigation_drawer_close_drawer_cont_desc /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(R.string.navigation_drawer_action_bar_open_drawer);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(R.string.navigation_drawer_action_bar_close_drawer);
                invalidateOptionsMenu();
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    public void closeAllDrawers() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
        // Change your action menu here, like the following
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
    }
*/


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            // handle the click on action bar items specific for the navigation drawer when it is opened
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }*/


}