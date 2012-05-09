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

import java.io.File;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.ui.activity.ConnectionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
	Button btnEdit;
	Button btnLog;
	Button btnRevisions;
	Button btnFileManager;
	
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
        btnEdit = (Button) findViewById(R.id.conndetail_edit);
        btnFileManager = (Button) findViewById(R.id.conndetail_open_fm);
        btnLog = (Button) findViewById(R.id.conndetail_logs);
        btnRevisions = (Button) findViewById(R.id.conndetail_revisions);
        
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

        this.btnEdit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// open the add repository activity
				Intent intent = new Intent(ConnectionDetails.this, AddRepository.class);
				startActivity(intent);
				
			}
		});
        
        this.btnFileManager.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Warn the user that not all file browser support landing in the correct directory
				Toast.makeText(ConnectionDetails.this, getString(R.string.file_manager_warning), 6000).show();
				
	            Intent intent = new Intent();  
	            intent.addCategory(Intent.CATEGORY_OPENABLE);
	            intent.setAction(Intent.ACTION_GET_CONTENT);  
	            intent.setDataAndType(Uri.parse(app.assignPath().toString()), "*/*");
	            startActivity(intent);  
				
			}
		});

	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		// populate the top
        populateTopInfo();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// populate the top
        populateTopInfo();
	}
	
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
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
					folder = app.getFullPathToMain().toString() + this.app.getCurrentConnection().getFolder().toString();
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
				
				this.topArea5Title.setText(this.getString(R.string.head) + this.getString(R.string.colon));
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
								checkoutThread.execute();
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
				this.menu.add(0, R.id.checkout, 0, R.string.checkout);
			    
				// conditionally add checkout to the menu
				if(determineCheckoutState()) {
					// working copy exists, add any menu options dependant on having a working copy here
					
					// add the cleanup option
				    this.menu.add(0, R.id.cleanup, 1, R.string.cleanup);
				    
				    // add the revert action
				    this.menu.add(0, R.id.revert, 1, R.string.revert);
				    
				    // delete entire local working copy
					this.menu.add(0, R.id.delete_working_copy, 0, R.string.delete_folder);
				}
				else {
					// any options that can exist without a working copy go here.
					
				}
				
				// options that should always be at the bottom of the menu

				// delete the connection (and possibly the entire working copy as well)
				this.menu.add(0, R.id.delete_connection, 0, R.string.delete_connection);
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
					// set the running flag
					ConnectionDetails.this.running = true;
					
					ConnectionDetails.this.status.setText(R.string.performing_checkout);
					
					CheckoutThread checkoutThread = new CheckoutThread();
					checkoutThread.execute();
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.cleanup) {
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
	 * Threads
	 */

	class CheckoutThread extends AsyncTask<Void, Void, String> {

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
				    	 ConnectionDetails.this.status.setText(R.string.performing_checkout);

				     }
				});
				
				
				// do the checkout
				returned = app.fullHeadCheckout();
				
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
				returned = app.update();
				
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
