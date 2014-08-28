package com.tukoapps.wingman;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

public class Bar implements Parcelable{
	  
	 String rating = "0.0";
	 String name = null;
	 Bitmap image = null;
	 int index = 0;
	 Double lifeExpectancy = null;
	 Double gnp = null;
	 Double surfaceArea = null;
	 int population = 0;
	 
	 public Bar(){
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
	 public Bitmap getImage() {
	  return image;
	 }
	 public void setImage(String url) {
		 GetXMLTask task = new GetXMLTask();
         task.execute(new String[] { url });
	 }
	 public void setBitmap(Bitmap image){
		 this.image = image;
	 }
	 public int getIndex() {
	  return this.index;
	 }
	 public void setIndex(int index) {
	  this.index = index;
	 }
	 public Double getLifeExpectancy() {
	  return lifeExpectancy;
	 }
	 public void setLifeExpectancy(Double lifeExpectancy) {
	  this.lifeExpectancy = lifeExpectancy;
	 }
	 public Double getGnp() {
	  return gnp;
	 }
	 public void setGnp(Double gnp) {
	  this.gnp = gnp;
	 }
	 public Double getSurfaceArea() {
	  return surfaceArea;
	 }
	 public void setSurfaceArea(Double surfaceArea) {
	  this.surfaceArea = surfaceArea;
	 }
	 public int getPopulation() {
	  return population;
	 }
	 public void setPopulation(int population) {
	  this.population = population;
	 }
	 
	 private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
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
	        	setBitmap(result);
	        	MainActivity.updateBarList();
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
    }
}