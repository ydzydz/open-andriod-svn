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

package com.valleytg.oasvnlite.android.ui.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;

import com.valleytg.oasvnlite.android.R;
import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.model.LogItem;
import com.valleytg.oasvnlite.android.ui.activity.CommitActivity.CommitThread;
import com.valleytg.oasvnlite.android.util.DateUtil;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Revisions extends ListActivity {
	
	/**
	 * Controls
	 */
	OASVNApplication app;
	Button btnRefresh;
	Button btnBack;
	
	/**
	 * Thread control
	 */
	Boolean running = false;
	
	/**
	 * Entries string
	 */
	private String[] entries = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revisions);
        
        try {
	        // get the application
	        this.app = (OASVNApplication)getApplication();
	        
	        // initialize the buttons
	        btnRefresh = (Button) findViewById(R.id.revisions_refresh);
	        btnBack = (Button) findViewById(R.id.revisions_back);
	        
	        // set the list adapter
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.revision_item));
	        
	        // populate the list
	        this.btnRefresh.performClick();
	        
	        // Make sure the device does not go to sleep while in this acitvity
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        
	        // handle refresh button press
	        this.btnRefresh.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// null the connections revisions
					app.getCurrentConnection().setRevisions(null);
					
					// refresh
					Revisions.this.populateList();
				}
			});
	        
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
	        //this.btnRefresh.performClick();
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
			Revisions.this.populateList();
		}
		catch (Exception e) {
        	// problem loading activity
        	this.finish();
        	e.printStackTrace();
        }
	}


	private void populateList() {
        
		try {
			
    		// check to see if there are any revisions
    		if(app.getCurrentConnection().getRevisions() == null || app.getCurrentConnection().getRevisions().size() == 0) {
    			// prompt the user for the number of revisions to return
				AlertDialog.Builder builder = new AlertDialog.Builder(Revisions.this);
				
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle(R.string.revisions);
				builder.setMessage(getString(R.string.number_revisions));
				
				// add the number input to the dialog 
				final EditText input = new EditText(this);
				
				// limit the number of revisions to 999
				InputFilter[] FilterArray = new InputFilter[1];
				FilterArray[0] = new InputFilter.LengthFilter(3);
				input.setFilters(FilterArray);
				
				// populate the default value
				input.setText("50");
				
				// make the input type numeric
				input.setRawInputType(InputType.TYPE_CLASS_NUMBER);	
				
				// show the prompt
				builder.setView(input);
				
				builder.setPositiveButton(R.string.retrieve, new DialogInterface.OnClickListener() {

		            public void onClick(DialogInterface dialog, int which) {
		            	synchronized (this) {
		            		try{
		            			
		            	        
		            	        // get the value entered and make sure it is valid
		            	        String value = input.getText().toString();
		            	        Long lValue = 50L;
		            	        
		            	        // try to convert the value to a long
		            	        try {
		            	        	lValue = Long.parseLong(value);
		            	        }
		            	        catch(Exception e) {
		            	        	lValue = 50L;
		            	        	e.printStackTrace();
		            	        }
		            	        
		            			// set the running flag
		            			if(!Revisions.this.running) {
		        					Revisions.this.running = true;
		        					
		        					RetrieveRevisionsThread revisionsThread = new RetrieveRevisionsThread();
		        					revisionsThread.execute(lValue);
		        					
		        					// notify the user that we are working
			                		entries = new String[1];
			                		entries[0]= getString(R.string.in_progress);
		        					
		            			}
		            		} 
		            		catch(Exception e) {
		            			e.printStackTrace();
		            		}
		            	}	
		            }
		        });
				builder.setNegativeButton(R.string.cancel, null);
				builder.show();	
    			
    			
    		}
    			
    		// if there are revisions, display them.
    		if(app.getCurrentConnection().getRevisions() != null && app.getCurrentConnection().getRevisions().size() > 0) {

    			// reverse the sort order, most recent first.
    			Collections.sort(app.getCurrentConnection().getRevisions(), new Comparator<SVNLogEntry>(){
    				
					public int compare(SVNLogEntry lhs, SVNLogEntry rhs) {
						SVNLogEntry p1 = (SVNLogEntry) lhs;
						SVNLogEntry p2 = (SVNLogEntry) rhs;
    	               return Long.valueOf(p2.getRevision()).compareTo(Long.valueOf(p1.getRevision()));
					}
    	 
    	        });
    			
        		// revisions ready to go
        		entries = new String[app.getCurrentConnection().getRevisions().size()];
        		int i = 0;
        		for(SVNLogEntry entry : app.getCurrentConnection().getRevisions()) {
        			int messageLength = entry.getMessage().length();
        			if(messageLength < 21 ) {
        				messageLength = 20;
        			}
        			entries[i] = entry.getRevision() + " | "  + DateUtil.getSimpleDateTime(entry.getDate(), this) + "\n"
            		+ getString(R.string.author) + " " + entry.getAuthor();
        			i++;
            	}

        		
        	}
        	else {
        		// no users in the local db
        		entries = new String[1];
        		entries[0]= getString(R.string.in_progress);
        	}

        	
        
        
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.revision_item, entries));
	
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
        }
        catch(Exception e) {
        	// no user/staff data in application yet. call for a refresh
        	entries = new String[1];
        	entries[0] = getString(R.string.no_revisions);
        	e.printStackTrace();
        }

    }
	
	public String[] getEntries() {
		return this.entries;
	}
	
	public void setEntries(String[] entries) {
		this.entries = entries;
	}
	
	
	class RetrieveRevisionsThread extends AsyncTask<Long, Void, String> {

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
		protected String doInBackground(Long... values) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			String returned;
			
			try {
				
				// get the passed number of revisions
				Long revisions = values[0];
		
				// get all the revisions
				//returned = app.getCurrentConnection().retrieveAllRevisions(app);
				
				// get 50 revisions
				returned = app.getCurrentConnection().retrieveXRevisions(app, revisions);
				if(returned.length() == 0) {
					returned = getString(R.string.revisions_retrieved);
				}
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
    			//app.getCurrentConnection().createLogEntry(app, getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
    			
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
