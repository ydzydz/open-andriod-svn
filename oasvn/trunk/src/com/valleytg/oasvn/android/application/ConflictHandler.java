package com.valleytg.oasvn.android.application;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNConflictResult;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.valleytg.oasvn.android.R;

public class ConflictHandler implements ISVNConflictHandler {
	
	// hold the app
	OASVNApplication app;
	
	// activity user was working in
	Activity activity;

	
	private static final int MY_NOTIFICATION_ID=1;
	private NotificationManager notificationManager;
	private Notification myNotification;
	private final String myBlog = "http://www.valleytg.com/";
	
	// constructor
	public ConflictHandler(OASVNApplication app, Activity activity) { 
		this.app = app;
		this.activity = activity;
	}
    
    public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
    	
    	// set the current conflict for the application
    	this.app.setCurrentConflict(conflictDescription);

    	// the following is going to be moved, likely to the new activity
        this.app.setConflictReason(conflictDescription.getConflictReason());
        this.app.setConflictFiles(conflictDescription.getMergeFiles());
        
        app.setConflictDecision(SVNConflictChoice.POSTPONE);

        if (this.app.getConflictReason() == SVNConflictReason.EDITED) {
        	// start the conflict activity

        	app.setConflictDecision(SVNConflictChoice.POSTPONE);

        }
        

	        
        // Send Notification
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) app.getApplicationContext().getSystemService(ns);
        
        myNotification = new Notification(R.drawable.oasvn_icon, this.activity.getString(R.string.conflict) + " " + this.activity.getString(R.string.detected), System.currentTimeMillis());
        Context context = app.getApplicationContext();
        String notificationTitle = this.activity.getString(R.string.app_name) + " " + this.activity.getString(R.string.conflict) + " " + this.activity.getString(R.string.detected);
        String notificationText = this.activity.getString(R.string.file) + this.activity.getString(R.string.colon) + " " + this.app.getConflictFiles().getWCFile() + "\n" 
        		+ R.string.reason + R.string.colon + " " + this.app.getConflictReason().getName() + "\n"
        		+ R.string.folder + R.string.colon + " " + this.app.getConflictFiles().getWCPath();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_EDIT);  
        
        
        String fileExtension= MimeTypeMap.getFileExtensionFromUrl(this.app.getConflictFiles().getWCPath());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        
        myIntent.setDataAndType(Uri.parse(this.app.getConflictFiles().getWCPath()), mimeType);
        
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        myNotification.defaults |= Notification.DEFAULT_SOUND;
        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        myNotification.setLatestEventInfo(context,
           notificationTitle,
           notificationText,
           pendingIntent);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

        System.out.println("Automatically resolving conflict for " + this.app.getConflictFiles().getWCFile() + 
                ", choosing " + SVNConflictChoice.POSTPONE.toString());
        return new SVNConflictResult(app.getConflictDecision(), this.app.getConflictFiles().getResultFile()); 
        
    }  
    
    
    public Dialog promptForConflictDialog() {


		
		final Dialog dialog = new Dialog(this.activity);
		
		Window window = dialog.getWindow(); 
	    WindowManager.LayoutParams lp = window.getAttributes();
	    lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
	    window.setAttributes(lp);
	    window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

	    
		dialog.setContentView(this.activity.getLayoutInflater().inflate(R.layout.conflict, null));
		dialog.setTitle(R.string.choose_action);
		
		//TextView text = (TextView) dialog.findViewById(R.id.conflict_top_header);
		//text.setText("Hello, this is a custom dialog!");
		
		Button btnTheirs;
		Button btnMine;
		Button btnBase;
		Button btnCancel;
		
		btnTheirs = (Button) dialog.findViewById(R.id.conflict_theirs);
        btnMine = (Button) dialog.findViewById(R.id.conflict_mine);
        btnBase = (Button) dialog.findViewById(R.id.conflict_base);
        btnCancel = (Button) dialog.findViewById(R.id.conflict_cancel);
		
		
        // button listeners     
        btnTheirs.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.THEIRS_FULL);
				dialog.dismiss();
			}
		});
        
        btnMine.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.MINE_FULL);
				dialog.dismiss();
			}
		});
        
        btnBase.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.BASE);
				dialog.dismiss();
			}
		});
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				app.setConflictDecision(SVNConflictChoice.POSTPONE);
				dialog.dismiss();
			}
		});
        
        return dialog;

	}
	

}