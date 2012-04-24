package com.valleytg.oasvn.android.ui.activity;

import java.util.Collections;
import java.util.Comparator;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.LogItem;
import com.valleytg.oasvn.android.util.DateUtil;

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

public class Versions extends ListActivity {
	OASVNApplication app;
	Button btnBack;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.versions);
        
        try {
	        // get the application
	        this.app = (OASVNApplication)getApplication();
	        
	        // initialize the buttons
	        btnBack = (Button) findViewById(R.id.versions_back);
	        
	        // set the list adapter
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.version_item));
	        
	        // populate the list
	        populateList();

	        this.btnBack.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Versions.this.finish();
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
			//Intent intent = new Intent(Versions.this, VersionDetails.class);
			//startActivity(intent);
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
        	
        	
        }
        catch(Exception e) {
        	// no user/staff data in application yet. call for a refresh
        	logs = new String[1];
        	logs[0] = getString(R.string.no_logs);
        }
        
        setListAdapter(new ArrayAdapter<String>(this, R.layout.log_item, logs));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

    }
}
