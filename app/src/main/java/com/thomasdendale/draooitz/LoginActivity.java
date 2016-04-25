package com.thomasdendale.draooitz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.tavendo.autobahn.WebSocketConnection;

/**
 * A login screen that offers login via email/password.
 * Parts from default login activity
 */
public class LoginActivity extends AppCompatActivity {

    private static String TAG = "trala";

    private WebSocketConnection wsConnection = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton mEmailSignInButton = (FloatingActionButton) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        // use shared preferences for now for user name & password.
        // this can be replaced with a short-lifetime hash key?
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        if (sp.contains("username")) {
            Log.i(TAG, "Found username");
            String pass = sp.getString("password", "none");
            String user = sp.getString("username", "none");

            mPasswordView.setText(pass);
            mEmailView.setText(user);
        } else {
            Log.i(TAG, "Could not find username");
        }
    }


    private void goto_information_activity() {
       Intent intent = new Intent(this, RoomsListActivity.class);
       startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.i(TAG, "attemptLogin: start");

        wsConnection = ((DraooitzApplication) getApplication()).wsConnection;

        // if connection was lost at some point
        ((DraooitzApplication) getApplication()).connect();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user address.
        if (TextUtils.isEmpty(user)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isUserValid(user)) {
            mEmailView.setError("This Username is invalid.\nDo not use spaces.");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            login_event_handler handler = new login_event_handler(user, password);  // Pass relevant data as constructor, so that the callback can save the credentials to the storage
                                                                                    // I don't really like this though...
            ((DraooitzApplication) getApplication()).set_event_handler(handler);
            // send password as hash
            MessageDigest md = null; // No propper way of hashing strings in a reliable way?
            String password_hashed = "";
            try {
                md = MessageDigest.getInstance("SHA");
                md.reset();
                md.update(password.getBytes());
                password_hashed = new String(md.digest());
            } catch (NoSuchAlgorithmException e) {
                password_hashed = password;     // SHA encryption not supported?
            }

            ((DraooitzApplication) getApplication()).send_message("LOGIN:"+user+","+password_hashed);


            Log.i(TAG, "attemptLogin: end");
        }
    }

    private boolean isUserValid(String user) {
        return !user.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.


        // Uhg, really?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("username", username);
        editor.putString("password", password);

        editor.apply();
    }


    public class login_event_handler implements EventHandler{
        String user;
        String pass;

        login_event_handler(String user, String pass) {
            this.user = user;
            this.pass = pass;

        }

        @Override
        public void onOpen() { }

        @Override
        public void onAlreadyOpen() { }

        @Override
        public void onTextMessage(String payload) {

            Log.i(TAG, "ONTEXTMESSAGE, login handler");

            Log.i(TAG, "Got response: " + payload);


            try {
                JSONObject js;
                String state;

                js = new JSONObject(payload);
                state = js.getString("state");

                if (state.equals("LOGGED_IN") || state.equals("REGISTERED_LOGGED_IN")) {
                    saveCredentials(user, pass);
                    Log.i("TAG", "Logged in");
                    goto_information_activity();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

                showProgress(false);

            } catch (JSONException e) {
                Log.i("TAG", e.toString());
            }



        }

        @Override
        public void onClose(int code, String reason) {
            Log.i("TAG", "Connection lost: "+reason);
            Toast toast = Toast.makeText(getApplicationContext(), "Connection lost ;_;", Toast.LENGTH_LONG);

            toast.show();

            showProgress(false);
        }
    }
}

