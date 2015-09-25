package com.squeaker.squeaker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {
    private SessionId sid;

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

        String intentSid = getIntent().getExtras().getString(SqueakerAndroidConstants.SESSION_ID_FIELD);
        sid = new SessionId(intentSid);
        UserMetadata userMetadata = (UserMetadata) getIntent().getExtras().get(SqueakerAndroidConstants.USER_FIELD);

        FetchUserProfileTask task = new FetchUserProfileTask(userMetadata);
        task.execute((Void) null);
    }

    private void updateUserProfileUI(UserProfile userProfile) {
        userProfileName.setText(userProfile.getEmail());
        userProfileSqueaks.setAdapter(new SqueakListAdapter(UserProfileActivity.this, R.layout.squeak_badge_layout, sid, userProfile.getSqueaks()));

        userFollowingList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserProfileActivity.this, sid, userProfile.getFollowingUsers()));
        userFollowingList.setAdapter(new UserMetadataArrayAdapter(UserProfileActivity.this, R.layout.user_badge_layout, userProfile.getFollowingUsers()));

        followButton.setText(userProfile.isFollowing() ? R.string.unfollow : R.string.follow);
    }

    /**
     * Represents an asynchronous user profile fetch task.
     */
    public class FetchUserProfileTask extends AsyncTask<Void, Void, UserProfile> {
        private final UserMetadata userMetadata;

        FetchUserProfileTask(UserMetadata user) {
            this.userMetadata = user;
        }

        @Override
        protected UserProfile doInBackground(Void... params) {
            try {
                return JsonApi.getUserProfile(sid, userMetadata);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final UserProfile userProfile) {
            super.onPostExecute(userProfile);

            if (userProfile == null) {
                userProfileContainer.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            } else {
                userProfileContainer.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);

                updateUserProfileUI(userProfile);

                followButton.setOnClickListener(new FollowButtonOnClickListener(userProfile));
            }
        }
    }

    private class FollowButtonOnClickListener implements OnClickListener {
        private final UserProfile userProfile;

        public FollowButtonOnClickListener(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        @Override
        public void onClick(View v) {
            try {
                if (userProfile.isFollowing())
                    JsonApi.unfollowUser(sid, userProfile.getEmail());
                else
                    JsonApi.followUser(sid, userProfile.getEmail());
            } catch (Exception ignored) {

            }

            userProfile.setIsFollowing(!userProfile.isFollowing());

            updateUserProfileUI(userProfile);
        }
    }
}
