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

import com.valleytg.oasvn.android.model.Repository.PROTOCOL_TYPE;

/**
 * Local copy is a class that represents a local copy of a repository
 * usually an export.  A Local copy itself is not under version 
 * control, however it contains the base information such as full path,
 * user defined name that are used by an extending working copy.
 * 
 * @author brian.gormanly
 *
 */
public class LocalCopy extends OASVNModelLocalDB {

	/**
	 * User readable name given to the local copy
	 */
	private String name = "";
	
	/**
	 * Repository protocol type
	 */
	private COPY_TYPE type;
	
	/**
	 * Full local path
	 */
	private String fullLocalPath;
	
	
	
	/**
	 * <p>Setup a the local copy type enum.</p>
	 * <p>Will be set by the constructor at the time the class is 
	 * instantiated.</p>
	 * @author brian.gormanly
	 *
	 */
	public enum COPY_TYPE  
	{  
	    LOCALCOPY("LOCALCOPY"),  
	    WORKINGCOPY("WORKINGCOPY");
	  
	    private final String label;  
	  
	    private COPY_TYPE(String label) { this.label = label; }  
	  
	    @Override  
	    public String toString() { return label; }  
	}
}
