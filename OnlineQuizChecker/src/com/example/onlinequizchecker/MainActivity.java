package com.example.onlinequizchecker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	 private String userClassification;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
//	        setContentView(R.layout.lect_studentregistrationview);
	        new MainController(this);
	        userClassification = "";
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main,  menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();

	        //noinspection SimplifiableIfStatement
	        if (id == R.id.action_settings) {
	            return true;
	        }

	        return super.onOptionsItemSelected(item);
	    }
	    protected void onResume() {
	        super.onResume();
	        if (userClassification.equals("Lecturer")) {
	            if (DropBoxSimple.mDBApi.getSession().authenticationSuccessful()) {
	                try {
	                    // Required to complete auth, sets the access token on the session
	                    DropBoxSimple.mDBApi.getSession().finishAuthentication();
	                    new LectStudentRegistrationController(this);
	                    String accessToken = DropBoxSimple.mDBApi.getSession().getOAuth2AccessToken();

	                } catch (IllegalStateException e) {
	                    Log.i("DbAuthLog", "Error authenticating", e);
	                    Toast.makeText(this.getApplicationContext(), "exception", 5000).show();//////////////////
	                }
	            }
	        }
	    }
	    public String getUserClassification() {
	        return userClassification;
	    }

	    public void setUserClassification(String userClassification) {
	        this.userClassification = userClassification;
	    }

}
