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

import java.util.Date;

import org.tmatesoft.svn.core.SVNLogEntry;

import android.database.Cursor;

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
