/**
 * @author brian.gormanly
 * 
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

import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.valleytg.oasvnlite.android.R;
import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.model.Connection;
import com.valleytg.oasvnlite.android.ui.activity.ConnectionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionDetails extends Activity {
	
	/**
	 * Application layer
	 */
	OASVNApplication app;
	
	/**
	 * Controls
	 */
	TextView topAreaHeader;
	
	
	
	TextView status;
	
	TextView topArea1Title;
	TextView topArea2Title;
	TextView topArea3Title;
	TextView topArea4Title;
	TextView topArea5Title;
	
	TextView topArea1;
	TextView topArea2;
	TextView topArea3;
	TextView topArea4;
	TextView topArea5;
	
	Button btnCheckoutHead;
	Button btnCommit;
	//Button btnEdit;
	Button btnLog;
	Button btnRevisions;
	//Button btnFileManager;
	
	Button btnLocalBrowser;
	Button btnRemoteBrowser;
	
	/**
	 * Keep track of the menu so it can be updated
	 */
	Menu menu;
	
	/**
	 * Thread control
	 */
	Boolean running = false;
	
	/**
	 * Checkout / Update Button state
	 */
	Boolean checkoutButtonState = true;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_details);
        
        // get the application
        this.app = (OASVNApplication)getApplication();
        
        this.topAreaHeader = (TextView)findViewById(R.id.conndetail_top_header);
        this.status = (TextView)findViewById(R.id.conndetail_status);
    	
    	this.topArea1Title = (TextView)findViewById(R.id.conndetail_top1_title);
        this.topArea2Title = (TextView)findViewById(R.id.conndetail_top2_title);
        this.topArea3Title = (TextView)findViewById(R.id.conndetail_top3_title);
        this.topArea4Title = (TextView)findViewById(R.id.conndetail_top4_title);
        this.topArea5Title = (TextView)findViewById(R.id.conndetail_top5_title);
        
        this.topArea1 = (TextView)findViewById(R.id.conndetail_top1);
        this.topArea2 = (TextView)findViewById(R.id.conndetail_top2);
        this.topArea3 = (TextView)findViewById(R.id.conndetail_top3);
        this.topArea4 = (TextView)findViewById(R.id.conndetail_top4);
        this.topArea5 = (TextView)findViewById(R.id.conndetail_top5);
        
        // buttons
        btnCheckoutHead = (Button) findViewById(R.id.conndetail_full_checkout);
        btnCommit = (Button) findViewById(R.id.conndetail_full_commit);
        //btnEdit = (Button) findViewById(R.id.conndetail_edit);
        //btnFileManager = (Button) findViewById(R.id.conndetail_open_fm);
        btnLog = (Button) findViewById(R.id.conndetail_logs);
        btnRevisions = (Button) findViewById(R.id.conndetail_revisions);
        btnLocalBrowser = (Button) findViewById(R.id.conndetail_local_browse);
        btnRemoteBrowser = (Button) findViewById(R.id.conndetail_remote_browse);
        
        // populate the top
        populateTopInfo();
        
        // Make sure the device does not go to sleep while in this acitvity
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        this.btnCommit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// try to text the phone number
	        	try {
	                Intent callIntent = new Intent(ConnectionDetails.this, CommitActivity.class);
	                // set the current contact
	                //callIntent.putExtra("number", myList.get(position).getPhoneNumber());
	                ConnectionDetails.this.startActivity(callIntent);
	            } catch (ActivityNotFoundException e) {
	                e.printStackTrace();
	            }
			}
		});
        
        this.btnLog.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// open the add repository activity
				Intent intent = new Intent(ConnectionDetails.this, LogView.class);
				startActivity(intent);
				
			}
		});
        
        this.btnRevisions.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// open the add repository activity
				Intent intent = new Intent(ConnectionDetails.this, Revisions.class);
				startActivity(intent);
				
			}
		});
        
		this.btnLocalBrowser.setOnClickListener(new View.OnClickListener() {
					
			public void onClick(View v) {
				// try to text the phone number
				try
				{
					Intent callIntent = new Intent(ConnectionDetails.this, LocalBrowse.class);
					
					ConnectionDetails.this.startActivity(callIntent);
				}
				catch (ActivityNotFoundException e)
				{
					e.printStackTrace();
				}	
						
						
			}
		});
		
		this.btnRemoteBrowser.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// try to text the phone number
				try
				{
					Intent callIntent = new Intent(ConnectionDetails.this, ConnectionBrowse.class);
					
					ConnectionDetails.this.startActivity(callIntent);
				}
				catch (ActivityNotFoundException e)
				{
					e.printStackTrace();
				}
				
				
			}
		});

 

	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		// populate the top
        populateTopInfo();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// populate the top
        populateTopInfo();
	}
	
	public Dialog onCreatePublic(int id) {
		return onCreateDialog(id);
	}
	
	/*
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		SVNConflictChoice choice = SVNConflictChoice.MINE_FULL;
		
		switch (id) {
			case DIALOG_CHOOSE_ACTION:
				dialog = promptForConflictDialog();
				break;
				
			case CONFLICT_MINE_FULL:
				choice = SVNConflictChoice.MINE_FULL;
				break;
					
			case CONFLICT_THIERS_FULL:
				choice = SVNConflictChoice.THEIRS_FULL;
				break;
		
			case CONFLICT_BASE:
				choice = SVNConflictChoice.BASE;
				break;
				
			case CONFLICT_POSTPONE:
				choice = SVNConflictChoice.POSTPONE;
				break;
		}
		
		app.setConflictDecision(choice);

		return dialog;
	}
	*/
	


	@Override
	public void finish() {
		super.finish();
		
		app.setCurrentConnection(null);
	}

	public void resetIdle() {
		// set the status
		this.status.setText(R.string.idle);
	}

	/**
	 * Determines whether the lead button is in a checkout or update state
	 * @return true for update, false for checkout
	 */
	private Boolean determineCheckoutState() {
		Boolean state = true;
		
		// check to see if the repository is currently checked out.
		try {
			state = app.verifyWorkingCopy(app.assignPath());
		}
		catch(Exception e) {
			e.printStackTrace();
			state = false;
		}
		
		return state;
	}
	
	
	private void populateTopInfo() {
		
		// create the header area info
		if(this.app.getCurrentConnection() != null) {
			if(this.app.getCurrentConnection().getActive()) {
				// populate the top header
				this.topAreaHeader.setText(this.getString(R.string.connection) + getText(R.string.colon) + this.app.getCurrentConnection().getName());
				
				String url = "";
				try {
					url = this.app.getCurrentConnection().getTextURL().toString();
				}
				catch(Exception e) {
					url = getString(R.string.unknown);
				}
				
				String protocol = "";
				try {
					protocol = this.app.getCurrentConnection().getType().toString();
				}
				catch(Exception e) {
					protocol = getString(R.string.unknown);
				}
				
				String username = "";
				try {
					username = this.app.getCurrentConnection().getUsername().toString();
				}
				catch(Exception e) {
					username = getString(R.string.unknown);
				}
				
				String folder = "";
				try {
					folder = this.app.getCurrentConnection().getFolder().toString();
				}
				catch(Exception e) {
					folder = getString(R.string.unknown);
				}
				
				String head = "";
				try {
					head = this.app.getCurrentConnection().getHead().toString();
				}
				catch(Exception e) {
					head = getString(R.string.unknown);
				}
				
				// assign text to individual text areas
				this.topArea1Title.setText(this.getString(R.string.url) + this.getString(R.string.colon));
				this.topArea1.setText(url);
				
				this.topArea2Title.setText(this.getString(R.string.protocol) + this.getString(R.string.colon));
				this.topArea2.setText(protocol);
				
				this.topArea3Title.setText(this.getString(R.string.username) + this.getString(R.string.colon));
				this.topArea3.setText(username);
				
				this.topArea4Title.setText(this.getString(R.string.folder) + this.getString(R.string.colon));
				this.topArea4.setText(folder);
				
				this.topArea5Title.setText(this.getString(R.string.revision) + this.getString(R.string.colon));
				this.topArea5.setText(head);
				
				// set the checkout button text
		        if (determineCheckoutState()) {
		        	// update
		        	btnCheckoutHead.setText(R.string.update);
		        }
		        else {
		        	// checkout
		        	btnCheckoutHead.setText(R.string.checkout);	
		        }
		        
		        // assign the checkout / update button action
		        this.btnCheckoutHead.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						// open the add repository activity
						if(running == false) {

							// set the running flag
							ConnectionDetails.this.running = true;
							
							// set the checkout button text
					        if (determineCheckoutState()) {
					        	// update
								ConnectionDetails.this.status.setText(R.string.performing_update);
								
								UpdateThread updateThread = new UpdateThread();
								updateThread.execute();
					        }
					        else {
					        	// checkout
								ConnectionDetails.this.status.setText(R.string.performing_checkout);
								
								CheckoutThread checkoutThread = new CheckoutThread();
								checkoutThread.execute(SVNRevision.HEAD);
					        }
						}
						else {
							Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
						}
					}
				});
	
			}
			else {
				// no ticket was selected go back to ticket screen
				// tell the user we are going to work
	        	Toast toast=Toast.makeText(this, getString(R.string.no_connection_selected), 2500);
	    		toast.show();
	    		this.finish();
			}
			
			// check to see if the system is idle
			if(this.running) {
				ConnectionDetails.this.status.setText(R.string.performing_checkout);
			}
			else {
				ConnectionDetails.this.status.setText(R.string.idle);
			}
		}
		else {
			// no ticket was selected go back to ticket screen
			// tell the user we are going to work
        	Toast toast=Toast.makeText(this, getString(R.string.no_connection_selected), 2500);
    		toast.show();
    		this.finish();
		}
		
		// update the menu
		updateMenu();

	}
	
	/**
	 * Menu
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    // hold on to the menu
	    this.menu = menu;
	    
	    updateMenu();
	    return true;
	}
	
	private void updateMenu() {
		try {
			if(this.menu != null) {
				this.menu.clear();
				
				// options that should always be at the top of the menu
				
				// checkout
				this.menu.add(0, R.id.checkout, 0, R.string.checkout_revision);
				
			    
				// conditionally add checkout to the menu
				if(determineCheckoutState()) {
					// working copy exists, add any menu options dependent on having a working copy here
					
					// add the cleanup option
				    this.menu.add(0, R.id.cleanup, 2, R.string.cleanup);
				    
				    // add the revert action
				    this.menu.add(0, R.id.revert, 3, R.string.revert);
				    
				    // export the local working copy
				    this.menu.add(0, R.id.export_local, 4, R.string.export_local);
				    
				    // delete entire local working copy
					this.menu.add(0, R.id.delete_working_copy, 5, R.string.delete_folder);
				}
				else {
					// any options that can exist without a working copy go here.
					this.menu.add(0, R.id.export, 1, R.string.export);
				}
				
				// options that should always be at the bottom of the menu
				
				// edit connection
				this.menu.add(0, R.id.edit, 6, R.string.edit);

				// delete the connection (and possibly the entire working copy as well)
				this.menu.add(0, R.id.delete_connection, 7, R.string.delete_connection);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.checkout) {
			// open navigation
			// show the ticket detail screen
			try {
				// open the add repository activity
				if(running == false) {
					
					ConnectionDetails.this.status.setText(R.string.performing_checkout);
					
					promptForVersion(false);
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	    else if (item.getItemId() == R.id.edit) {

			try {
				// open the add repository activity
				Intent intent = new Intent(ConnectionDetails.this, AddRepository.class);
				startActivity(intent);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	    else if (item.getItemId() == R.id.export) {

			try {
				// open the add repository activity
				if(running == false) {
					
					ConnectionDetails.this.status.setText(R.string.performing_export);
					
					promptForVersion(true);
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	    else if (item.getItemId() == R.id.export_local) {

			try {
				// open the add repository activity
				if(running == false) {
					
					ConnectionDetails.this.status.setText(R.string.performing_export);
					promptForExportFolder();
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	    else if (item.getItemId() == R.id.cleanup) {
			// do cleanup
			try {
				// open the add repository activity
				if(running == false) {
					// set the running flag
					ConnectionDetails.this.running = true;
					
					ConnectionDetails.this.status.setText(R.string.performing_cleanup);
					
					CleanUpThread cleanUpThread = new CleanUpThread();
					cleanUpThread.execute();
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.revert) {
			// do revert
			try {
				// open the add repository activity
				if(running == false) {
					
					// double check the users intention
					AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
					
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle(R.string.confirm);
					builder.setMessage(getString(R.string.revert_confirmation));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			            public void onClick(DialogInterface dialog, int which) {
			            	synchronized (this) {
			            		try{
			            			// set the running flag
									ConnectionDetails.this.running = true;

									ConnectionDetails.this.status.setText(R.string.performing_revert);
									
									RevertThread revertThread = new RevertThread();
									revertThread.execute();
							        
							        // update the header
							        populateTopInfo();
							        
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
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.delete_working_copy) {
			// open navigation
			// show the ticket detail screen
			try {
				// open the add repository activity
				if(running	== false) {

					// double check the users intention
					AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
					
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle(R.string.confirm);
					builder.setMessage(getString(R.string.delete_message));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			            public void onClick(DialogInterface dialog, int which) {
			            	synchronized (this) {
			            		try{
			            			app.initializePath();
			            			File tree = app.assignPath();
			            			app.deleteRecursive(tree);
			            			
			            			// set the connection revision back to 0
							        app.getCurrentConnection().setHead(0);
							        app.saveConnection(app.getCurrentConnection());
							        
							        // update the header
							        populateTopInfo();
							        
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
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.delete_connection) {
			// delete the connection from oasvn
			try {
				// open the add repository activity
				if(running	== false) {

					// double check the users intention
					AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
					
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle(R.string.confirm);
					builder.setMessage(getString(R.string.delete_repo_message));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			            public void onClick(DialogInterface dialog, int which) {
			            	// if there is a local working copy then..
			            	// check to see if the user wants to delete the local folder as well
			            	// double check the users intention
			            	if(determineCheckoutState()) {
								AlertDialog.Builder builder2 = new AlertDialog.Builder(ConnectionDetails.this);
								
								builder2.setIcon(android.R.drawable.ic_dialog_alert);
								builder2.setTitle(R.string.confirm);
								builder2.setMessage(getString(R.string.delete_folder_too));
								builder2.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

						            public void onClick(DialogInterface dialog2, int which) {
						            	// user choose to delete the local folder
						            	synchronized (this) {
						            		try{
						            			app.initializePath();
						            			File tree = app.assignPath();
						            			app.deleteRecursive(tree);
						            			
						            			// close the activity
						            			ConnectionDetails.this.finish();
						            		} 
						            		catch(Exception e) {
						            			e.printStackTrace();
						            		}
						            	}
						            }
					            });
								builder2.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

						            public void onClick(DialogInterface dialog2, int which) {
						            	// close the activity
				            			ConnectionDetails.this.finish();
						            }
								});
						        builder2.show();	
			            	}
			            	
					        // remove the connection from the local database
			            	synchronized (this) {
			            		try{
			            			// remove from the database
			            			app.getCurrentConnection().deleteFromDatabase(app);
			            			
			            			// remove from the allConnections array
			            			app.getAllConnections().remove(app.getCurrentConnection());
			            			
			            		} 
			            		catch(Exception e) {
			            			e.printStackTrace();
			            		}
			            	}
			            	
			            	// close the activity only if there was no working copy
			            	if(!determineCheckoutState()) {
			            		ConnectionDetails.this.finish();
			            	}
			            }

			        });
			    builder.setNegativeButton(R.string.no, null);
			    builder.show();	
					
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Prompt the user with an Alert Dialog to ask the user which version
	 * they want to checkout or export
	 * @param isExport true for export, false for checkout
	 */
	private void promptForVersion(final Boolean isExport) {
		// prompt the user for the number of revisions to return
		AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
		
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(R.string.revision);
		builder.setMessage(getString(R.string.choose_revision));
		
		// add the number input to the dialog 
		final EditText input = new EditText(this);
		
		// populate the default value
		input.setText("0");
		
		// make the input type numeric
		input.setRawInputType(InputType.TYPE_CLASS_NUMBER);	
		
		// show the prompt
		builder.setView(input);
		
		builder.setPositiveButton(R.string.specified, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
            	synchronized (this) {
            		try{
            			
            	        
            	        // get the value entered and make sure it is valid
            	        String value = input.getText().toString();
            	        SVNRevision lValue = SVNRevision.HEAD;
            	        
            	        // try to convert the value to a long
            	        try {
            	        	lValue = SVNRevision.create(Long.parseLong(value));
            	        }
            	        catch(Exception e) {
            	        	lValue = SVNRevision.HEAD;
            	        	e.printStackTrace();
            	        }
            	        
            			// set the running flag
            			if(!ConnectionDetails.this.running) {
            				ConnectionDetails.this.running = true;
            				
            				// check to see if we are doing a export or checkout
            				if(isExport) {
            					ExportThread exportThread = new ExportThread();
            					exportThread.execute(lValue);
            				}
            				else {
            					CheckoutThread checkoutThread = new CheckoutThread();
            					checkoutThread.execute(lValue);
            				}
        					
            			}
            		} 
            		catch(Exception e) {
            			e.printStackTrace();
            		}
            	}	
            }
        });
		builder.setNegativeButton(R.string.head, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
            	synchronized (this) {
            		try{

            	        SVNRevision lValue = SVNRevision.HEAD;

            			// set the running flag
            			if(!ConnectionDetails.this.running) {
            				ConnectionDetails.this.running = true;
            				
            				// check to see if we are doing a export or checkout
            				if(isExport) {
            					ExportThread exportThread = new ExportThread();
            					exportThread.execute(lValue);
            				}
            				else {
            					CheckoutThread checkoutThread = new CheckoutThread();
            					checkoutThread.execute(lValue);
            				}
        					
            			}
            		} 
            		catch(Exception e) {
            			e.printStackTrace();
            		}
            	}	
            }
        });
		builder.show();	
	}
	
	/**
	 * Prompt the user with an Alert Dialog to ask for the new local folder to perform 
	 * the local export to
	 */
	private void promptForExportFolder() {
		// prompt the user for the number of revisions to return
		AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
		
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(R.string.export_local);
		builder.setMessage(getString(R.string.choose_export_folder));
		
		// add the number input to the dialog 
		final EditText input = new EditText(this);
		
		// populate the default value
		input.setText(app.getFullPathToMain().toString());
		
		// make the input type numeric
		input.setRawInputType(InputType.TYPE_CLASS_TEXT);	
		
		// show the prompt
		builder.setView(input);
		
		builder.setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
            	synchronized (this) {
            		try{
            			// initialize a flag that will allow a save at the end if all checks have passed
            			Boolean ready = false;
            	        
            	        // get the value entered and make sure it is valid
            	        final String value = input.getText().toString();
            	        
            	        // check to see if the user typed a folder name
            			if(value.length() == 0) {
            				Toast.makeText(ConnectionDetails.this, getString(R.string.folder_invalid), 5000).show();	
            				return;
            			}
            			
            			// check to see if the folder already exists in the database
            			Log.d("path considered", value);
            			final File file = new File(value);
            			
            			// check all existing connections
            			if(app.getAllConnections().size() > 0) {
            				for(Connection thisConnection2 : app.getAllConnections()) {
            					Log.d("check database for folder", thisConnection2.getFolder() + " - " + value);
            					if(thisConnection2.getFolder().equals(value)) {
            						// check to see if this is a new or existing connection
            						if(app.getCurrentConnection() != null) {
            							// existing connection, check to see if local database id's match (same entry can overwrite same folder name)
            							if(thisConnection2.getLocalDBId() == app.getCurrentConnection().getLocalDBId()) {
            								// ok
            							}
            							else {
            								// different connection, should not change folder name to existing one
            								Toast.makeText(ConnectionDetails.this, getString(R.string.folder_exists), 5000).show();	
            								return;
            							}
            						}
            						else {
            							// new connection, folder should not exist in the database
            							Toast.makeText(ConnectionDetails.this, getString(R.string.folder_exists), 5000).show();	
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
            				AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
            				
            				builder.setIcon(android.R.drawable.ic_dialog_alert);
            				builder.setTitle(R.string.confirm);
            				builder.setMessage(getString(R.string.local_folder_warning));
            				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            		            public void onClick(DialogInterface dialog, int which) {
            		            	// we made it do the save
            		            	// set the running flag
                        			if(!ConnectionDetails.this.running) {
                        				ConnectionDetails.this.running = true;
                        				
                        				System.out.println("about to run local export thread");
                        				// check to see if we are doing a export or checkout
                    					ExportLocalThread exportLocalThread = new ExportLocalThread();
                    					exportLocalThread.execute(file);

                    					
                        			}
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
            				app.createPath(file);
            			}

            			System.out.println("file : " + file.toString());
            			System.out.println("can write :" + file.canWrite());
            			// double check that we can write to the path and it is valid
            			if (!file.canWrite() ) {
            				Toast.makeText(ConnectionDetails.this, getString(R.string.directory_write_fail), 5000).show();	
            				return;
            			}
            			
            			if(ready) {
            				if(!ConnectionDetails.this.running) {
                				ConnectionDetails.this.running = true;
                				
                				System.out.println("about to run local export thread");
                				// check to see if we are doing a export or checkout
            					ExportLocalThread exportLocalThread = new ExportLocalThread();
            					exportLocalThread.execute(file);

            					
                			}
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
	
	/**
	 * Prompt user if there is a conflict and get the user preference to
	 * resolve the conflict
	 *
	public Dialog promptForConflictDialog() {
		final CharSequence[] items = { 
				getResources().getString(R.string.mine).toLowerCase(),
				getResources().getString(R.string.theirs).toLowerCase(),
				getResources().getString(R.string.base).toLowerCase(),
				getResources().getString(R.string.postpone).toLowerCase()
				};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_action);
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				switch (item)
				{
					case 0: // mine
						showDialog(CONFLICT_MINE_FULL);
						break;
						
					case 1: // thiers
						showDialog(CONFLICT_THIERS_FULL);
						break;
						
					case 2: // base
						showDialog(CONFLICT_BASE);
						break;
						
					case 3: // postpone
						showDialog(CONFLICT_POSTPONE);
						break;
				}
			}
		});
		
		return builder.create();
	}
	
	*/
	
	/**
	 * Threads
	 */

	class CheckoutThread extends AsyncTask<SVNRevision, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
	        dialog.setMessage(getString(R.string.in_progress));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	    }
		
		@Override
		protected String doInBackground(SVNRevision... revisions) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			// get the passed revision
			SVNRevision revision = revisions[0];
			
			String returned;
			
			try {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_checkout);

				     }
				});
				
				
				// do the checkout
				returned = app.doCheckout(revision);
				
				// get the current version
				app.getCurrentConnection().setHead((int)app.getRevisionNumber());
				
				// save the current connection
				app.getCurrentConnection().saveToLocalDB(app);

				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.checkout_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	/**
	 * Does export from the remote repository to the local folder specified
	 * in the connection.
	 * 
	 * @author brian.gormanly
	 * @param SVNRevision revision - the revision number of the remote repository
	 * that will be exported.
	 */
	class ExportThread extends AsyncTask<SVNRevision, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
	        dialog.setMessage(getString(R.string.in_progress));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	    }
		
		@Override
		protected String doInBackground(SVNRevision... revisions) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			// get the passed revision
			SVNRevision revision = revisions[0];
			
			String returned;
			
			try {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_export);

				     }
				});
				
				// do the checkout
				returned = app.doExport(revision);
				
				// get the current version
				//app.getCurrentConnection().setHead((int)app.getRevisionNumber());
				
				// save the current connection
				app.getCurrentConnection().saveToLocalDB(app);

			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.export_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	/**
	 * Thread that does an export from the local copy.  Will create a copy of
	 * the data in another local folder specified (and passed in).
	 * @author brian.gormanly
	 * @param String folder name - full path to the local folder where the local
	 * copy will be created without the working copy information.
	 */
	class ExportLocalThread extends AsyncTask<File, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
	        dialog.setMessage(getString(R.string.in_progress));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	    }
		
		@Override
		protected String doInBackground(File... folder) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			// get the passed revision
			File newFolder = folder[0];
			System.out.println("folder:" + newFolder.toString());
			String returned;
			
			try {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_export);

				     }
				});
				
				// do the checkout
				returned = app.doLocalExport(newFolder);
				
				// get the current version
				//app.getCurrentConnection().setHead((int)app.getRevisionNumber());
				
				// save the current connection
				//app.getCurrentConnection().saveToLocalDB(app);

			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.export_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	
	class UpdateThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
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
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_update);

				     }
				});
				
				
				// do the update
				returned = app.update(ConnectionDetails.this);
				
				// get the current version
				app.getCurrentConnection().setHead((int)app.getRevisionNumber());
				
				// save the current connection
				app.getCurrentConnection().saveToLocalDB(app);

				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.update_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	
	class CleanUpThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
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
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_cleanup);

				     }
				});
				
				
				// do the cleanup
				returned = app.cleanUp();

				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.cleanup_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	class RevertThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
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
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_revert);

				     }
				});
				
				
				// do the revert
				returned = app.revertToHead();

				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d(getString(R.string.alarm), getString(R.string.revert_successful));

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
	
	
}
