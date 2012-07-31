package com.valleytg.oasvn.android.util;

import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;

public class AuthManagerUtil {

	
	
	
	
	/**
	 * This method should be called anytime a new currentConnection is chosen
	 * before any action is attempted.
	 * 
	 */
    public void initAuthManager() {
    	try {
	    	// check to see that we have a current connection
	    	if(currentConnection != null) { 
	    		// initialize the Auth manager
	    		myAuthManager = new BasicAuthenticationManager(this.currentConnection.getUsername(), this.currentConnection.getPassword());
	    	}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
