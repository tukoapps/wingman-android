package com.tukoapps.wingman;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

public class Bar implements Parcelable{
	  
	 String rating = "0.0";
	 String name = null;
	 Bitmap image = null;
	 Bitmap logo = null;
	 int users = 0;
	 String description = "";
	 String schedule = "";
	 String food = "";
	 String music = "";
	 double drink_price = 0;
	 Context context;
	 
	 public Bar(Context context){
		 this.context = context;
	 }
	  
	 public String getRating() {
	  return rating;
	 }
	 public void setRating(String rating) {
	  this.rating = rating;
	 }
	 public String getName() {
	  return name;
	 }
	 public void setName(String name) {
	  this.name = name;
	 }
	 public String getDescription() {
	  return description;
	 }
	 public void setDescription(String description) {
	  this.description = description;
	 }
	 public String getSchedule() {
	  return schedule;
	 }
	 public void setSchedule(String schedule) {
	  this.schedule = schedule;
	 }
	 public String getFood() {
	  return food;
	 }
	 public void setFood(String food) {
	  this.food = food;
	 }
	 public String getMusic() {
	  return music;
	 }
	 public void setMusic(String music) {
	  this.music = music;
	 }
	 public double getDrinkPrice() {
	  return drink_price;
	 }
	 public void setDrinkPrice(double drink_price) {
	  this.drink_price = drink_price;
	 }
	 public Bitmap getImage() {
	  return image;
	 }
	 public void setImage(String url) {
		 GetXMLTask task = new GetXMLTask();
		 task.islogo = false;
         task.execute(new String[] { url });
	 }
	 public void setBitmapImage(Bitmap image){
		 this.image = image;
	 }
	 
	 public Bitmap getLogo() {
		  return logo;
		 }
	 public void setLogo(String url) {
		 GetXMLTask task = new GetXMLTask();
		 task.islogo = true;
         task.execute(new String[] { url });
	 }
	 public void setBitmapLogo(Bitmap logo){
		 this.logo = logo;
	 }
	 public int getUsers() {
	  return this.users;
	 }
	 public void setUsers(int users) {
	  this.users = users;
	 }
	 
	 private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
		 public boolean islogo;
	        @Override
	        protected Bitmap doInBackground(String... urls) {
	            Bitmap map = null;
	            for (String url : urls) {
	                map = downloadImage(url);
	            }
	            return map;
	        }
	 
	        // Sets the Bitmap returned by doInBackground
	        @Override
	        protected void onPostExecute(Bitmap result) {
	        	if (islogo)
	        		setBitmapLogo(scaleDownBitmap(result, 50, context));
	        	else
	        		setBitmapImage(scaleDownBitmap(result, 100, context));
	        	MainActivity.updateBarList();
	        }
	        
	        public Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

	        	final float densityMultiplier = context.getResources().getDisplayMetrics().density;        
			
	        	int h= (int) (newHeight*densityMultiplier);
	        	int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
			
	        	photo=Bitmap.createScaledBitmap(photo, w, h, true);
			
	        	return photo;
			}
	 
	        // Creates Bitmap from InputStream and returns it
	        private Bitmap downloadImage(String url) {
	            Bitmap bitmap = null;
	            InputStream stream = null;
	            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	            bmOptions.inSampleSize = 1;
	 
	            try {
	                stream = getHttpConnection(url);
	                bitmap = BitmapFactory.
	                        decodeStream(stream, null, bmOptions);
	                stream.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	            return bitmap;
	        }
	 
	        // Makes HttpURLConnection and returns InputStream
	        private InputStream getHttpConnection(String urlString)
	                throws IOException {
	            InputStream stream = null;
	            URL url = new URL(urlString);
	            URLConnection connection = url.openConnection();
	 
	            try {
	                HttpURLConnection httpConnection = (HttpURLConnection) connection;
	                httpConnection.setRequestMethod("GET");
	                httpConnection.connect();
	 
	                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                    stream = httpConnection.getInputStream();
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	            return stream;
	        }
	    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// write your object's data to the passed-in Parcel
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(rating);
        out.writeValue(image);
        out.writeValue(logo);
        out.writeString(description);
        out.writeString(schedule);
        out.writeString(food);
        out.writeString(music);
        out.writeDouble(drink_price);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Bar> CREATOR = new Parcelable.Creator<Bar>() {
        public Bar createFromParcel(Parcel in) {
            return new Bar(in);
        }

        public Bar[] newArray(int size) {
            return new Bar[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Bar(Parcel in) {
        name = in.readString();
        rating = in.readString();
        image = (Bitmap) in.readValue(null);
        logo = (Bitmap) in.readValue(null);
        description = in.readString();
        schedule = in.readString();
        food = in.readString();
        music = in.readString();
        drink_price = in.readFloat();
    }
}