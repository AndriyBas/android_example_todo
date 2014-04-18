package com.oyster.DBandContentProviderEx.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;

/**
 * @author bamboo
 * @since 4/1/14 3:03 PM
 */
public abstract class DebugLoggingSupportFragment extends Fragment {

    public boolean mDebugMode = true;

    private void log(String s) {
        if (mDebugMode) {
            Log.i("Debug ololo : ", s);

        }
    }

    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("onCreateView()");
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        log("onHiddenChanged( hidden : " + hidden + ")");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("onActivityResult(requestCode : " + requestCode + ", resultCode : "
                + resultCode + ", intent : " + data.getAction() + ")");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        log("onAttach ( Activity : " + activity.getClass().getSimpleName() + ")");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        log("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        log("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        log("onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        log("onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        log("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        log("onConfigurationChanged (" + newConfig.getClass().getSimpleName() + ")");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        log("onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        log("onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        log("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        log("onDetach");
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        log("onCreateOptionsMenu ( Menu : " + menu.getClass().getSimpleName()
                + " , MenuInflater : " + inflater.getClass().getSimpleName() + ")");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        log("onPrepareOptionsMenu ( Menu : " + menu.getClass().getSimpleName() + ")");
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDestroyOptionsMenu() {
        log("onDestroyOptionsMenu");
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        log("onOptionsMenuClosed ( Menu : " + menu.getClass().getSimpleName() + ")");
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        log("onCreateContextMenu  ( ContextMenu : " + menu.getClass().getSimpleName()
                + ", View : " + v.getClass().getSimpleName() + ", ContextMenyInfo : " + menuInfo.toString() + ")");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log("onOptionsItemSelected ( MenuItem : " + item.getClass().getSimpleName());
        return super.onOptionsItemSelected(item);
    }
}