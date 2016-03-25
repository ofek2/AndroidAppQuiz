package com.example.onlinequizchecker;


import android.app.Activity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class DropBoxSimple {
    final static private String APP_KEY = "6uzu0uyprajxb0p";
    final static private String APP_SECRET = "e7iwzdqp4rwtu88";

    public static DropboxAPI<AndroidAuthSession> mDBApi;
    private MainActivity activity;
    public DropBoxSimple(MainActivity activity)

    {
        this.activity=activity;
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(activity);

    }
}
