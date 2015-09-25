package com.squeaker.squeaker;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

public class OpenUserProfileOnClickListener implements AdapterView.OnItemClickListener {
    private final Context context;
    private final SessionId sid;
    private final ArrayList<UserMetadata> users;

    public OpenUserProfileOnClickListener(Context context, SessionId sid, ArrayList<UserMetadata> users) {
        this.context = context;
        this.sid = sid;
        this.users = users;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(SqueakerAndroidConstants.SESSION_ID_FIELD, sid.getId());
        intent.putExtra(SqueakerAndroidConstants.USER_FIELD, users.get(position));
        context.startActivity(intent);
    }
}
