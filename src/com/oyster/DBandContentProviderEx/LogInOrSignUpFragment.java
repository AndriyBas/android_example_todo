package com.oyster.DBandContentProviderEx;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * @author bamboo
 * @since 3/30/14 1:36 PM
 */
public class LogInOrSignUpFragment extends Fragment {

    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    private Button mButtonSignUp;
    private Button mButtonLogIn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_or_sign_up_fragment, container, false);

        init(v);

        bindViews();

        return v;
    }

    /**
     * set listeners on SignIn and LogIn buttons
     */
    private void bindViews() {

        mButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String username = mEditTextUsername.getText().toString();
                final String password = mEditTextPassword.getText().toString();

                boolean validationError = false;
                StringBuilder validationErrorMsg = new StringBuilder("Please ");

                // check if username is not empty (1)
                if (isEmpty(username)) {
                    validationError = true;
                    validationErrorMsg.append(" enter a username");
                }

                // check if password is not empty (2)
                if (isEmpty(password)) {
                    if (validationError)
                        validationErrorMsg.append(", and");
                    validationError = true;
                    validationErrorMsg.append("enter a password");
                }

                validationErrorMsg.append(" !");

                // show message if (1) or (2) and return, login failed
                if (validationError) {
                    onLogInError(validationErrorMsg.toString());
                    return;
                }

                // Dialog to show while logging in in the background
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle("Logging in ...");
                dlg.setMessage("Logging in. Please, wait");
                dlg.show();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (dlg != null && dlg.isShowing()) {
                            dlg.dismiss();
                        }

                        if (e == null) {
                            startMainActivity();
                        } else {
                            onLogInError(e.getMessage());
                        }
                    }
                });

            }
        });

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    // Show Toast message on login failure
    private void onLogInError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG)
                .show();
    }

    /**
     * Start MainActivity on successful login
     */
    private void startMainActivity() {
        Intent i = new Intent(getActivity(), TodoMainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Initialize Views from the layout inflated into v
     *
     * @param v View where the layout is inflated
     */
    private void init(View v) {
        mEditTextUsername = (EditText) v.findViewById(R.id.login_username);
        mEditTextPassword = (EditText) v.findViewById(R.id.login_password);

        mButtonLogIn = (Button) v.findViewById(R.id.login_btnLogIn);
        mButtonSignUp = (Button) v.findViewById(R.id.login_btnSignUp);
    }

    /**
     * Check if current String consists at least of 1 alphanumeric character
     *
     * @param s String to check
     * @return true if String contains at lest 1 alphanumeric character
     */
    private boolean isEmpty(String s) {
        return s.trim().length() == 0;
    }
}