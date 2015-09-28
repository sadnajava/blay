package com.squeaker.app;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.squeaker.squeaker.ServerApi;
import com.squeaker.squeaker.Session;
import com.squeaker.squeaker.UserMetadata;

import java.util.ArrayList;

public class OpenUserProfileOnClickListener implements AdapterView.OnItemClickListener {
    private final Session session;
    private final ServerApi api;
    private final Context context;
    private final ArrayList<UserMetadata> users;

    public OpenUserProfileOnClickListener(Context context, Session session, ServerApi api, ArrayList<UserMetadata> users) {
        this.context = context;
        this.session = session;
        this.api = api;
        this.users = users;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        SqueakerActivityProtocol.serializeActivityParameters(intent, session, api);
        intent.putExtra(SqueakerActivityProtocol.USER_FIELD, users.get(position));
        context.startActivity(intent);
    }
}
