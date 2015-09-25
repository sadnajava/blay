package com.squeaker.squeaker;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class UserSearchActivity extends ActionBarActivity {
    private EditText userSearchText;
    private ListView searchResultList;

    private SessionId sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        userSearchText = (EditText) findViewById(R.id.userSearchText);
        searchResultList = (ListView) findViewById(R.id.searchResultList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String intentSid = getIntent().getExtras().getString(SqueakerAndroidConstants.SESSION_ID_FIELD);
        sid = new SessionId(intentSid);
    }

    public void findUser(View view) {
        UserSearchTask task = new UserSearchTask(sid, userSearchText.getText().toString());
        task.execute();
    }

    /**
     * Represents an asynchronous user search task.
     */
    public class UserSearchTask extends AsyncTask<Void, Void, ArrayList<UserMetadata>> {
        private SessionId sid;
        private String searchValue;
        private ArrayList<String> fetchedUsers;

        UserSearchTask(SessionId sid, String searchValue) {
            this.sid = sid;
            this.searchValue = searchValue;
        }

        @Override
        protected ArrayList<UserMetadata> doInBackground(Void... params) {
            try {
                return JsonApi.findUser(sid, searchValue);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserMetadata> users) {
            super.onPostExecute(users);

            searchResultList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserSearchActivity.this, sid, users));
            searchResultList.setAdapter(new UserMetadataArrayAdapter(UserSearchActivity.this, R.layout.user_badge_layout, users));
        }
    }
}
