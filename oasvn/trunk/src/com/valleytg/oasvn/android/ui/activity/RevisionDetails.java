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

import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntryPath;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.util.DateUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RevisionDetails extends Activity {
	/**
	 * Application layer
	 */
	OASVNApplication app;
	
	/**
	 * Controls
	 */
	TextView topAreaHeader;
	TextView revisiondetail_num_text;
	TextView revisiondetail_num;
	TextView revisiondetail_date_text;
	TextView revisiondetail_date;
	TextView revisiondetail_author_text;
	TextView revisiondetail_author;
	TextView revisiondetail_files_text;
	TextView revisiondetail_files;
	TextView revisiondetail_message_text;
	TextView revisiondetail_message;
	Button btnBack;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.revision_details);

		try {
			// get the application
			this.app = (OASVNApplication)getApplication();
			
			// initialize the controls
			this.topAreaHeader = (TextView)findViewById(R.id.revisiondetail_top_header);
			this.revisiondetail_num_text = (TextView)findViewById(R.id.revisiondetail_num_text);
			this.revisiondetail_num = (TextView)findViewById(R.id.revisiondetail_num);
			this.revisiondetail_date_text = (TextView)findViewById(R.id.revisiondetail_date_text);
			this.revisiondetail_date = (TextView)findViewById(R.id.revisiondetail_date);
			this.revisiondetail_author_text = (TextView)findViewById(R.id.revisiondetail_author_text);
			this.revisiondetail_author = (TextView)findViewById(R.id.revisiondetail_author);
			this.revisiondetail_files_text = (TextView)findViewById(R.id.revisiondetail_files_text);
			this.revisiondetail_files = (TextView)findViewById(R.id.revisiondetail_files);
			this.revisiondetail_message_text = (TextView)findViewById(R.id.revisiondetail_message_text);
			this.revisiondetail_message = (TextView)findViewById(R.id.revisiondetail_message);
			this.btnBack = (Button) findViewById(R.id.revisiondetail_back);
			
			// get the file path details
			String paths = "";
			if ( app.getCurrentRevision().getChangedPaths().size() > 0 ) {
				Set changedPathsSet = app.getCurrentRevision().getChangedPaths().keySet();
				
				for ( Iterator changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
					SVNLogEntryPath entryPath = ( SVNLogEntryPath ) app.getCurrentRevision().getChangedPaths().get(changedPaths.next());
					paths += entryPath.getType( ) + " " + entryPath.getPath( ) + ( ( entryPath.getCopyPath( ) != null ) ? " (from " 
							+ entryPath.getCopyPath( ) + " revision " + entryPath.getCopyRevision( ) + ")" : "" ) + "\n";
				}
				
			
			}
			
			// set the values
			this.revisiondetail_num.setText(Long.toString(app.getCurrentRevision().getRevision()));
			this.revisiondetail_date.setText(DateUtil.getSimpleDateTime(app.getCurrentRevision().getDate(), this));
			this.revisiondetail_author.setText(app.getCurrentRevision().getAuthor());
			this.revisiondetail_files.setText(paths);
			this.revisiondetail_message.setText(app.getCurrentRevision().getMessage());
			
			// button listeners     
	        this.btnBack.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					RevisionDetails.this.finish();
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
				
		}
		catch (Exception e) {
			// problem loading activity
			this.finish();
			e.printStackTrace();
		 }
	}
	 
	@Override
	protected void onResume() {
		try {
			super.onResume();

		}
		catch (Exception e) {
			// problem loading activity
			this.finish();
			e.printStackTrace();
		}
	}
}
