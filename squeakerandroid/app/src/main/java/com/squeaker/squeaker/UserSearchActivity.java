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

    private Session session;

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

        session = (Session) getIntent().getExtras().get(SqueakerAndroidConstants.SESSION_FIELD);
    }

    public void findUser(View view) {
        UserSearchTask task = new UserSearchTask(userSearchText.getText().toString());
        task.execute();
    }

    /**
     * Represents an asynchronous user search task.
     */
    public class UserSearchTask extends AsyncTask<Void, Void, ArrayList<UserMetadata>> {
        private String searchValue;
        private ArrayList<String> fetchedUsers;

        UserSearchTask(String searchValue) {
            this.searchValue = searchValue;
        }

        @Override
        protected ArrayList<UserMetadata> doInBackground(Void... params) {
            try {
                return JsonApi.findUser(session, searchValue);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserMetadata> users) {
            super.onPostExecute(users);

            searchResultList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserSearchActivity.this, session, users));
            searchResultList.setAdapter(new UserMetadataArrayAdapter(UserSearchActivity.this, R.layout.user_badge_layout, users));
        }
    }
}
