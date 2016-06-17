package com.example.onlinequizchecker;

/**
 * The Class LectDropBoxAuthController. This class initiates the authorization
 * process in the lecturer's phone.
 */
public class LectDropBoxAuthController {

	/** The activity. */
	private MainActivity activity;

	/**
	 * Instantiates a new lect drop box auth controller.
	 *
	 * @param activity
	 *            the activity
	 */
	public LectDropBoxAuthController(MainActivity activity) {
		this.activity = activity;
		this.activity.setContentView(R.layout.lect_dropboxauthview);
		this.activity.setDropboxAuthRequest(true);
		new DropBoxSimple(this.activity);
	}
}
