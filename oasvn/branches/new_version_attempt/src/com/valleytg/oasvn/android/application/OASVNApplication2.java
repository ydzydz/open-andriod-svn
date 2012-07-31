package com.valleytg.oasvn.android.application;

import java.io.File;
import java.util.ArrayList;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.model.Connection;
import com.valleytg.oasvn.android.model.LogItem;
import com.valleytg.oasvn.android.util.Settings;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class OASVNApplication2 extends Application {
	
	/**
     * Current connection
     */
    private Connection currentConnection;
    
    /**
     * Current Log
     */
    private LogItem currentLog;
    
    /**
     * Current Revision
     */
    private SVNLogEntry currentRevision;
    
    /**
     * All connections
     */
    private ArrayList<Connection> allConnections;
    
    
    /**
     * BasicAithenticationManager sets up the svn authentication with the server.
     */
    private BasicAuthenticationManager myAuthManager;
    
    /**
     * 
     */
    private ISVNAuthenticationManager myASVNAuthManager;
    
    
	
	
	/**
     * Constructor
     */
	public OASVNApplication2() {
   	
		// initialize arraylists
		this.allConnections = new ArrayList<Connection>();
		
		// Initialize the settings
			Settings.getInstance();
		
		// initialize the storage state
		this.discoverStorageState();
   	
	}
	
	
	
	
	
	
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
    
    
    public void initISVNAuthManager() {
    	try {
	    	// check to see that we have a current connection
	    	if(currentConnection != null) { 
	    		// initialize the Auth manager
	    		myISVNAuthManager = SVNWCUtil.cr
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	
	
	
}
