package com.example.onlinequizchecker;


import android.app.Activity;

/**
 * Created by 311165906 on 10/03/2016.
 */
public class LectDropBoxAuthController {
    private MainActivity activity;
    private DropBoxSimple dbx;
    public LectDropBoxAuthController(MainActivity activity)
    {
        this.activity = activity;
        this.activity.setContentView(R.layout.lect_dropboxauthview);
        dbx = new DropBoxSimple(this.activity);
    }
}
