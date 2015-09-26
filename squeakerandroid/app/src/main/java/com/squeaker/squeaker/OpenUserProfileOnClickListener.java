package com.squeaker.squeaker;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class OpenUserProfileOnClickListener implements AdapterView.OnItemClickListener {
    private final Context context;
    private final Session session;
    private final ArrayList<UserMetadata> users;

    public OpenUserProfileOnClickListener(Context context, Session session, ArrayList<UserMetadata> users) {
        this.context = context;
        this.session = session;
        this.users = users;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(SqueakerAndroidConstants.SESSION_FIELD, session);
        intent.putExtra(SqueakerAndroidConstants.USER_FIELD, users.get(position));
        context.startActivity(intent);
    }
}
