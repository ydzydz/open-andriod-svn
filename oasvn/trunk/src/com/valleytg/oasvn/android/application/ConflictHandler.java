package com.valleytg.oasvn.android.application;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNMergeFileSet;

public class ConflictHandler implements ISVNConflictHandler {
	
	// hold the app
	OASVNApplication app;
	
	// constructor
	public ConflictHandler(OASVNApplication app) { 
		this.app = app;
	}
    
    public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
    	
    	// set the current conflict for the application
    	this.app.setCurrentConflict(conflictDescription);
    	
    	// start the conflict activity
    	
    	
    	
    	// the following is going to be moved, likely to the new activity
        SVNConflictReason reason = conflictDescription.getConflictReason();
        SVNMergeFileSet mergeFiles = conflictDescription.getMergeFiles();
        
        SVNConflictChoice choice = SVNConflictChoice.THEIRS_FULL;
        if (reason == SVNConflictReason.EDITED) {
            //If the reason why conflict occurred is local edits, chose local version of the file
            //Otherwise the repository version of the file will be chosen.
            choice = SVNConflictChoice.MINE_FULL;
        }
        System.out.println("Automatically resolving conflict for " + mergeFiles.getWCFile() + 
                ", choosing " + (choice == SVNConflictChoice.MINE_FULL ? "local file" : "repository file"));
        return new SVNConflictResult(choice, mergeFiles.getResultFile()); 
        
        
    }   

}