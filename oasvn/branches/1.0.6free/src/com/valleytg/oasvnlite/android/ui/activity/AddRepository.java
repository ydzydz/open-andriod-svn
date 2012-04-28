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

import android.app.*;
import android.content.DialogInterface;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.valleytg.oasvnlite.android.R;
import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.model.Connection;

public class AddRepository extends Activity {
	
	/**
	 * Application
	 */
	OASVNApplication app;

	Button btnSave;
	Button btnBack;
	EditText name;
	EditText url;
	EditText username;
	EditText password;
	EditText folder;
	
	/**
	 * Flag to track whether this is an edit so the original 
	 * folder name can remain.
	 */
	private Boolean isEdit;
	
	/**
	 * working connection
	 */
	Connection thisConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrepos);
	
		// get the application
        this.app = (OASVNApplication)getApplication();
		
		btnSave = (Button) findViewById(R.id.add_save);
        btnBack = (Button) findViewById(R.id.add_cancel);
        
        name = (EditText) findViewById(R.id.add_name);
		url = (EditText) findViewById(R.id.add_url);
		username = (EditText) findViewById(R.id.add_username);
		password = (EditText) findViewById(R.id.add_password);
		folder = (EditText) findViewById(R.id.add_folder);
        
        // check to see if we are editing a connection
        if(app.getCurrentConnection() != null) {
        	thisConnection = app.getCurrentConnection();
        	
        	name.setText(app.getCurrentConnection().getName());
        	url.setText(app.getCurrentConnection().getTextURL());
        	username.setText(app.getCurrentConnection().getUsername());
        	password.setText(app.getCurrentConnection().getPassword());
        	folder.setText(app.getCurrentConnection().getFolder());
        }
        else {
	        // initialize the connection
	        thisConnection = new Connection();
        }
        
        // button listeners     
        this.btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AddRepository.this.finish();
			}
		});
        
        this.btnSave.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// do the save
				AddRepository.this.checkRequired();
			}
		});
	}

	private void save() {
		
		// prep data for save
		thisConnection.setName(name.getText().toString());
		thisConnection.setUrl(url.getText().toString());
		thisConnection.setUsername(username.getText().toString());
		thisConnection.setPassword(password.getText().toString());
		thisConnection.setFolder(folder.getText().toString());
		
		app.saveConnection(thisConnection);

		// save to the database and in-memory array
		
		// close the activity
		this.finish();
	}
	
	private void checkRequired() {
		
		// initialize a flag that will allow a save at the end if all checks have passed
		Boolean ready = false;
				
		if(this.url.getText().toString().length() >= 7 && (this.url.getText().toString().substring(0, 4).toLowerCase().equals("http") 
				|| this.url.getText().toString().substring(0, 3).toLowerCase().equals("svn"))) {
			// url includes protocol
			
		}
		else {
			// invalid url
			Toast.makeText(this, getString(R.string.url_invalid), 5000).show();
			return;
		}
		if(this.name.getText().toString().length() == 0) {
			Toast.makeText(this, getString(R.string.name_invalid), 5000).show();	
			return;
		}
		
		// check to see if the user typed a folder name
		if(this.folder.getText().toString().length() == 0) {
			Toast.makeText(this, getString(R.string.folder_invalid), 5000).show();	
			return;
		}
		
		// check to see if the folder already exists in the database
		Log.d("path considered", app.getFullPathToMain().toString() + this.folder.getText().toString());
		File file = new File(app.getFullPathToMain().toString() + this.folder.getText().toString());
		
		// check all existing connection
		if(app.getAllConnections().size() > 0) {
			for(Connection thisConnection2 : app.getAllConnections()) {
				Log.d("check database for folder", thisConnection2.getFolder() + " - " + this.folder.getText().toString());
				if(thisConnection2.getFolder().equals(this.folder.getText().toString())) {
					// check to see if this is a new or existing connection
					if(this.app.getCurrentConnection() != null) {
						// existing connection, check to see if local database id's match (same entry can overwrite same folder name)
						if(thisConnection2.getLocalDBId() == this.app.getCurrentConnection().getLocalDBId()) {
							// ok
						}
						else {
							// different connection, should not change folder name to existing one
							Toast.makeText(this, getString(R.string.folder_exists), 5000).show();	
							return;
						}
					}
					else {
						// new connection, folder should not exist in the database
						Toast.makeText(this, getString(R.string.folder_exists), 5000).show();	
						return;
					}
				}
			}
		}
		else {
			ready = true;
		}
		
		// check to see if the file physically exists in storage and warn the user that there is already possibly data there
		Log.d("file test", Boolean.toString(file.exists()));
		Log.d("file test2", Boolean.toString(file.isDirectory()));
		if(file.exists()) {
			ready = false;
			AlertDialog.Builder builder = new AlertDialog.Builder(AddRepository.this);
			
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setTitle(R.string.confirm);
			builder.setMessage(getString(R.string.local_folder_warning));
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int which) {
	            	// we made it do the save
	        		AddRepository.this.save();
	        		return;
	            }

	        });
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int which) {
	            	return;
	            }

	        });

			builder.show();	
			return;
		}
		else {
			ready = true;
		}
		
		// made it to the end, save the connection
		if (ready) {
			this.save();
		}
		
	}
	
	
	
}
