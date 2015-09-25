package com.squeaker.squeaker;

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
    private TextView noDataTextView;
    private ListView squeakListView;

    private SessionId sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        noDataTextView = (TextView) findViewById(R.id.noData);
        squeakListView = (ListView) findViewById(R.id.newsFeedSqueaks);
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
    public class FetchNewsFeedTask extends AsyncTask<Void, Void, ArrayList<SqueakMetadata>> {
        private SessionId sid;

        FetchNewsFeedTask(SessionId sid) {
            this.sid = sid;
        }

        @Override
        protected ArrayList<SqueakMetadata> doInBackground(Void... params) {
            try {
                return JsonApi.updateFeed(sid);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<SqueakMetadata> squeaks) {
            super.onPostExecute(squeaks);
            if (squeaks.size() == 0) {
                noDataTextView.setVisibility(View.VISIBLE);
                squeakListView.setVisibility(View.GONE);
            } else {
                noDataTextView.setVisibility(View.GONE);
                squeakListView.setVisibility(View.VISIBLE);
            }

            squeakListView.setAdapter(new SqueakListAdapter(NewsFeedActivity.this, R.layout.squeak_badge_layout, sid, squeaks));
        }
    }
}
