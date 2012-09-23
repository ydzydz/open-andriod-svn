package com.valleytg.oasvn.android.ui.activity;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictResult;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConflictActivity extends Activity {

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
	
	TextView topArea1;
	TextView topArea2;
	TextView topArea3;
	
	Button btnTheirs;
	Button btnMine;
	Button btnCancel;
	
	Activity passingActivity;
	
	public ConflictActivity(Activity activity) {
		passingActivity = activity;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conflict);
        
        // get the application
        this.app = (OASVNApplication)getApplication();
        
        this.topAreaHeader = (TextView)findViewById(R.id.conflict_top_header);
    	/*
    	this.topArea1Title = (TextView)findViewById(R.id.conflict_top1_title);
        this.topArea2Title = (TextView)findViewById(R.id.conflict_top2_title);
        this.topArea3Title = (TextView)findViewById(R.id.conflict_top3_title);
        
        this.topArea1 = (TextView)findViewById(R.id.conflict_top1);
        this.topArea2 = (TextView)findViewById(R.id.conflict_top2);
        this.topArea3 = (TextView)findViewById(R.id.conflict_top3);
        */
        
        // buttons
        btnTheirs = (Button) findViewById(R.id.conflict_theirs);
        btnMine = (Button) findViewById(R.id.conflict_mine);
        btnCancel = (Button) findViewById(R.id.conflict_cancel);
        
        // button listeners     
        this.btnTheirs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.THEIRS_FULL);
				ConflictActivity.this.finish();
			}
		});
        
        this.btnMine.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.MINE_FULL);
				ConflictActivity.this.finish();
			}
		});
        
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.POSTPONE);
				ConflictActivity.this.finish();
			}
		});
	}

}
