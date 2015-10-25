package com.squeaker.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.ServerApi;
import com.squeaker.squeaker.Session;
import com.squeaker.squeaker.JsonServerApi;
import com.squeaker.squeaker.UserMetadata;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    // UI references.
    private EditText serverIpText;
    private EditText emailText;
    private EditText passwordText;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serverIpText = (EditText) findViewById(R.id.serverIp);
        emailText = (EditText) findViewById(R.id.email);

        passwordText = (EditText) findViewById(R.id.password);
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        serverIpText.setError(null);
        emailText.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        String serverIp = serverIpText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordText.setError(getString(R.string.error_empty_password));
            focusView = passwordText;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailText.setError(getString(R.string.error_empty_email));
            focusView = emailText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailText.setError(getString(R.string.error_invalid_email));
            focusView = emailText;
            cancel = true;
        }

        if (TextUtils.isEmpty(serverIp)) {
            serverIpText.setError(getString(R.string.error_server_ip_empty));
            focusView = serverIpText;
            cancel = true;
        } else if (!isIpValid(serverIp)) {
            serverIpText.setError(getString(R.string.error_invalid_server_ip));
            focusView = serverIpText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            ServerApi api = new JsonServerApi(serverIp);
            UserLoginTask task = new UserLoginTask(api, email, password);
            task.execute();
        }
    }

    private boolean isIpValid(String ip) {
        return Patterns.IP_ADDRESS.matcher(ip).matches();
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        private final String email;
        private final String password;
        private final ServerApi api;

        public UserLoginTask(ServerApi api, String email, String password) {
            this.api = api;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return api.login(email, password);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String sessionId) {
            super.onPostExecute(sessionId);

            showProgress(false);

            if (sessionId != null) {
                Session session = new Session(sessionId, new UserMetadata(email, "", 0));
                Intent intent = new Intent(LoginActivity.this, NewsFeedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SqueakerActivityProtocol.API_FIELD, api);
                intent.putExtra(SqueakerActivityProtocol.SESSION_FIELD, session);
                startActivity(intent);
                finish();
            } else {
                passwordText.setError(getString(R.string.error_incorrect_password));
                passwordText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

