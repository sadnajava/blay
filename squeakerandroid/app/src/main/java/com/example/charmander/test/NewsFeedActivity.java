package com.example.charmander.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class NewsFeedActivity extends ActionBarActivity {

    // UI references.
    private TextView mNoDataTextView;
    private ListView mSqueakListView;

    private SessionId sid;
    private ArrayList<SqueakMetadata> newsFeedSqueaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        mNoDataTextView = (TextView) findViewById(R.id.noData);
        mSqueakListView = (ListView) findViewById(R.id.newsFeedSqueaks);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String intentSid = getIntent().getExtras().getString(SqueakerAndroidConstants.SESSION_ID_FIELD);
        sid = new SessionId(intentSid);

        FetchNewsFeedTask task = new FetchNewsFeedTask(sid);
        task.execute((Void) null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.action_find_users) {
            intent = new Intent(this, UserSearchActivity.class);
        } else if (id == R.id.action_record_squeak) {
            intent = new Intent(this, RecordSqueakActivity.class);
        }

        if (intent != null) {
            intent.putExtra(SqueakerAndroidConstants.SESSION_ID_FIELD, sid.getId());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous news feed fetch task.
     */
    public class FetchNewsFeedTask extends AsyncTask<Void, Void, Boolean> {
        private SessionId mSid;

        FetchNewsFeedTask(SessionId sid) {
            mSid = sid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                newsFeedSqueaks = JsonApi.updateFeed(mSid);
                return true;
            } catch (Exception e) {
                newsFeedSqueaks = new ArrayList<>();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (newsFeedSqueaks.size() == 0) {
                mNoDataTextView.setVisibility(View.VISIBLE);
                mSqueakListView.setVisibility(View.GONE);
            } else {
                mNoDataTextView.setVisibility(View.GONE);
                mSqueakListView.setVisibility(View.VISIBLE);
            }
        }
    }
}
