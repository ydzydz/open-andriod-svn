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


package com.valleytg.oasvn.android.model;

import java.util.Collection;
import java.util.Date;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import android.database.Cursor;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.Repository.PROTOCOL_TYPE;
import com.valleytg.oasvn.android.util.DateUtil;

/**
 * Revision Model
 * @author brian.gormanly
 * @version Added 1.1 Stores revision information in the local database and increases
 * local management abilities so calls to the repository can be done less often and
 * more transparently.
 *
 */

public class Revision extends OASVNModelLocalDB {

	
	private String author;
	private Date commitDate;
	private String message;
	private Long revisionNumber;
	
	
	/**
     * Retrieves all of the directories for the current repository, from the root
     * @return ArrayList<SVNDirEntry> - Contains all directories as objects
     */
    public Collection<Repository> getAllRevisions(Repository repo, OASVNApplication app) {
    	
    	Collection<Repository> logEntries = null;
    	
    	long startRevision = 0;
    	long endRevision = -1; //HEAD (i.e. the latest) revision
    	
		try {
	    	logEntries = repo.repository.log( new String[] { "" }, null, startRevision, endRevision, true, true );
	    	
		} 
		catch(SVNException se) {
			String msg = se.getMessage();
			
			// log this failure
			repo.createLogEntry(app, app.getString(R.string.error), se.getMessage().substring(0, 19), se.getMessage().toString());
			
			se.printStackTrace();
		}
		catch(VerifyError ve) {
			String msg = ve.getMessage();
			
			// log this failure
			repo.createLogEntry(app, app.getString(R.string.error), ve.getMessage().substring(0, 19), ve.getMessage().toString());
			
			ve.printStackTrace();
		}
		catch(Exception e) {
			String msg = e.getMessage();
			
			// log this failure
			repo.createLogEntry(app, app.getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
			
			e.printStackTrace();
		}
		
    	return logEntries;
    	
    }
    
    /**
     * Retrieves the last (num) the revisions for the current repository, from the root
     * NOTE: not helping. will need another approach.
     * @param num = The number of revisions you want to recover.  Will retrieve the last
     * (num) revisions
     * @return ArrayList<SVNDirEntry> - Contains all directories as objects
     */
    public Collection<Repository> getXRevisions(Long num, Repository repo, OASVNApplication app) {

    	Collection<Repository> logEntries = null;

		try {
			
			// get the most recent revision
			long endRevision = repo.repository.getLatestRevision();
		
			long startRevision;
			// determine the start revision number
			if (endRevision - num >= 0) {
				startRevision = endRevision - num;
			}
			else {
				startRevision = 0;
			}

	    	logEntries = repo.repository.log( new String[] { "" }, null, startRevision, endRevision, true, true );
	    	
		} 
		catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(VerifyError ve) {
			String msg = ve.getMessage();
			
			// log this failure
			repo.createLogEntry(app, app.getString(R.string.error), ve.getMessage().substring(0, 19), ve.getMessage().toString());
			
			ve.printStackTrace();
		}
		catch(Exception e) {
			String msg = e.getMessage();
			
			// log this failure
			repo.createLogEntry(app, app.getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
			e.printStackTrace();
		}
		
    	return logEntries;
    	
    }
    
    /**
     * Gets the revision number.
     * @return integer value of the current checked out revision
     */
    public Integer getRevisionNumber(Repository repo, OASVNApplication app) {
    	
    	try {
    		// make sure there is a selected connection
        	if(repo != null) {
        		
        		Integer rev = (int) repo.repository.get.getStatusClient().doStatus(this.assignPath(), false).getRevision().getNumber();
        		
        		// log that the revision number was retrieved 
        		this.getCurrentConnection().createLogEntry(this, getString(R.string.revision), getString(R.string.rev_no) + getString(R.string.colon) 
        				+ " " + rev, getString(R.string.local_rev_updated) + getString(R.string.colon) + " " + rev);
        		
        		return rev;
        	}
        	else {
        		return 0;
        	}
			
    	}
    	catch(SVNException se) {
			String msg = se.getMessage();
			
			// log this failure
			this.getCurrentConnection().createLogEntry(this, getString(R.string.error), se.getMessage().substring(0, 19), se.getMessage().toString());
			
			return 0;
		}
		catch(VerifyError ve) {
			String msg = ve.getMessage();
			
			// log this failure
			this.getCurrentConnection().createLogEntry(this, getString(R.string.error), ve.getMessage().substring(0, 19), ve.getMessage().toString());
			
			ve.printStackTrace();
			return 0;
		}
		catch(Exception e) {
			String msg = e.getMessage();
			
			// log this failure
			this.getCurrentConnection().createLogEntry(this, getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
			
			e.printStackTrace();
			return 0;
		}

    }
	
	
	/**
	 * Save the instance data to the local database on the device
	 * @param app - reference to the singleton application
	 */
	@Override
	public void saveToLocalDB(OASVNApplication app) {
		values.put("author", this.getAuthor());
		values.put("commit_date", DateUtil.dateFormat.format(this.getCommitDate()));
		values.put("message", this.getMessage());
		values.put("revision_number", this.getRevisionNumber());
		
		super.saveToLocalDB(app);
	}
	
	/**
	 * Retrieve the data from the database and assign it to the proper members
	 */
	@Override
	public void setData(Cursor results) {
		try {
			this.setAuthor(results.getString(results.getColumnIndex("author")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(results.getString(results.getColumnIndex("commit_date")) != null) {
				this.setLastRemoteUpdate(DateUtil.toDate(results.getString(results.getColumnIndex("commit_date"))));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setMessage(results.getString(results.getColumnIndex("message")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setRevisionNumber(Long.parseLong(results.getString(results.getColumnIndex("revision_number"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.setData(results);
	}
	
	
	

	public void setRevision(SVNLogEntry revision) {
		this.revisionNumber = revision.getRevision();
		this.author = revision.getAuthor();
		this.commitDate = revision.getDate();
		this.message = revision.getMessage();
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getCommitDate() {
		return commitDate;
	}
	
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Long getRevisionNumber() {
		return revisionNumber;
	}
	
	public void setRevisionNumber(Long revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	
	
}
