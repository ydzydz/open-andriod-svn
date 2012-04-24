package com.valleytg.oasvn.android.ui.activity;

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
			this.revisiondetail_author_text = (TextView)findViewById(R.id.revisiondetail_date_text);
			this.revisiondetail_author = (TextView)findViewById(R.id.revisiondetail_date);
			this.revisiondetail_message_text = (TextView)findViewById(R.id.revisiondetail_message_text);
			this.revisiondetail_message = (TextView)findViewById(R.id.revisiondetail_message);
			this.btnBack = (Button) findViewById(R.id.revisiondetail_back);
			
			// set the values
			this.revisiondetail_num.setText(Long.toString(app.getCurrentRevision().getRevision()));
			this.revisiondetail_date.setText(DateUtil.getSimpleDateTime(app.getCurrentRevision().getDate(), this));
			this.revisiondetail_author.setText(app.getCurrentRevision().getAuthor());
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
