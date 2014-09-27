package com.tukoapps.wingman;

import java.util.Arrays;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

public class LaunchActivity extends FragmentActivity{
	
	ProgressBar spinner;
	LoginButton button;
	private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.launch);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
//        if (savedInstanceState == null) {
//            // Add the fragment on initial activity setup
//            mainFragment = new MainFragment();
//            getSupportFragmentManager()
//            .beginTransaction()
//            .add(android.R.id.content, mainFragment)
//            .commit();
//            Log.d("LAUNCH", "" + mainFragment.loggedIn());
//            if (mainFragment.loggedIn()){
//            	Intent main = new Intent(this, MainActivity.class);
//            	startActivity(main);
//            	finish();
//            }
//        } else {
//            // Or set the fragment from restored state info
//            mainFragment = (MainFragment) getSupportFragmentManager()
//            .findFragmentById(android.R.id.content);
//        }
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        button = (LoginButton) findViewById(R.id.authButton);
        button.setVisibility(View.INVISIBLE);
//        Session session = Session.getActiveSession();
//	    if (session != null &&
//	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState(), null);
//	    }else{
	    	spinner.setVisibility(View.INVISIBLE);
    		button.setVisibility(View.VISIBLE);
//	    }
        
        
//        
//        button.setReadPermissions(Arrays.asList("basic_info","email"));
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	if (session != null && session.isOpened()) {
    		Log.d("DEBUG", "facebook session is open ");
    		// make request to the /me API
    		Request.newMeRequest(session, new Request.GraphUserCallback() {
			  // callback after Graph API response with user object
			  @Override
			  public void onCompleted(GraphUser user, Response response) {
			    if (user != null) {
			      Log.d("RESPONSE", "Hello " + user.getName() + "!");
			      Intent main = new Intent(LaunchActivity.this, MainActivity.class);
			      startActivity(main);
			      LaunchActivity.this.finish();
			    }
			  }
			}).executeAsync();
    	}else {
    		spinner.setVisibility(View.INVISIBLE);
    		button.setVisibility(View.VISIBLE);
    	}
    }
	
	 @Override
    public void onResume() {
        super.onResume();
        
//        Session session = Session.getActiveSession();
//	    if (session != null &&
//	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState(), null);
//	    }
        uiHelper.onResume();
    }
 
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
