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

package com.valleytg.oasvn.android.database;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.model.Connection;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * <p>Database control layer</p>
 * 
 * <p>Sets up and maintains local database</p>
 * 
 * @author brian.gormanly
 * @since version 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {
Context mContext;
	
	public static final String DB_NAME = "OASVN";
	public static final int VERSION = 3;
	
	public String pNumber;
	OASVNApplication app;
	
	public DatabaseHelper(Context context, OASVNApplication app) {
		super(context, DB_NAME, null, VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// upgrades will go here
		if(newVersion == 2) {
			try {
				String[] sql = mContext.getString(R.string.db_update_1_1).split("\n");
				db.beginTransaction();
				try {
					// Create tables & test data
					execMultipleSQL(db, sql);
					db.setTransactionSuccessful();
					Log.d("Database upgrade", "Database upgrade successful!");
				} catch (SQLException e) {
		            Log.e("Error creating tables and debug data", e.toString());
		        } finally {
		        	db.endTransaction();
		        }
				
			}
			catch(SQLException e) {
				Log.e("Database upgrade for version 1.1.0 failed", e.toString());
			}
		}
		
		if(newVersion == 3) {
			try {
				for(Connection thisConn : app.getAllConnections()) {
					thisConn.setFolder(app.getRootPath() + thisConn.getFolder());
					thisConn.saveToLocalDB(app);
				}
			}
			catch (SQLException se) {
				Log.e("Database upgrade for version 3 failed", se.toString());
			}
			catch (Exception e) {
				Log.e("Database upgrade for version 3 failed", e.toString());
			}
		}
		
	}
	
	/**
	 * Create database tables from the create scripts stored in the
	 * db_create resource in database.xml
	 * @param db
	 */
	private void createTables(SQLiteDatabase db) {
		String[] sql = mContext.getString(R.string.db_create).split("\n");
		db.beginTransaction();
		try {
			// Create tables & test data
			execMultipleSQL(db, sql);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
        	db.endTransaction();
        }
        
        /*
        
        String sql2 = "insert into phone (id, fullNumber, name, currentUser, defaultRouteId, isActive) values (1, '" + this.pNumber + "', 'Default Phone', 'Default User', 1, 1)";
		db.beginTransaction();
		try {
			// Create tables & test data
			db.execSQL(sql2);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
        	db.endTransaction();
        }
        
        */
	}

	/**
     * Execute all of the SQL statements in the String[] array
     * @param db The database on which to execute the statements
     * @param sql An array of SQL statements to execute
     */
    private void execMultipleSQL(SQLiteDatabase db, String[] sql){
    	for( String s : sql )
    		if (s.trim().length()>0)
    			db.execSQL(s);
    }

}