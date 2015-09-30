package com.squeaker.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.SqueakMetadata;
import com.squeaker.squeaker.UserMetadata;
import com.squeaker.squeaker.UserProfile;

public class UserProfileActivity extends Activity {
    private UserProfile userProfile;
    private UserMetadata userMetadata;
    private boolean isMyProfile;

    private TextView userProfileName;
    private Button followButton;
    private LinearLayout userProfileContainer;
    private ListView userProfileSqueaks;
    private ListView userFollowingList;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfileName = (TextView) findViewById(R.id.userProfileName);
        followButton = (Button) findViewById(R.id.followButton);
        userProfileContainer = (LinearLayout) findViewById(R.id.userProfileContainer);
        userProfileSqueaks = (ListView) findViewById(R.id.userProfileSqueaks);
        userFollowingList = (ListView) findViewById(R.id.userFollowingList);
        noData = (TextView) findViewById(R.id.noData);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            isMyProfile = getIntent().getExtras().getBoolean(SqueakerActivityProtocol.MY_PROFILE_FIELD);
        } catch (Exception ignored) {
            isMyProfile = false;
        }

        if (isMyProfile) {
            followButton.setVisibility(View.GONE);
            userMetadata = session.getUser();
            userProfileSqueaks.setOnItemLongClickListener(new DeleteSqueakItemLongClickListener());
        } else {
            userMetadata = (UserMetadata) getIntent().getExtras().get(SqueakerActivityProtocol.USER_FIELD);
        }

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        FetchUserProfileTask task = new FetchUserProfileTask(api) {
            @Override
            protected void onPostExecute(final UserProfile userProfile) {
                super.onPostExecute(userProfile);

                UserProfileActivity.this.userProfile = userProfile;

                if (userProfile == null) {
                    userProfileContainer.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                } else {
                    userProfileContainer.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);

                    updateUserProfileUI();

                    followButton.setOnClickListener(new FollowButtonOnClickListener());
                }
            }
        };

        task.execute(userMetadata.getEmail());
    }

    private void updateUserProfileUI() {
        userProfileName.setText(userProfile.getMetadata().getDisplayString());
        userProfileSqueaks.setAdapter(new SqueakArrayAdapter(UserProfileActivity.this, R.layout.squeak_badge_layout, api, userProfile.getSqueaks()));

        userFollowingList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserProfileActivity.this, session, api, userProfile.getFollowingUsers()));
        userFollowingList.setAdapter(new UserMetadataArrayAdapter(UserProfileActivity.this, R.layout.user_badge_layout, userProfile.getFollowingUsers()));

        followButton.setText(userProfile.isFollowing() ? R.string.unfollow : R.string.follow);
    }

    private class FollowButtonOnClickListener implements OnClickListener {
        public FollowButtonOnClickListener() {
        }

        @Override
        public void onClick(View v) {
            FollowUserTask task = new FollowUserTask();
            task.execute();
        }
    }

    private class DeleteSqueakItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final SqueakMetadata squeak = userProfile.getSqueaks().get(position);

            new AlertDialog.Builder(UserProfileActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.delete_squeak)
                    .setMessage(getString(R.string.delete_squeak_message, squeak.getCaption()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeleteSqueakTask task = new DeleteSqueakTask(squeak.getSqueakId());
                            task.execute();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();

            return true;
        }
    }

    /**
     * Represents an asynchronous user follow/unfollow fetch task.
     */
    private class FollowUserTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (userProfile.isFollowing())
                    api.unfollowUser(userProfile.getMetadata().getEmail());
                else
                    api.followUser(userProfile.getMetadata().getEmail());

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success) {
                userProfile.setIsFollowing(!userProfile.isFollowing());
                updateUserProfileUI();
            }
        }
    }

    private class DeleteSqueakTask extends AsyncTask<Void, Void, Boolean> {
        private final String squeakId;

        public DeleteSqueakTask(String squeakId) {
            this.squeakId = squeakId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                api.deleteSqueak(squeakId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success)
                fetchUserProfile();
        }
    }
}
