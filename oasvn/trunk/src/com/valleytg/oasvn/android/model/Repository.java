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

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

public class Repository extends OASVNModelLocalDB {

	/**
	 * <p>SVN repository reference.</p>
	 * <p>Provides an interface for protocol specific drivers used for 
	 * direct working with a Subversion repository. SVNRepository joins 
	 * all low-level API methods needed for repository access operations.</p>
	 */
	SVNRepository repository;
	
	/**
	 * User readable name given to the repository
	 */
	private String name = "";
	
	/**
	 * URL of the repository as string
	 */
	private String textURL = "";
	
	/**
	 * SVNURL repository URL (in use?)
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
	 * Used by SVNRepository drivers for user authentication purposes.
	 */
    private ISVNAuthenticationManager authManager;
	
	/**
	 * Date / Time that this repository object was last compared against 
	 * the actual SVN repository.  This date can be used to determine 
	 * current data dirty state and also as feed back to the user about
	 * the age of their current information.
	 */
    private Date lastRemoteUpadate;
	
	
	
	
	
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
}
