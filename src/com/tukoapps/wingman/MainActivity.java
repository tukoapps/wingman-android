package com.tukoapps.wingman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tukoapps.wingman.MyLocation.LocationResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
	
	private final static String TAG = "MAIN ACTIVITY";
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private TextView noBar;
    private ProgressBar loader;
    public static String name = "";
    private String id;
    private boolean oneanddone = false;
    
    ArrayList<Bar> barList;
    
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
	public boolean userAuthenticated;
    
    static MyCustomAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        
        loader = (ProgressBar) findViewById(R.id.loader);
        noBar = (TextView) findViewById(R.id.tvNoBar);
        loader.setVisibility(View.VISIBLE);
        

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        LoginButton button = (LoginButton) findViewById(R.id.authButtonMain);
        
        Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState(), null);
	    }else{
	    	Intent main = new Intent(MainActivity.this, LaunchActivity.class);
		    startActivity(main);
		    MainActivity.this.finish();
	    }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
            	//Toast.makeText(this, "Profile View would appear", Toast.LENGTH_SHORT).show();
                //mTitle = getString(R.string.title_section1);
                break;
            case 2:
//            	Toast.makeText(this, "Settings View would appear", Toast.LENGTH_SHORT).show();
            	Intent settings = new Intent(this, SettingActivity.class);
            	startActivity(settings);            	
                //mTitle = getString(R.string.title_section2);
                break;
            case 3:
                //mTitle = getString(R.string.title_section3);
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	Log.d("DEBUG", "session state changed");
    	if (session != null && session.isOpened()) {
    		Log.d("DEBUG", "facebook session is open ");
    		// make request to the /me API;
    		Request.newMeRequest(session, new Request.GraphUserCallback() {
			  // callback after Graph API response with user object
			  @Override
			  public void onCompleted(GraphUser user, Response response) {
			    if (user != null) {
			      Log.d("RESPONSE", "Hello " + user.getName() + "!");
			      name = user.getName();
			      mNavigationDrawerFragment.loadMenu(name);
			      String access_token = Session.getActiveSession().getAccessToken();
			      String fb_id = user.getId();
			      new RequestTask().execute("http://www.get-wingman.com/api/v1/sessions/new?access_token=" + access_token);
			    }
			  }
			}).executeAsync();
    	}else{
    		Intent main = new Intent(MainActivity.this, LaunchActivity.class);
		    startActivity(main);
		    MainActivity.this.finish();
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
    
    public static void updateBarList(){
    	dataAdapter.notifyDataSetChanged(); 
    }
    
    class RequestTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("JSON", "dsf" + result);
            if (!result.contains("fb_access_token")){
            	loader.setVisibility(View.GONE);
            	displayBarList(result);
            }else{
            	getLocationAndGetBars(result);
            	
            }
        }
    }
    
    private void getLocationAndGetBars(String response){
    	JSONObject responseObj = null;
    	
     	try {
			responseObj = new JSONObject(response);
			id = responseObj.getString("id");
     	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
     	LocationAlarm alarm = new LocationAlarm();
//      alarm.CancelAlarm(this);
     	final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
             
	     	final AlertDialog.Builder builder =
	                new AlertDialog.Builder(this);
	        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
	        final String message = "To get better bar recommendations,"
	            + " please enable location services. Click OK to go there now.";
	 
	        builder.setMessage(message)
	            .setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface d, int id) {
	                        MainActivity.this.startActivity(new Intent(action));
	                        d.dismiss();
	                    }
	            })
	            .setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface d, int id) {
	                        d.cancel();
	                    }
	            });
	        builder.create().show();
        }
        
     	//turnGPSOn();
    	alarm.SetAlarm(MainActivity.this, id);
    	//Log.d("NOWWWW", "about to json");
    	LocationResult locationResult = new LocationResult(){
    	    @Override
    	    public void gotLocation(Location location){
    	        //Got the location!
    	    	if (location == null){
    	    		final AlertDialog.Builder builder =
    		                new AlertDialog.Builder(MainActivity.this);
    		        final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
    		        final String message = "We need some sort of location services enabled"
    		            + " so we can find bars near your area. Click OK to go there now.";
    		 
    		        builder.setMessage(message)
    		            .setPositiveButton("OK",
    		                new DialogInterface.OnClickListener() {
    		                    public void onClick(DialogInterface d, int id) {
    		                        MainActivity.this.startActivity(new Intent(action));
    		                        d.dismiss();
    		                    }
    		            })
    		            .setNegativeButton("Cancel",
    		                new DialogInterface.OnClickListener() {
    		                    public void onClick(DialogInterface d, int id) {
    		                        d.cancel();
    		                    }
    		            });
    		        builder.create().show();
    		        loader.setVisibility(View.GONE);
    	    	}else{
    	    		new RequestTask().execute("http://www.get-wingman.com/api/v1/bars?user_id="+id+"&lat="+location.getLatitude()+"&lon="+location.getLongitude());
    	    	}
    	    }
    	};
    	MyLocation myLocation = new MyLocation();
    	myLocation.getLocation(this, locationResult);
    }
    
    
    
    private void displayBarList(String response){
    	 
    	  JSONObject responseObj = null; 
    	 
    	  try {
    	   JSONArray barListObj = new JSONArray(response);
    	  
    	   barList = new ArrayList<Bar>();
    	   if (barListObj.length() < 1){
    		   noBar.setVisibility(View.VISIBLE);
    	   }else{
	    	   for (int i=0; i<barListObj.length(); i++){
	    	 
	    	    //get the country information JSON object
	    	    JSONObject json_bar = barListObj.getJSONObject(i);
	    	    Bar new_bar = new Bar(this);
	    	    new_bar.setUsers(json_bar.getInt("current_users"));
	    	    new_bar.setName(json_bar.getString("name"));
	    	    new_bar.setRating(json_bar.getString("rating"));
	    	    new_bar.setImage(json_bar.getString("image_url"));
	    	    new_bar.setLogo(json_bar.getString("logo_url"));
	    	    new_bar.setDescription(json_bar.getString("description"));
	    	    new_bar.setSchedule(json_bar.getString("schedule"));
	    	    new_bar.setFood(json_bar.getString("food"));
	    	    new_bar.setMusic(json_bar.getString("music"));
	    	    if (json_bar.isNull("drink_price"))
	    	    	new_bar.setDrinkPrice(0.0);
	    	    else
	    	    	new_bar.setDrinkPrice(json_bar.getDouble("drink_price"));
	
	    	    //add to country array list
	    	    barList.add(new_bar);
	    	   }
	    	   //create an ArrayAdaptar from the String Array
	    	   dataAdapter = new MyCustomAdapter(this,
	    	     R.layout.bar_info, barList);
	    	   ListView listView = (ListView) findViewById(R.id.barListView);
	    	   // Assign adapter to ListView
	    	   listView.setAdapter(dataAdapter);
	    	 
	    	   //enables filtering for the contents of the given ListView
	    	   listView.setTextFilterEnabled(true);
	    	 
	    	   listView.setOnItemClickListener(new OnItemClickListener() {
	    	    
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Bar bar = new Bar(MainActivity.this);
					bar = (Bar) arg0.getItemAtPosition(arg2);
					Intent show_bar = new Intent(MainActivity.this, ShowBar.class);
					show_bar.putExtra("BarDeets", (Parcelable) bar);
					startActivity(show_bar);
	//	    	    Toast.makeText(getApplicationContext(),
	//	    	      bar.getName(), Toast.LENGTH_SHORT).show();
				}
	    	   });
    	   }
    	 
    	  } catch (JSONException e) {
    	   e.printStackTrace();
    	  }
    	 
    	 }
    
    
    
    
    private class MyCustomAdapter extends ArrayAdapter<Bar> {
    	 
    	  private ArrayList<Bar> barList;
    	 
    	  public MyCustomAdapter(Context context, int textViewResourceId, 
    	    ArrayList<Bar> countryList) {
    	   super(context, textViewResourceId, countryList);
    	   this.barList = new ArrayList<Bar>();
    	   this.barList.addAll(countryList);
    	  }
    	 
    	  private class ViewHolder {
    	   TextView users;
    	   TextView name;
    	   ImageView logo;
    	   ImageView image;
    	  }
    	 
    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  
    		  
    	   ViewHolder holder = null;
    	   Log.v("ConvertView", String.valueOf(position));
    	   
    	   Bar bar = barList.get(position);
    	   
    	   
//    	   if (convertView == null) {
    		   LayoutInflater vi = (LayoutInflater)getSystemService(
  		    	     Context.LAYOUT_INFLATER_SERVICE);
    		   holder = new ViewHolder();
    		   if (position == 0){
    			   convertView = vi.inflate(R.layout.bar_info_first, null);
    			   holder.users = (TextView) convertView.findViewById(R.id.code_first);
    	    	   holder.name = (TextView) convertView.findViewById(R.id.name_first);
    	    	   holder.image = (ImageView) convertView.findViewById(R.id.image_back);
    	    	   holder.logo = (ImageView) convertView.findViewById(R.id.image_first);
    		   }else{
		    	   convertView = vi.inflate(R.layout.bar_info, null);
		    	   holder.users = (TextView) convertView.findViewById(R.id.code);
		    	   holder.name = (TextView) convertView.findViewById(R.id.name);
		    	   holder.logo = (ImageView) convertView.findViewById(R.id.image);
    		   }
    		   
    		   convertView.setTag(holder);
//    	   } else {
//    	    holder = (ViewHolder) convertView.getTag();
//    	   }
    	   
    	   holder.users.setText("" + bar.getUsers());
    	   holder.name.setText(bar.getName());
    	  
    	   if (holder.image != null)
    		   holder.image.setImageBitmap(bar.getImage());
 		   holder.logo.setImageBitmap(bar.getLogo());
    	 
    	   return convertView;
    	 
    	  }
    	 
    	 }

	
    
    
}


