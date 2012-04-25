/**
 * @author brian.gormanly
 * OASVN (Open Android SVN)
 * Copyright (C) 2012 Brian Gormanly
 * Valley Technologies Group
 * http://www.valleytg.com
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. 
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

package com.valleytg.oasvn.android.ui.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.LogItem;
import com.valleytg.oasvn.android.util.DateUtil;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Revisions extends ListActivity {
	
	/**
	 * Controls
	 */
	OASVNApplication app;
	Button btnBack;
	
	/**
	 * Thread control
	 */
	Boolean running = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revisions);
        
        try {
	        // get the application
	        this.app = (OASVNApplication)getApplication();
	        
	        // initialize the buttons
	        btnBack = (Button) findViewById(R.id.revisions_back);
	        
	        // set the list adapter
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.revision_item));
	        
	        // populate the list
	        populateList();
	        
	        // handle back button press
	        this.btnBack.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Revisions.this.finish();
				}
			});

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
			
			// populate the list
	        populateList();
		}
		catch (Exception e) {
        	// problem loading activity
        	this.finish();
        	e.printStackTrace();
        }
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		try {
			SVNLogEntry thisEntry = app.getCurrentConnection().getRevisions().get(position);

			// set the current log
			app.setCurrentRevision(thisEntry);
			
			// go to the log details screen
			Intent intent = new Intent(Revisions.this, RevisionDetails.class);
			startActivity(intent);
		} 
		catch (Exception e) {
			//Toast.makeText(this, getString(R.string.create_connection), 1500).show();
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume() {
		try {
			super.onResume();
			
			// populate the list
	        populateList();
		}
		catch (Exception e) {
        	// problem loading activity
        	this.finish();
        	e.printStackTrace();
        }
	}


	private void populateList() {
    	// Initialize array of choices
        String[] entries;
        entries = null;
 
        // try to retrieve the local staff / users
        try {
        	
        	// check to see that there is a current connection
        	if(this.app.getCurrentConnection() != null) {
        		
        		
        		
        		// add the revisions to the connection
        		//app.getCurrentConnection().setDirectories(revisions);
        		
        		if(app.getCurrentConnection().getRevisions().size() > 0) {
        			// sort the list by staff first name
        			/*
	        		Collections.sort(revisions, new Comparator<SVNDirEntry>(){
						public int compare(SVNDirEntry lhs, SVNDirEntry rhs) {
							SVNDirEntry p1 = (SVNDirEntry) lhs;
							SVNDirEntry p2 = (SVNDirEntry) rhs;
	    	               return p2.getDate().compareTo(p1.getDate());
						}
	    	 
	    	        });
	    	        */

	        		// revisions ready to go
	        		entries = new String[app.getCurrentConnection().getRevisions().size()];
	        		int i = 0;
	        		for(SVNLogEntry entry : app.getCurrentConnection().getRevisions()) {
	        			entries[i] = entry.getRevision() + " | " + entry.getMessage() + " | " + DateUtil.getSimpleDateTime(entry.getDate(), this) 
	            		+ "\nAuthor: " + entry.getAuthor();
	        			i++;
	            	}

	        		
	        	}
	        	else {
	        		// no users in the local db
	        		entries = new String[1];
	        		entries[0]= getString(R.string.no_revisions);
	        		Toast toast=Toast.makeText(this, getString(R.string.no_revisions), 1500);
	        		toast.show();
	        	}
        	}
        	
        	
        }
        catch(Exception e) {
        	// no user/staff data in application yet. call for a refresh
        	entries = new String[1];
        	entries[0] = getString(R.string.no_revisions);
        	e.printStackTrace();
        }
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.revision_item, entries));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

    }
	
	class RetrieveRevisionsThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(Revisions.this);
	        dialog.setMessage(getString(R.string.in_progress));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	    }
		
		@Override
		protected String doInBackground(Void... unused) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			String returned;
			
			try {
		
				// get all the revisions
				app.getCurrentConnection().retrieveAllRevisions(app);
				returned = getString(R.string.revisions_retrieved);
			}
    		catch(VerifyError ve) {
    			String msg = ve.getMessage();
    			
    			// log this failure
    			app.getCurrentConnection().createLogEntry(app, getString(R.string.error), ve.getMessage().substring(0, 19), ve.getMessage().toString());
    			
    			ve.printStackTrace();
    			return getString(R.string.verify) + " " + msg;
    		}
    		catch(Exception e) {
    			String msg = e.getMessage();
    			
    			// log this failure
    			app.getCurrentConnection().createLogEntry(app, getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
    			
    			e.printStackTrace();
    			return getString(R.string.exception) + " " + msg;
    		}

			return returned;
		}
		
		protected void onPostExecute(final String result) {

			android.util.Log.d(getString(R.string.alarm), getString(R.string.revision_success));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateList();
	        
			// unset the running flag
			Revisions.this.running = false;
	    }
	}
}
