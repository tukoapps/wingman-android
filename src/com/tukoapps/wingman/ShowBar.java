package com.tukoapps.wingman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ShowBar extends Activity{
	
	private TextView name;
	private TextView rating;
	private TextView description;
	private TextView schedule;
	private TextView music;
	private TextView food;
	private TextView drink_price;
	private TextView currentUsers;
	private ImageView back;
	private ImageView thumb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_bar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bar bar = (Bar) getIntent().getParcelableExtra("BarDeets");
//		bar.loadHeader();
		name = (TextView) findViewById(R.id.deets_name);
		rating = (TextView) findViewById(R.id.deets_rating);
		description = (TextView) findViewById(R.id.tvDesc);
		back = (ImageView) findViewById(R.id.deets_back);
		currentUsers = (TextView) findViewById(R.id.textView2);
		thumb = (ImageView) findViewById(R.id.deets_thumb);
		schedule = (TextView) findViewById(R.id.tvSched);
		music = (TextView) findViewById(R.id.tvMusic);
		food = (TextView) findViewById(R.id.tvFood);
		drink_price = (TextView) findViewById(R.id.tvDrinkPrice);
		
		name.setText(bar.getName());
		currentUsers.setText(bar.getUsers() + "+");
		rating.setText(bar.getRating());
		description.setText(bar.getDescription());
		back.setImageBitmap(bar.getImage());
		thumb.setImageBitmap(bar.getLogo());
		schedule.setText(bar.getSchedule());
		music.setText(bar.getMusic());
		food.setText(bar.getFood());
		drink_price.setText("$" + bar.getDrinkPrice());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void displayBarStats(String response){
   	 
  	  JSONObject responseObj = null; 
  	 
  	  try {
  	   JSONObject barObj = new JSONObject(response);
  	  } catch (JSONException e) {
  	   e.printStackTrace();
  	  }
  	 
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
//            Log.d("JSON", result);
            displayBarStats(result);
        }
    }

}
