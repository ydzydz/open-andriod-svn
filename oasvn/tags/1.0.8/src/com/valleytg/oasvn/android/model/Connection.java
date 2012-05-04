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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;

import android.content.ContentValues;
import android.database.Cursor;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.util.DateUtil;

public class Connection extends OASVNModelLocalDB {

	/**
	 * Setup a the protocol type enum for the supported protocols
	 * @author brian.gormanly
	 *
	 */
	public enum PROTOCOL_TYPE  
	{  
	    HTTP("HTTP"),  
	    HTTPS("HTTPS"),  
	    SVN("SVN"), 
	    SVNSSH("SVN+SSH");
	  
	    private final String label;  
	  
	    private PROTOCOL_TYPE(String label) { this.label = label; }  
	  
	    @Override  
	    public String toString() { return label; }  
	}
	
	// members saved in the database
	private String name = "";
	private String textURL = "";
	private SVNURL repositoryURL;
	private PROTOCOL_TYPE type;
	private BasicAuthenticationManager authManager;
	private String username ="";
	private String password = "";
	private String folder = "";
	private Integer head = 0;
	
	private ArrayList<LogItem> logs = new ArrayList<LogItem>();
	
	// members not saved in the databse
	private Collection<SVNDirEntry> directories = null;
	private List<SVNLogEntry> revisions;
	
	/**
	 * Default Constructor, connection is not ready to be used until url, username and password are provided
	 * and the AuthManager is initialized.
	 */
	public Connection() {
		// call the super, setting the table name
		super("connection");
		
	}
	
	/**
	 * Creates Connection and prepares it with the provided information
	 * @param url
	 * @param type
	 * @param username
	 * @param password
	 * @param folder
	 */
	public Connection(String name, String url, PROTOCOL_TYPE type, String username, String password, String folder) {
		
		// call the super, setting the table name
		super("connection");
		
		this.setName(name);
		this.setUrl(url);
		this.setUsername(username);
		this.setPassword(password);
		this.setFolder(folder);
		this.initializeAuthManager();
	}
	
	/**
	 * Save the instance data to the local database on the device
	 * @param app - reference to the singleton application
	 */
	@Override
	public void saveToLocalDB(OASVNApplication app) {
		values.put("name", this.getName());
		values.put("url", this.getTextURL());
		values.put("protocol", this.type.toString());
		values.put("username", this.getUsername());
		values.put("password", this.getPassword());
		values.put("folder", this.getFolder());
		values.put("head", this.getHead());
		
		super.saveToLocalDB(app);
	}
	
	@Override
	public void setData(Cursor results) {
		try {
			this.setName(results.getString(results.getColumnIndex("name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setUsername(results.getString(results.getColumnIndex("username")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setPassword(results.getString(results.getColumnIndex("password")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setUrl(results.getString(results.getColumnIndex("url")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(results.getString(results.getColumnIndex("protocol")).equals(PROTOCOL_TYPE.HTTP.label.toString())) {
				this.type = PROTOCOL_TYPE.HTTP;
			}
			else if (results.getString(results.getColumnIndex("protocol")).equals(PROTOCOL_TYPE.HTTPS.label.toString())) {
				this.type = PROTOCOL_TYPE.HTTPS;
			}
			else if (results.getString(results.getColumnIndex("protocol")).equals(PROTOCOL_TYPE.SVN.label.toString())) {
				this.type = PROTOCOL_TYPE.SVN;
			}
			else if (results.getString(results.getColumnIndex("protocol")).equals(PROTOCOL_TYPE.SVNSSH.label.toString())) {
				this.type = PROTOCOL_TYPE.SVNSSH;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setFolder(results.getString(results.getColumnIndex("folder")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setHead(results.getInt(results.getColumnIndex("head")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.setData(results);
	}
	
	/**
	 * Setup the BasicAuthManager with the supplied username and password
	 */
	public void initializeAuthManager(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
		this.initializeAuthManager();
	}
	
	/**
	 * Setup the BasicAuthenticationManager with the user and password
	 * @return 
	 */
	public void initializeAuthManager() {
		try {
			// check the user name and password exist
			if(this.username.length() > 0 && this.password.length() > 0) {
				this.authManager = new BasicAuthenticationManager(this.username, this.password);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// revision management
	
	/**
	 * Retrieve all of the revisions that are associated with the connection.
	 * Saves them in the logs ArrayList.
	 * 
	 * @param app - Application context, required to access the database
	 */
	public String retrieveAllRevisions(OASVNApplication app) {
		try {
			List<SVNLogEntry> list;
			if (app.getAllRevisions() instanceof List)
			  list = (List<SVNLogEntry>)app.getAllRevisions();
			else
			  list = new ArrayList<SVNLogEntry>(app.getAllRevisions());
			//Collections.sort(list);
			
			this.revisions = list;
			
			return "";
		}
		catch(VerifyError ve) {
			String msg = app.getString(R.string.problem);
			ve.printStackTrace();
			
			return msg;
		}
		catch(Exception e) {
			String msg = app.getString(R.string.problem);
			e.printStackTrace();
			
			return msg;
		}
	}
	
	
	// log management
	
	/**
	 * Retrieve all of the logs that are saved in the local database and are 
	 * associated with this connection.  Saves them in the logs ArrayList.
	 * 
	 * @param app - Application context, required to access the database
	 */
	public void retrieveAllLogs(OASVNApplication app) {
		String sql = "select * from logging where connectionId = " + this.getLocalDBId() + ";";
		Cursor dbCursor = app.database.rawQuery(sql, null);
		dbCursor.moveToFirst();
		
		if(!dbCursor.isAfterLast()) {
			
			// clear out any user currently stored in mem
			this.logs.removeAll(this.logs);
			
			// iterate through local and populate
			while(!dbCursor.isAfterLast()) {
				LogItem thisLog = new LogItem();
				thisLog.setData(dbCursor);
				dbCursor.moveToNext();
				
				this.logs.add(thisLog);
			}
		}
		dbCursor.close();
	}
	
	/**
	 * Creates a LogItem, saves it to the local db associated with the connection and adds the LogItem
	 * to the connections ArrayList of log items.
	 * 
	 * @param app
	 * @param log_number
	 * @param shortMessage
	 * @param message
	 */
	public void createLogEntry(OASVNApplication app, String log_number, String shortMessage, String message) {
		//create the new LogItem
		LogItem thisLogItem = new LogItem(this.getLocalDBId(), log_number, shortMessage, message);
		
		// save the logItem to the local db
		thisLogItem.saveToLocalDB(app);
		
		// add the logItem to the arraylist
		this.logs.add(thisLogItem);
	}
	
	/**
	 * Creates a LogItem, saves it to the local db associated with the connection and adds the LogItem
	 * to the connections ArrayList of log items.
	 * 
	 * @param app
	 * @param shortMessage
	 * @param message
	 */
	public void createLogEntry(OASVNApplication app, String shortMessage, String message) {
		createLogEntry(app, "-", shortMessage, message);
	}
	
	/**
	 * Creates a LogItem, saves it to the local db associated with the connection and adds the LogItem
	 * to the connections ArrayList of log items.
	 * 
	 * @param app
	 * @param message
	 */
	public void createLogEntry(OASVNApplication app, String message) {
		createLogEntry(app, "-", "-", message);
	}
	
	/**
	 * STUB: Will delete all LogItems for this connection from the local database and remove them 
	 * from the connections arraylist of logs.
	 * @param app
	 */
	public void removeLogEntrys(OASVNApplication app) {
		for(LogItem thislog : this.getLogs()) {
			thislog.deleteFromDatabase(app);
		}
		
		// once the database removals are done empty the logs arraylist
		this.getLogs().clear();
	}
	
	/**
	 * STUB: Will delete one LogItem for this connection from the local database and remove it 
	 * from the connections arraylist of logs.
	 * @param app
	 * @param logId - Id of the log to be removed
	 */
	public void removeLogEntry(OASVNApplication app, Integer logId) {
		// flag will contain the index of the log if found
		Integer flag = -1;
		for(LogItem thisLog : this.getLogs()) {
			if(thisLog.getLocalDBId() == logId) {
				thisLog.deleteFromDatabase(app);
				flag = this.getLogs().indexOf(thisLog);
			}
		}
		
		// If there was a match remove it from teh arraylist as well
		if(flag >= 0) {
			// check to see if this is the last log entry
			if(this.getLogs().size() == 1) {
				this.getLogs().removeAll(this.getLogs());
			}
			else {
				this.getLogs().remove(flag);
			}
		}
	}
	
	
	// gettors and settors
	
	public ArrayList<LogItem> getLogs() {
		return this.logs;
	}
	
	public void setLogs(ArrayList<LogItem> logs) {
		this.logs = logs;
	}
	
	public void dateUpdated() {
		this.setDateModified(DateUtil.getGMTNow());
	}

	public String getTextURL() {
		return textURL;
	}

	public void setUrl(String url) {
		this.textURL = url;
		
		// set the type
		this.setType(url);
		
		this.setRepositoryUTL();
		
		dateUpdated();
	}
	
	public SVNURL getRepositoryURL() {
		return this.repositoryURL;
	}
	
	public void setRepositoryUTL() {
		try {
			this.repositoryURL = SVNURL.parseURIEncoded(this.getTextURL());
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	public BasicAuthenticationManager getAuthManager() {
		return authManager;
	}

	public void setAuthManager(BasicAuthenticationManager authManager) {
		this.authManager = authManager;
		dateUpdated();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		dateUpdated();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		dateUpdated();
	}

	public void setFolder(String folder) {
		this.folder = folder;
		dateUpdated();
	}

	public String getFolder() {
		return folder;
	}
	
	public PROTOCOL_TYPE getType() {
		return this.type;
	}
	
	public void setType(String url) {
		if(url.substring(0, 5).toLowerCase().equals("https")) {
			this.type = Connection.PROTOCOL_TYPE.HTTPS;
		}
		
		else if(url.substring(0, 5).toLowerCase().equals("http:")) {
			this.type = Connection.PROTOCOL_TYPE.HTTP;
		}
		
		else if(url.substring(0, 7).toLowerCase().equals("svn+ssh")) {
			this.type = Connection.PROTOCOL_TYPE.SVNSSH;
		}
		else if(url.substring(0, 4).toLowerCase().equals("svn:")) {
			this.type = Connection.PROTOCOL_TYPE.SVN;
		}
		// default to HTTP
		else {
			this.type = Connection.PROTOCOL_TYPE.HTTP;
		}
		
		dateUpdated();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setHead(Integer head) {
		this.head = head;
	}

	public Integer getHead() {
		return head;
	}

	public void setDirectories(Collection<SVNDirEntry> directories) {
		this.directories = directories;
	}

	public Collection<SVNDirEntry> getDirectories() {
		return directories;
	}

	public void setRevisions(List<SVNLogEntry> revisions) {
		this.revisions = revisions;
	}

	public List<SVNLogEntry> getRevisions() {
		return revisions;
	}
}
