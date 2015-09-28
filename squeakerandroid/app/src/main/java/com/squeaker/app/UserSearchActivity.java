package com.squeaker.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.UserMetadata;

import java.util.ArrayList;

public class UserSearchActivity extends Activity {
    private EditText userSearchText;
    private ListView searchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        userSearchText = (EditText) findViewById(R.id.userSearchText);
        searchResultList = (ListView) findViewById(R.id.searchResultList);
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

        UserSearchTask(String searchValue) {
            this.searchValue = searchValue;
        }

        @Override
        protected ArrayList<UserMetadata> doInBackground(Void... params) {
            try {
                return api.findUser(searchValue);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<UserMetadata> users) {
            super.onPostExecute(users);

            searchResultList.setOnItemClickListener(new OpenUserProfileOnClickListener(UserSearchActivity.this, session, api, users));
            searchResultList.setAdapter(new UserMetadataArrayAdapter(UserSearchActivity.this, R.layout.user_badge_layout, users));
        }
    }
}
