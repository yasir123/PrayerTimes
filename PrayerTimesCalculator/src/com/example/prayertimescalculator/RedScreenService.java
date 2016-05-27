package com.example.prayertimescalculator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RedScreenService extends Service {
	private View redView;

	//public static RedScreenService singleton;
	
	static boolean running = false;
	int total;
	int alt;
	int turn;
	int totalTakingTurn;
	int interval;
	boolean pm=true;
	boolean shouldTakeTime;
	 String localTime;
	
	private WindowManager windowManager;
	
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   SharedPreferences preferences=getSharedPreferences("DrSalomon",Context.MODE_PRIVATE);
			   /*if(preferences.getBoolean("red_screen_enabled", false))
			   {

			   }*/
			   SharedPreferences preferencesPrayerTimes=getSharedPreferences("Prayer",Context.MODE_PRIVATE);
			   
			   String isha=preferencesPrayerTimes.getString("Isha", "No");
			   
			   Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		         Date currentLocalTime = cal.getTime();
		         DateFormat date = new SimpleDateFormat("hh:mm a"); 
		         // you can get seconds by adding  "...:ss" to it
		         date.setTimeZone(TimeZone.getDefault()); 
                 Log.d("isha", isha);
		       localTime  = date.format(currentLocalTime);
		       if(localTime.contentEquals("12:00 am")){
		    	   
		    	   final Calendar c = Calendar.getInstance();
		    	 int  mYear = c.get(Calendar.YEAR);
		    	 int  mMonth = c.get(Calendar.MONTH);
		    	 int mDay = c.get(Calendar.DAY_OF_MONTH);
		    	StringBuilder dateCurrent= new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-").append(mYear).append(" ");
		    	   
		    	 double latitude=  Double.valueOf(preferencesPrayerTimes.getString("Latitude", ""));
		    	 double longitude=  Double.valueOf(preferencesPrayerTimes.getString("Longitude", ""));
		    	   
		    	   getTime(latitude, longitude,mYear,mMonth,mDay);
		       }
		       
		       if(localTime.contentEquals(preferencesPrayerTimes.getString("duhar", ""))){
		    	   
		    	   RedScreenService.this.redify();
		    	   
		       }
		       



	           




	         

		  
//
			 



		      handler.postDelayed(this, 30000);

		   }
		};


	public static boolean isRunning()
	{
		return running;
	}
		
    public IBinder onBind(Intent arg0) 
    {
          return null;
    }
    
    
    public void unredify()
    {
    	if(this.redView==null)
    		return;
    	
    	this.windowManager.removeView(this.redView);
    	this.redView = null;
    }
    
    public void redify()
    {
    	if(this.redView!=null)
    		return;
    	Log.d("enter", "Enter into redify");
		
		this.windowManager = ((WindowManager)getSystemService(WINDOW_SERVICE));
		Display localDisplay = this.windowManager.getDefaultDisplay();
	    int i1 = localDisplay.getHeight();
	    int i2 = localDisplay.getWidth();
	    if (i1 > i2) {
	      i2 = (int)(1.2D * i1);
	    }
	    
	    LayoutInflater localLayoutInflater = LayoutInflater.from(RedScreenService.this);
	    WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams(-2, -2, 2006, 65832, -3);
	    this.redView = localLayoutInflater.inflate(R.layout.red_screen, null);
	    this.redView.setBackgroundColor(Color.argb(150, 213, 15, 2));
	    
	    localLayoutParams.height = i1;
	    localLayoutParams.width = i2;
	   this.windowManager.addView(this.redView, localLayoutParams);

//    	
    }
    
    
    public int getStartTime()
    {
    	SharedPreferences preferences=getSharedPreferences("DrSalomon",Context.MODE_PRIVATE);
    	return preferences.getInt("red_screen_from", 0);
    }
    
    public void setStartTime(int value)
    {
    	SharedPreferences preferences=getSharedPreferences("DrSalomon",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("red_screen_from", value);
		editor.commit();
    }

    public int getStopTime()
    {
    	SharedPreferences preferences=getSharedPreferences("DrSalomon",Context.MODE_PRIVATE);
    	return preferences.getInt("red_screen_to", 0);
    }
    
    public void setStopTime(int value)
    {


		SharedPreferences preferences=getSharedPreferences("DrSalomon",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("red_screen_to", value);
		editor.commit();




    }

    
    public void onCreate() 
    {
          super.onCreate();
       
         
          startService();
    }

    private void startService()
    {

    	running = true;
    	handler.post(this.runnable);
    	
    }


    public void onDestroy() 
    {
          super.onDestroy();
          if (this.handler != null){
              this.handler.removeCallbacks(runnable);
          }
          this.unredify();
          running=false;
          
          //RedScreenService.singleton.stopSelf();
    }
    
    public void getTime(double latitude, double longitude,int year,int month,int day) {
        // Retrive lat, lng using location API
//         = 31.4187 ;
//        = 73.0791;
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
 
        Date now = new Date(year,month,day);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
 
        ArrayList prayerTimes = prayers.getPrayerTimes(cal, latitude,
                longitude, timezone);
        ArrayList prayerNames = prayers.getTimeNames();
 
//        for (int i = 0; i < prayerTimes.size(); i++) {
//            txtPrayerTimes.append("\n" + prayerNames.get(i) + " - "
//                    + prayerTimes.get(i));
//        }
       
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

