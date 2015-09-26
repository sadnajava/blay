package com.squeaker.squeaker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {
    private Session session;
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

        session = (Session) getIntent().getExtras().get(SqueakerAndroidConstants.SESSION_FIELD);

        try {
            isMyProfile = getIntent().getExtras().getBoolean(SqueakerAndroidConstants.MY_PROFILE_FIELD);
        } catch (Exception ignored) {
            isMyProfile = false;
        }

        if (isMyProfile) {
            followButton.setVisibility(View.GONE);
            userMetadata = new UserMetadata(session.getEmail(), 0);
            userProfileSqueaks.setOnItemLongClickListener(new DeleteSqueakItemLongClickListener());
        } else {
            userMetadata = (UserMetadata) getIntent().getExtras().get(SqueakerAndroidConstants.USER_FIELD);
        }

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        FetchUserProfileTask task = new FetchUserProfileTask(userMetadata.getEmail());
        task.execute((Void) null);
    }

    private void updateUserProfileUI() {
        userProfileName.setText(userProfile.getEmail());
        userProfileSqueaks.setAdapter(new SqueakArrayAdapter(UserProfileActivity.this, R.layout.squeak_badge_layout, session, userProfile.getSqueaks()));

        userFollowingList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserProfileActivity.this, session, userProfile.getFollowingUsers()));
        userFollowingList.setAdapter(new UserMetadataArrayAdapter(UserProfileActivity.this, R.layout.user_badge_layout, userProfile.getFollowingUsers()));

        followButton.setText(userProfile.isFollowing() ? R.string.unfollow : R.string.follow);
    }

    /**
     * Represents an asynchronous user profile fetch task.
     */
    private class FetchUserProfileTask extends AsyncTask<Void, Void, UserProfile> {
        private final String email;

        FetchUserProfileTask(String email) {
            this.email = email;
        }

        @Override
        protected UserProfile doInBackground(Void... params) {
            try {
                return JsonApi.getUserProfile(session, email);
            } catch (Exception e) {
                return null;
            }
        }

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
                    JsonApi.unfollowUser(session, userProfile.getEmail());
                else
                    JsonApi.followUser(session, userProfile.getEmail());

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
                JsonApi.deleteSqueak(session, squeakId);
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
