package com.squeaker.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.UserProfile;

public class AccountSettingsActivity extends Activity {
    private TextView accountSettingsHeader;
    private EditText displayName;
    private Button updateSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        accountSettingsHeader = (TextView) findViewById(R.id.accountSettingsHeader);
        displayName = (EditText) findViewById(R.id.displayName);
        updateSettings = (Button) findViewById(R.id.updateSettings);

        updateSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccountSettingsTask task = new UpdateAccountSettingsTask(displayName.getText().toString());
                task.execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        accountSettingsHeader.setText(getString(R.string.account_settings_header, session.getUser().getEmail()));

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        FetchUserProfileTask task = new FetchUserProfileTask(api) {
            @Override
            protected void onPostExecute(UserProfile userProfile) {
                super.onPostExecute(userProfile);

                if (userProfile != null) {
                    session.getUser().setDisplayName(userProfile.getMetadata().getDisplayName());
                    displayName.setText(userProfile.getMetadata().getDisplayName());
                }
            }
        };

        task.execute(session.getUser().getEmail());
    }

    private class UpdateAccountSettingsTask extends AsyncTask<Void, Void, Boolean> {
        private final String displayName;

        UpdateAccountSettingsTask(String displayName) {
            this.displayName = displayName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                api.updateDisplayName(displayName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success)
                finish();
        }
    }
}
