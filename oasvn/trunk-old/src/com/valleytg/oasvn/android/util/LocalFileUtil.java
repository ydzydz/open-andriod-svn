package com.valleytg.oasvn.android.util;

import java.io.File;

import android.os.Environment;
import android.util.Log;

import com.valleytg.oasvn.android.R;

public class LocalFileUtil {

	
	/**
	 * Maintains application perspective on whether or not there is external 
	 * storage available.
	 */
	private boolean mExternalStorageAvailable = false;
	
	/**
	 * Maintains applications perspective on whether or not the external storage
	 * is writable.
	 */
    private boolean mExternalStorageWriteable = false;
	
	
	
	
	
	
	/**
	 * Checks the state of storage on the device and sets 2 members, 
	 * setmExternalStorageAvailable and setmExternalStorageWriteable 
	 * accordingly.  These values can be used by the application to 
	 * Determine the proper course of action for dealing with storage 
	 * on the particular device.
	 */
	private void discoverStorageState() {
    	// get the current state of external storage
    	String state = Environment.getExternalStorageState();

	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        // We can read and write the media
	        setmExternalStorageAvailable(setmExternalStorageWriteable(true));
	    } 
	    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        // We can only read the media
	        setmExternalStorageAvailable(true);
	        setmExternalStorageWriteable(false);
	    } 
	    else {
	        // Something else is wrong. It may be one of many other states, but all we need
	        //  to know is we can neither read nor write
	        setmExternalStorageAvailable(setmExternalStorageWriteable(false));
	    }
    }
	
	/**
	 * Initializes the path for the local working copy or exports.  Checks to see if the
	 * path exists and if not creates the local path.
	 */
	private void initializePath() {
    	try {
    		String mainFolder = "";
    		
    		// check to see if there is a default folder from the settings
    		if(Settings.getInstance().getRootFolder().length() == 0) {
    			mainFolder = "OASVN/";
    		}
    		else {
    			mainFolder = Settings.getInstance().getRootFolder();
    		}
    		
    		// set the full path to main
    		this.setFullPathToMain(Environment.getExternalStorageDirectory() + "/" + mainFolder);
    		
	    	File folder = new File(this.getFullPathToMain());
	    	
		    if(!folder.exists()){
		    	// folder does not yet exist, create it.
		         folder.mkdir();
		         this.setRootPath(folder);
		         Log.i(getString(R.string.FILE), getString(R.string.directory_created)); 
		    }
		    else {
		    	// folder already exists
		    	this.setRootPath(folder);
		    	//Log.i(getString(R.string.FILE), getString(R.string.directory_exists)); 
		    }
		    
    	}
    	catch(Exception e) {
    		Log.e("FILE", "can't create folder");
    		e.printStackTrace();
    	}
    }
}
