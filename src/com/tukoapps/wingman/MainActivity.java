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

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private TextView info;
    public String name = "";
    
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
        
        info = (TextView) findViewById(R.id.info);

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
	        onSessionStateChange(session, session.getState(), null);
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
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
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
    	if (session != null && session.isOpened()) {
    		Log.d("DEBUG", "facebook session is open ");
    		// make request to the /me API
    		Request.newMeRequest(session, new Request.GraphUserCallback() {
			  // callback after Graph API response with user object
			  @Override
			  public void onCompleted(GraphUser user, Response response) {
			    if (user != null) {
			      Log.d("RESPONSE", "Hello " + user.getName() + "!");
			      name = user.getName();
			      
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
        Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
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
            //Log.d("JSON", "dsf" + result);
            if (!result.contains("fb_access_token")){
            	displayBarList(result);
            }else{
            	getLocationAndGetBars(result);
            }
        }
    }
    
    private void getLocationAndGetBars(String response){
    	JSONObject responseObj = null;
    	String id = null;
     	try {
			responseObj = new JSONObject(response);
			id = responseObj.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Location loc = getLastLocation(this);
//        Log.d("STRING", "http://wingman.ngrok.com/api/v1/bars?user_id="+id+"&lat="+loc.getLatitude()+"&lon="+loc.getLongitude());
    	new RequestTask().execute("http://www.get-wingman.com/api/v1/bars?user_id="+id+"&lat="+loc.getLatitude()+"&lon="+loc.getLongitude());
    }
    
    public static Location getLastLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<Location>();
        for (String provider : providers) {
             Location location = manager.getLastKnownLocation(provider);
             if (location != null && location.getAccuracy() !=0.0) {
                 locations.add(location);
             }
        }
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location location2) {
                return (int) (location.getAccuracy() - location2.getAccuracy());
            }
        });
        if (locations.size() > 0) {
            return locations.get(0);
        }
        return null;
    }
    
    private void displayBarList(String response){
    	 
    	  JSONObject responseObj = null; 
    	 
    	  try {
    	   JSONArray barListObj = new JSONArray(response);
    	  
    	   barList = new ArrayList<Bar>();
    	   for (int i=0; i<barListObj.length(); i++){
    	 
    	    //get the country information JSON object
    	    JSONObject json_bar = barListObj.getJSONObject(i);
    	    Bar new_bar = new Bar();
    	    new_bar.setName(json_bar.getString("name"));
    	    new_bar.setRating(json_bar.getString("rating"));
    	    new_bar.setImage(json_bar.getString("image_url"));

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
				Bar bar = new Bar();
				bar = (Bar) arg0.getItemAtPosition(arg2);
				Intent show_bar = new Intent(MainActivity.this, ShowBar.class);
				show_bar.putExtra("BarDeets", (Parcelable) bar);
				startActivity(show_bar);
//	    	    Toast.makeText(getApplicationContext(),
//	    	      bar.getName(), Toast.LENGTH_SHORT).show();
			}
    	   });
    	 
    	 
    	 
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
    	   TextView rating;
    	   TextView name;
    	   ImageView image_thumb;
    	   ImageView image_back;
    	  }
    	 
    	  @Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    		  
    		  
    	   ViewHolder holder = null;
    	   Log.v("ConvertView", String.valueOf(position));
    	   if (convertView == null) {
    		   LayoutInflater vi = (LayoutInflater)getSystemService(
  		    	     Context.LAYOUT_INFLATER_SERVICE);
    		   holder = new ViewHolder();
    		   if (position == 0){
    			   convertView = vi.inflate(R.layout.bar_info_first, null);
    			   holder.rating = (TextView) convertView.findViewById(R.id.code_first);
    	    	   holder.name = (TextView) convertView.findViewById(R.id.name_first);
    	    	   holder.image_back = (ImageView) convertView.findViewById(R.id.image_back);
    	    	   holder.image_thumb = (ImageView) convertView.findViewById(R.id.image_first);
    		   }else{
		    	   convertView = vi.inflate(R.layout.bar_info, null);
		    	   holder.rating = (TextView) convertView.findViewById(R.id.code);
		    	   holder.name = (TextView) convertView.findViewById(R.id.name);
		    	   holder.image_thumb = (ImageView) convertView.findViewById(R.id.image);
    		   }
    		   
    		   convertView.setTag(holder);
    	   } else {
    	    holder = (ViewHolder) convertView.getTag();
    	   }
    	  
    	   Bar bar = barList.get(position);
    	   holder.rating.setText(bar.getRating());
    	   holder.name.setText(bar.getName());
    	  
    	   if (holder.image_back != null)
    		   holder.image_back.setImageBitmap(bar.getImage());
 		   holder.image_thumb.setImageBitmap(bar.getImage());
    	 
    	   return convertView;
    	 
    	  }
    	 
    	 }
    
    
}


