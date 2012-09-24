/**
 * @author brian.gormanly
 * @author Sascha Zieger
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

package com.valleytg.oasvn.android.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class LocalBrowse extends ListActivity implements Runnable, OnItemLongClickListener {
	private static final int DIALOG_WAIT_LOADING = 1;
	private static final int DIALOG_WAIT_EXPORT = 2;
	private static final int DIALOG_CHOSE_ACTION_DIR = 3;
	private static final int DIALOG_CHOSE_ACTION_FILE = 4;
	private static final int DIALOG_EXPORT = 5;
	private static final int DIALOG_COPY = 6;
	private static final int DIALOG_MOVE = 7;
	private static final int DIALOG_WAIT_MOVE = 8;
	
	private static final int DIALOG_CONFLICT_POSTPONE = 10;
	private static final int DIALOG_CONFLICT_MINE = 11;
	private static final int DIALOG_CONFLICT_THEIRS = 12;
	private static final int DIALOG_CONFLICT_BASE = 13;
	private static final int DIALOG_CONFLICT_MERGED = 14;
	
	private Context mContext;
	private OASVNApplication mApp;
	
	private ProgressDialog mLoadingDialog;
	private BrowseAdapter mAdapter;
	private int mLoadingDialogType;
	
	private List<File> mDirs;
	private List<List<File>> mDirCache;
	private boolean mDirCacheInit = false;
	private String mCurDir = "";
	private int mLastDialogElem;
	private String mLastExportPath;
	private SVNRevision mCurRevision = SVNRevision.HEAD;
	private String mExportMsg;
	
	private File entry;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		
		switch (id) {
			case DIALOG_CHOSE_ACTION_DIR:
			case DIALOG_CHOSE_ACTION_FILE:
				dialog = createChooseActionDialog();
				break;
		
			case DIALOG_COPY:
				dialog = createCopyDialog();
				break;
				
			case DIALOG_MOVE:
				dialog = createMoveDialog();
		}
		mLoadingDialogType = id;
		
		return dialog;
	}
	
	private Dialog createChooseActionDialog() {
		
		// is there a conflict?
		SVNStatus status = mApp.isFileInConflict(entry);
		CharSequence[] items;
		
		String temp = "";

		if(status != null && status.getContentsStatus() == SVNStatusType.STATUS_CONFLICTED) {
			items = new CharSequence[8];
			items[0] = getResources().getString(R.string.copy).toLowerCase();
			items[1] = getResources().getString(R.string.move).toLowerCase();
			items[2] = getResources().getString(R.string.edit).toLowerCase();
			items[3] = getResources().getString(R.string.conflict).toLowerCase() + " " + getResources().getString(R.string.mine).toLowerCase();
			items[4] = getResources().getString(R.string.conflict).toLowerCase() + " " + getResources().getString(R.string.theirs).toLowerCase();
			items[5] = getResources().getString(R.string.conflict).toLowerCase() + " " + getResources().getString(R.string.base).toLowerCase();
			items[6] = getResources().getString(R.string.conflict).toLowerCase() + " " + getResources().getString(R.string.merge).toLowerCase();
			items[7] = getResources().getString(R.string.conflict).toLowerCase() + " " + getResources().getString(R.string.postpone).toLowerCase();

			
		}
		else {
			items = new CharSequence[3];
			items[0] = getResources().getString(R.string.copy).toLowerCase();
			items[1] = getResources().getString(R.string.move).toLowerCase();
			items[2] = getResources().getString(R.string.edit).toLowerCase();
		}
		

		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_action);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
					case 0: // copy
						showDialog(DIALOG_COPY);
						break;
						
					case 1: // move
						showDialog(DIALOG_MOVE);
						break;
						
					case 2: // edit
						Intent myIntent = new Intent();
				        myIntent.setAction(Intent.ACTION_EDIT);  
				        
				        Uri selectedUri = Uri.fromFile(entry);
				        String fileExtension= MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
				        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
				        
				        myIntent.setDataAndType(Uri.parse(entry.getPath()), mimeType);
				        startActivity(myIntent);
						break;
						
					case 3: // mine
						mApp.resolveConflict(new File(entry.getPath()), SVNDepth.INFINITY, SVNConflictChoice.MINE_FULL);
						break;
						
					case 4: // theirs
						mApp.resolveConflict(new File(entry.getPath()), SVNDepth.INFINITY, SVNConflictChoice.THEIRS_FULL);
						break;
						
					case 5: // base
						mApp.resolveConflict(new File(entry.getPath()), SVNDepth.INFINITY, SVNConflictChoice.BASE);
						break;
						
					case 6: // merge
						mApp.resolveConflict(new File(entry.getPath()), SVNDepth.INFINITY, SVNConflictChoice.MERGED);
						break;
						
					case 7: // postpone
						mApp.resolveConflict(new File(entry.getPath()), SVNDepth.INFINITY, SVNConflictChoice.POSTPONE);
						break;	
						
				}
				
				StatusThread statusThread = new StatusThread();
				statusThread.execute();
			}
		});
		
		return builder.create();
	}
	
	protected Dialog createCopyDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.connection_browse_copy_dialog, (ViewGroup) findViewById(R.id.connbrowse_copy_dialog_layout_root));
		
		final EditText path = (EditText) layout.findViewById(R.id.connbrowse_copy_dialog_path_edit);
		
		path.setText(mApp.getRootPath().toString() + "/", TextView.BufferType.EDITABLE);
		
		Button save = (Button) layout.findViewById(R.id.connbrowse_copy_dialog_save_btn);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				mLastExportPath = path.getText().toString();
				dismissDialog(DIALOG_COPY);
				startExport();
			}
		});
		
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		
		return builder.create();
	}
	
	protected Dialog createMoveDialog() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.connection_browse_move_dialog, (ViewGroup) findViewById(R.id.connbrowse_move_dialog_layout_root));
		
		final EditText path = (EditText) layout.findViewById(R.id.connbrowse_move_dialog_path_edit);
		
		path.setText(mApp.getRootPath().toString() + "/", TextView.BufferType.EDITABLE);
		
		Button save = (Button) layout.findViewById(R.id.connbrowse_move_dialog_save_btn);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				mLastExportPath = path.getText().toString();
				dismissDialog(DIALOG_MOVE);
				startMove();
			}
		});
		
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		
		return builder.create();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection_browse);
		
		try {
			mContext = this;
			mApp = (OASVNApplication) getApplication();
			mDirCache = new ArrayList<List<File>>();
			mCurDir = mApp.getCurrentConnection().getFolder().toString() + "/";
			
			StatusThread statusThread = new StatusThread();
			statusThread.execute();
			
			updateDataAndList();
			
			getListView().setOnItemLongClickListener(this);
		}
		catch(Exception e) {
			this.finish();
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		entry = mDirs.get(position);
		if (entry.isDirectory()) {
			mCurDir = mCurDir + entry.getName() + "/";
			System.out.println("new dir:" + mCurDir);
			updateDataAndList();
		}
		else if (entry.isFile()) {
			mLastDialogElem = position;
			showDialog(DIALOG_CHOSE_ACTION_FILE);
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
	
	public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
		entry = mDirs.get(position);
		
		if (entry.isDirectory()) {
			mLastDialogElem = position;
			showDialog(DIALOG_CHOSE_ACTION_DIR);
		}
		else if (entry.isFile()) {
			mLastDialogElem = position;
			showDialog(DIALOG_CHOSE_ACTION_FILE);
		}
		
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if (mCurDir.compareTo("") == 0)
			super.onBackPressed();
		else
		{
			do {
				mCurDir = mCurDir.substring(0, mCurDir.length() - 1);
			}
			while (mCurDir.endsWith("/") == false && mCurDir.compareTo("") != 0);
		
			try {
				mDirs = mDirCache.remove(mDirCache.size() - 1);
			}
			catch (Exception e) {
				this.finish();
			}
			updateList();
		}
	}
	
	private void startExport() {
		mLoadingDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.performing_export), true, false);
		
		mLoadingDialogType = DIALOG_WAIT_EXPORT;
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private void startMove() {
		mLoadingDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.performing_export), true, false);
		
		mLoadingDialogType = DIALOG_WAIT_MOVE;
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private String exportSingleElement() {
		String sdPath = mLastExportPath;
		File file = mDirs.get(mLastDialogElem);
		
		if (sdPath.length() > 0 && sdPath.endsWith("/") == false)
			sdPath += "/";
		
		if (sdPath.endsWith(file.toString()) == false)
			sdPath += file.getName();
		
		return mApp.doLocalCopy(file, new File(sdPath));
	}
	
	private String moveSingleElement() {
		String sdPath = mLastExportPath;
		File file = mDirs.get(mLastDialogElem);
		
		if (sdPath.length() > 0 && sdPath.endsWith("/") == false)
			sdPath += "/";
		
		if (sdPath.endsWith(file.toString()) == false)
			sdPath += file.getName();
		
		return mApp.doLocalMove(file, new File(sdPath));
	}
	
	private void updateDataAndList() {
		mLoadingDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading), true, false);
		mLoadingDialogType = DIALOG_WAIT_LOADING;
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@SuppressWarnings("unchecked")
	private void updateData() {
		if (mDirCacheInit)
			mDirCache.add(mDirs);
		else
			mDirCacheInit = true;
		
		mDirs = new ArrayList<File>();
		
		File f = new File(mCurDir);
		File[] files = f.listFiles();
  
		if(files != null) {
			if(files.length > 0) {
				for(int i=0; i < files.length; i++) {
		
					File file = files[i];
					mDirs.add(file);
				}
			}
			else {
				mDirs.add(new File("- " + getResources().getString(R.string.empty) + " -"));
			}
		}
		
	}
	
	private void updateList() {
		mAdapter = new BrowseAdapter(mContext, mDirs);
		setListAdapter(mAdapter);
		
		switch (mLoadingDialogType)
		{
			case DIALOG_WAIT_EXPORT:
				if (mExportMsg.equals("success"))
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.export_successful)
									+ ": "+ mDirs.get(mLastDialogElem).getName(), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), mExportMsg,
							Toast.LENGTH_SHORT).show();
		
				break;
		}
		
		if(mDirs.size() == 0) {
			// no files to display
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.folder_invalid), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void run() {
		switch (mLoadingDialogType) {
			case DIALOG_WAIT_LOADING:
				updateData();
				break;
			case DIALOG_WAIT_EXPORT:
				mExportMsg = exportSingleElement();
				break;
			case DIALOG_WAIT_MOVE:
				mExportMsg = moveSingleElement();
				break;
		}
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			mLoadingDialog.dismiss();
			
			updateList();
			
			mLoadingDialogType = -1;
		}
	};
	
	private class BrowseAdapter extends ArrayAdapter<File> {
		public BrowseAdapter(Context context, List<File> dirNames) {
			super(context, R.layout.connection_browse_listitem, dirNames);
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.connection_browse_listitem, parent, false);
		
			TextView kind = (TextView) row.findViewById(R.id.connbrowse_listitem_kind);
			ImageView iButton = (ImageView) row.findViewById(R.id.connbrowse_listitem_image);
			TextView name = (TextView) row.findViewById(R.id.connbrowse_listitem_name);
		
			entry = mDirs.get(position);
			//iButton.setImageResource(R.drawable.warning);
		
			if (entry.isFile())
				kind.setVisibility(View.INVISIBLE);
			
			// is there a conflict?
			SVNStatus status = mApp.isFileInConflict(entry);
			
			String temp = "";
			if(status != null) {
				if(status.getContentsStatus() != null) {
					iButton.setVisibility(View.VISIBLE);
					temp = entry.getName() + "  --->  " + status.getContentsStatus();
				}
				else {
					temp = entry.getName();
				}
			}
			else {
				temp = entry.getName();
			}
		
			name.setText(temp);
		
			return row;
		}
	}
	
	/**
	 * Threads
	 */
	
	class StatusThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(LocalBrowse.this);
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
				    	 //LocalBrowse.this.status.setText(R.string.performing_revert);

				     }
				});
				
				
				// do the revert
				returned = mApp.showStatus(mApp.assignPath(), true , true , false , true , false );

				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			//LocalBrowse.this.resetIdle();

			//android.util.Log.d(getString(R.string.alarm), getString(R.string.status_successful));

	        dialog.dismiss();
	        
	        /*
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        */
	        
	        // populate the top
	        //populateTopInfo();
	        
	        //ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			//ConnectionDetails.this.running = false;
	    }
	}

}
