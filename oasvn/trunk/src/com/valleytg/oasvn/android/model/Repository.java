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
import java.util.Date;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import android.database.Cursor;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.Connection.PROTOCOL_TYPE;
import com.valleytg.oasvn.android.util.DateUtil;

/**
 * Repository Model
 * @author brian.gormanly
 * @version Added 1.1 replaces Connection.
 *
 */

public class Repository extends OASVNModelLocalDB {

	/**
	 * <p>SVN repository reference.</p>
	 * <p>the SVN object that is being wrapped and managed by this class.</p>
	 * <p>Provides an interface for protocol specific drivers used for 
	 * direct working with a Subversion repository. SVNRepository joins 
	 * all low-level API methods needed for repository access operations.</p>
	 */
	SVNRepository repository;
	
	/**
	 * Application context, required to instantiate the class.
	 */
	OASVNApplication app;
	
	/**
	 * User readable name given to the repository
	 */
	private String name = "";
	
	/**
	 * SVNURL repository URL
	 */
	private SVNURL repositoryURL;
	
	/**
	 * Repository protocol type
	 */
	private PROTOCOL_TYPE type;
	
	/**
	 * Username for repository authentication
	 */
	private String username ="";
	
	/**
	 * Password for repository authentication
	 */
	private String password = "";
	
	/**
	 * Known head version of the repository.  This information
	 * can be outdated in the local database and should be checked.
	 */
	private Integer head = 0;
	
	/**
	 * List of revisions for this repository
	 */
	private List<SVNLogEntry> revisions;
	
	/**
	 * ArrayList of logitems for this repository
	 */
	private ArrayList<LogItem> logs = new ArrayList<LogItem>();
	
	/**
	 * Used by SVNRepository drivers for user authentication purposes.
	 */
    private ISVNAuthenticationManager authManager;
	
    /**
	 * Default Constructor, repository is not ready to be used until url, username and password are provided
	 * and the AuthManager is initialized.
	 */
	public Repository(OASVNApplication app) {
		// call the super, setting the table name
		super("repository");
		
		// set the context
		this.app = app;
		
	}
	
	/**
	 * Creates repository and prepares it with the provided information
	 * @param url - non-encoded!
	 * @param type
	 * @param username
	 * @param password
	 */
	public Repository(OASVNApplication app, String name, String url, PROTOCOL_TYPE type, String username, String password) {
		
		// call the super, setting the table name
		super("connection");
		
		this.setName(name);
		this.setRepositoryDecodedURL(url);
		this.setUsername(username);
		this.setPassword(password);
		this.initializeAuthManager();
		
		// set the context
		this.app = app;
	}
	
	/**
	 * Setup the BasicAuthManager with the supplied username and password
	 */
	private void initializeAuthManager(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
		this.initializeAuthManager();
	}
	
	/**
	 * Setup the BasicAuthenticationManager with the user and password
	 * @return 
	 */
	private void initializeAuthManager() {
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
	
	private void initializeRepository() {
		
		initializeAuthManager();
		
		try {
			this.setRepository(SVNRepositoryFactory.create(this.repositoryURL));
			this.getRepository().setAuthenticationManager(this.getAuthManager());
		} 
		catch(SVNException se) {
			String msg = se.getMessage();
			
			// log this failure
			this.createLogEntry(app, app.getString(R.string.error), se.getMessage().substring(0, 19), se.getMessage().toString());
			
			se.printStackTrace();
		}
		catch(VerifyError ve) {
			String msg = ve.getMessage();
			
			// log this failure
			this.createLogEntry(app, app.getString(R.string.error), ve.getMessage().substring(0, 19), ve.getMessage().toString());
			
			ve.printStackTrace();
		}
		catch(Exception e) {
			String msg = e.getMessage();
			
			// log this failure
			this.createLogEntry(app, app.getString(R.string.error), e.getCause().toString().substring(0, 19), e.getMessage().toString());
			
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
	
	/**
	 * Retrieve all of the revisions that are associated with the connection.
	 * Saves them in the logs ArrayList.
	 * 
	 * @param app - Application context, required to access the database
	 */
	public String retrieveXRevisions(OASVNApplication app, long numRevisions) {
		try {
			List<SVNLogEntry> list;
			if (app.getXRevisions(numRevisions) instanceof List)
			  list = (List<SVNLogEntry>)app.getXRevisions(numRevisions);
			else
			  list = new ArrayList<SVNLogEntry>(app.getXRevisions(numRevisions));
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
		values.put("head", this.getHead());
		
		super.saveToLocalDB(app);
	}
	
	/**
	 * Retrieve the data from the database and assign it to the proper members
	 */
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
			this.setRepositoryDecodedURL(results.getString(results.getColumnIndex("url")));
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
			this.setHead(results.getInt(results.getColumnIndex("head")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.setData(results);
	}
	
	
	
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

	public SVNRepository getRepository() {
		return repository;
	}

	public void setRepository(SVNRepository repository) {
		this.repository = repository;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SVNURL getRepositoryURL() {
		return repositoryURL;
	}
	
	public String getTextDecodedURL() {
		return this.repositoryURL.toDecodedString();
	}
	
	public String getTextURL() {
		return this.repositoryURL.toString();
	}

	public void setRepositoryURL(SVNURL repositoryURL) {
		this.repositoryURL = repositoryURL;
	}
	
	/**
	 * Set the repository url using a non-encoded text string url.
	 * @param url - non-encoded url
	 */
	public void setRepositoryDecodedURL(String url) {
		if(url != null) {
			try {
				this.repositoryURL = SVNURL.parseURIDecoded(url);
			} catch (SVNException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Set the repository url using a encoded text string url.
	 * @param url - encoded url
	 */
	public void setRepositoryEncodedURL(String url) {
		if(url != null) {
			try {
				this.repositoryURL = SVNURL.parseURIEncoded(url);
			} catch (SVNException e) {
				e.printStackTrace();
			}
		}
	}

	public PROTOCOL_TYPE getType() {
		return type;
	}

	public void setType(PROTOCOL_TYPE type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getHead() {
		return head;
	}

	public void setHead(Integer head) {
		this.head = head;
	}

	public List<SVNLogEntry> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<SVNLogEntry> revisions) {
		this.revisions = revisions;
	}

	public ISVNAuthenticationManager getAuthManager() {
		return authManager;
	}

	public void setAuthManager(ISVNAuthenticationManager authManager) {
		this.authManager = authManager;
	}

	public ArrayList<LogItem> getLogs() {
		return logs;
	}

	public void setLogs(ArrayList<LogItem> logs) {
		this.logs = logs;
	}
}
