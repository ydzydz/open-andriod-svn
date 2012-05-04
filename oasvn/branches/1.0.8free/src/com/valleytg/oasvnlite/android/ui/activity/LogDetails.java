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

import com.valleytg.oasvnlite.android.R;
import com.valleytg.oasvnlite.android.application.OASVNApplication;
import com.valleytg.oasvnlite.android.util.DateUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LogDetails extends Activity {
	/**
	 * Application layer
	 */
	OASVNApplication app;
	
	/**
	 * Controls
	 */
	TextView topAreaHeader;
	TextView logdetail_type_text;
	TextView logdetail_type;
	TextView logdetail_date_text;
	TextView logdetail_date;
	TextView logdetail_message_text;
	TextView logdetail_message;
	Button btnDelete;
	Button btnBack;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_details);

		try {
			// get the application
			this.app = (OASVNApplication)getApplication();
			
			// initialize the controls
			this.topAreaHeader = (TextView)findViewById(R.id.logdetail_top_header);
			this.logdetail_type_text = (TextView)findViewById(R.id.logdetail_type_text);
			this.logdetail_type = (TextView)findViewById(R.id.logdetail_type);
			this.logdetail_date_text = (TextView)findViewById(R.id.logdetail_date_text);
			this.logdetail_date = (TextView)findViewById(R.id.logdetail_date);
			this.logdetail_message_text = (TextView)findViewById(R.id.logdetail_message_text);
			this.logdetail_message = (TextView)findViewById(R.id.logdetail_message);
			this.btnBack = (Button) findViewById(R.id.logdetail_back);
			this.btnDelete = (Button) findViewById(R.id.logdetail_delete);
			
			// set the values
			this.logdetail_type.setText(app.getCurrentLog().getLogNumber());
			this.logdetail_date.setText(DateUtil.getSimpleDateTime(app.getCurrentLog().getDateCreated(), this));
			this.logdetail_message.setText(app.getCurrentLog().getMessage());
			
			// button listeners     
	        this.btnBack.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					LogDetails.this.finish();
				}
			});
	        
	        this.btnDelete.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// call the connections removeLogEntrys method
					app.getCurrentConnection().removeLogEntry(app, app.getCurrentLog().getLocalDBId());
					
					// close the activity
					LogDetails.this.finish();
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
