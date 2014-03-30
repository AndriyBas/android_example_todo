package com.oyster.DBandContentProviderEx;

import android.app.Fragment;

/**
 * @author bamboo
 * @since 3/30/14 1:02 PM
 */
public class SignUpActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SignUpFragment();
    }
}