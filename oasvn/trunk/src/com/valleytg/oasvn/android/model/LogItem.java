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

import android.database.Cursor;

import com.valleytg.oasvn.android.application.OASVNApplication;

public class LogItem extends OASVNModelLocalDB {
	
	private Integer connectionId;
	private String logNumber;
	private String shortMessage;
	private String message;
	
	/**
	 * Default constructor
	 */
	public LogItem() {
		// call the super and set the database name
		super("logging");
	}
	
	/**
	 * Creates a LogItem and populates it with the provided information
	 * @param connectionId
	 * @param logNumber
	 * @param shortMessage
	 * @param message
	 */
	public LogItem(Integer connectionId, String logNumber, String shortMessage, String message) {
		
		// call the super, setting the table name
		super("logging");
		
		this.setConnectionId(connectionId);
		this.setLogNumber(logNumber);
		this.setShortMessage(shortMessage);
		this.setMessage(message);
	}
	
	/**
	 * Save the instance data to the local database on the device
	 * @param app - reference to the singleton application
	 */
	@Override
	public void saveToLocalDB(OASVNApplication app) {
		values.put("connectionId", this.getConnectionId());
		values.put("log_number", this.getLogNumber());
		values.put("short_message", this.getShortMessage());
		values.put("message", this.getMessage());
		
		super.saveToLocalDB(app);
	}
	
	@Override
	public void setData(Cursor results) {
		try {
			this.setConnectionId(results.getInt(results.getColumnIndex("connectionId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setLogNumber(results.getString(results.getColumnIndex("log_number")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setShortMessage(results.getString(results.getColumnIndex("short_message")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.setMessage(results.getString(results.getColumnIndex("message")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.setData(results);
	}
	
	
	// gettors and settors

	public Integer getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(Integer connectionId) {
		this.connectionId = connectionId;
	}

	public String getLogNumber() {
		return logNumber;
	}

	public void setLogNumber(String logNumber) {
		this.logNumber = logNumber;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
