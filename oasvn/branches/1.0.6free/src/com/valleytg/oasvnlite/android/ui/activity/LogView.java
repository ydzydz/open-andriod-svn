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

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import com.valleytg.oasvnlite.android.R;
import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.model.Connection;
import com.valleytg.oasvnlite.android.model.LogItem;
import com.valleytg.oasvnlite.android.util.DateUtil;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class LogView extends ListActivity {
	
	OASVNApplication app;
	Button btnDelete;
	Button btnBack;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        
        try {
	        // get the application
	        this.app = (OASVNApplication)getApplication();
	        
	        // initialize the buttons
	        btnDelete = (Button) findViewById(R.id.log_delete);
	        btnBack = (Button) findViewById(R.id.log_back);
	        
	        // set the list adapter
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.log_item));
	        
	        // populate the list
	        populateList();
	        
	        // button listeners 
	        this.btnDelete.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
	
					// double check the users intention
					AlertDialog.Builder builder = new AlertDialog.Builder(LogView.this);
					
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle(R.string.confirm);
					builder.setMessage(getString(R.string.delete_all_logs_warning));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			            public void onClick(DialogInterface dialog, int which) {
			            	synchronized (this) {
			            		try{
			            			// do the removal
			            			app.getCurrentConnection().removeLogEntrys(app);
			            			
		    				        // update the activity
		    				        populateList();
		    				        
			            		} 
			            		catch(Exception e) {
			            			e.printStackTrace();
			            		}
			            	}	
			            }
			        });
					builder.setNegativeButton(R.string.no, null);
					builder.show();	

				}
			});

	        this.btnBack.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					LogView.this.finish();
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
			final LogItem thisLog = app.getCurrentConnection().getLogs().get(position);
			
			// set the current log
			app.setCurrentLog(thisLog);
			
			// go to the log details screen
			Intent intent = new Intent(LogView.this, LogDetails.class);
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
        String[] logs;
        logs = null;
 
        // try to retrieve the local staff / users
        try {
        	
        	// check to see that there is a current connection
        	if(this.app.getCurrentConnection() != null) {
        	
	        	// do the retrieval
	        	app.getCurrentConnection().retrieveAllLogs(app);
	        	
	        	// check to see if there are any
	        	if(app.getCurrentConnection().getLogs().size() > 0) {
	        		// sort the fist by staff first name
	        		Collections.sort(app.getCurrentConnection().getLogs(), new Comparator<LogItem>(){
	
						public int compare(LogItem lhs, LogItem rhs) {
							LogItem p1 = (LogItem) lhs;
	    	                LogItem p2 = (LogItem) rhs;
	    	               return p2.getDateCreated().compareTo(p1.getDateCreated());
						}
	    	 
	    	        });
	        		
	        		// connections ready to go
	        		logs = new String[app.getCurrentConnection().getLogs().size()];
	        		for(LogItem log : app.getCurrentConnection().getLogs()) {
	            		logs[app.getCurrentConnection().getLogs().indexOf(log)] = log.getLogNumber() + " | " + DateUtil.getSimpleDateTime(log.getDateCreated(), this) 
	            		+ "\nType: " + log.getShortMessage();
	            	}
	        		
	        		
	        	}
	        	else {
	        		// no users in the local db
	        		logs = new String[1];
	        		logs[0]= getString(R.string.no_logs);
	        		Toast toast=Toast.makeText(this, getString(R.string.no_logs), 1500);
	        		toast.show();
	        	}
        	}
        	
        	setListAdapter(new ArrayAdapter<String>(this, R.layout.log_item, logs));

    		ListView lv = getListView();
    		lv.setTextFilterEnabled(true);
        	
        }
        catch(Exception e) {
        	// no user/staff data in application yet. call for a refresh
        	logs = new String[1];
        	logs[0] = getString(R.string.no_logs);
        }
        
        

    }
	
}
