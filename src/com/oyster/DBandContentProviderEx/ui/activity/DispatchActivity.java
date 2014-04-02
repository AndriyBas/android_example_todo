package com.oyster.DBandContentProviderEx.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/30/14 12:57 PM
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(DispatchActivity.this, TodoMainActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(DispatchActivity.this, LogInOrSignUpActivity.class);
            startActivity(i);
        }

    }
}