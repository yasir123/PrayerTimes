package com.example.prayertimescalculator;

import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
	 
	public class MainActivity extends Activity  {
	    private TextView txtPrayerTimes;
	    protected LocationManager locationManager;
	    protected LocationListener locationListener;
	    protected Context context;
	    TextView txtLat;
	    String lat;
	    String provider;
	    protected String latitude,longitude; 
	    protected boolean gps_enabled,network_enabled;
	    GPSTracker gps;
	    
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	       	        
	        txtPrayerTimes = (TextView) findViewById(R.id.txtPrayerTimes);
	        startService(new Intent(MainActivity.this, RedScreenService.class));
	        gps = new GPSTracker(MainActivity.this);
	        
            // check if GPS enabled     
            if(gps.canGetLocation()){
                 
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                final Calendar c = Calendar.getInstance();
   	    	    int  mYear = c.get(Calendar.YEAR);
   	    	    int  mMonth = c.get(Calendar.MONTH);
   	    	    int mDay = c.get(Calendar.DAY_OF_MONTH);
   	    	    
   	    	    SharedPreferences preferences=getSharedPreferences("Prayer",Context.MODE_PRIVATE);
   				Editor editor = preferences.edit();
   				editor.putString("Latitude", String.valueOf(latitude));
   				editor.putString("Longitude", String.valueOf(longitude));
   	    	    getTime(latitude, longitude,mYear,mMonth,mDay);
   	    	    
   	    	    
   	    	    
                // \n is for new line
   	    	    
   	    	    
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
	    	 
           
	    	 
	        
	       
	    }
	 
	        // onClick method for the button
	    public void getTime(double latitude, double longitude,int year,int month,int day) {
	        // Retrive lat, lng using location API
//	         = 31.4187 ;
//	        = 73.0791;
	        double timezone = (Calendar.getInstance().getTimeZone()
	                .getOffset(Calendar.getInstance().getTimeInMillis()))
	                / (1000 * 60 * 60);
	        PrayTime prayers = new PrayTime();
	 
	        prayers.setTimeFormat(prayers.Time12);
	        prayers.setCalcMethod(prayers.Karachi);
	        prayers.setAsrJuristic(prayers.Shafii);
	        prayers.setAdjustHighLats(prayers.Shafii);
	        int[] offsets = { 0, 0, 0, 0, 0, 0, 0 }; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
	        prayers.tune(offsets);
	 
	        Date now = new Date(2016, 5, 25);
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(now);
	 
	        ArrayList prayerTimes = prayers.getPrayerTimes(cal, latitude,
	                longitude, timezone);
	        ArrayList prayerNames = prayers.getTimeNames();
	 
	        for (int i = 0; i < prayerTimes.size(); i++) {
	            txtPrayerTimes.append("\n" + prayerNames.get(i) + " - "
	                    + prayerTimes.get(i));
	        }
	       
	        SharedPreferences preferences=getSharedPreferences("Prayer",Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			
			editor.putString("fajar", prayerTimes.get(0).toString());
			editor.putString("sunrise", prayerTimes.get(1).toString());
			editor.putString("duhar", prayerTimes.get(2).toString());
			editor.putString("asar", prayerTimes.get(3).toString());
			editor.putString("maghrib", prayerTimes.get(4).toString());
			editor.putString("Isha", prayerTimes.get(5).toString());
			editor.commit();
			
			
			
	    }
	    
	  
}

