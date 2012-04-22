package com.valleytg.oasvn.android.ui.activity;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;

import android.app.Activity;
import android.os.Bundle;

public class LogDetails extends Activity {
	/**
	 * Application layer
	 */
	OASVNApplication app;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.log_details);
	        
	        // get the application
	        this.app = (OASVNApplication)getApplication();
	        
	        try {
	        	
	        }
	        catch (Exception e) {
	        	// problem loading activity
	        	this.finish();
	        	e.printStackTrace();
	        }
	 }
	 
	 @Override
		protected void onRestart() {
			
			try{
				super.onRestart();
				
			}
			catch (Exception e) {
	        	// problem loading activity
	        	this.finish();
	        	e.printStackTrace();
	        }
		}
	 
	 @Override
		protected void onResume() {
			try {
				super.onResume();

			}
			catch (Exception e) {
	        	// problem loading activity
	        	this.finish();
	        	e.printStackTrace();
	        }
		}
}
