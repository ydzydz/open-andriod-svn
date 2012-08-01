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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.svn.InfoHandler;
import com.valleytg.oasvn.android.svn.StatusHandler;

/**
 * Working Copy is a SVN managed version controlled version of 
 * Local Copy.  Everything about a working copy should be 
 * managed through this class.
 * 
 * @author brian.gormanly
 *
 */
public class WorkingCopy extends LocalCopy {
	
	private static SVNClientManager ourClientManager;
	
	public WorkingCopy(BasicAuthenticationManager myAuthManager) {
		ISVNOptions options = SVNWCUtil.createDefaultOptions( true );
		
		ourClientManager = SVNClientManager.newInstance( options , myAuthManager );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Retrieve all of the revisions that are associated with the connection.
	 * Saves them in the logs ArrayList.
	 * 
	 * @param app - Application context, required to access the database
	 */
	public String retrieveAllRevisions(OASVNApplication app) {
		try {
			List<Revision> list;
			if (app.getAllRevisions() instanceof List)
			  list = (List<Revision>)app.getAllRevisions();
			else
			  list = new ArrayList<Revision>(app.getAllRevisions());
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
	
	
	
	
	
	
	
	
	
	private static SVNCommitInfo makeDirectory( SVNURL url , String commitMessage ) throws SVNException {
		return ourClientManager.getCommitClient( ).doMkDir( new SVNURL[] { url } , commitMessage );
	}
	
	private static SVNCommitInfo importDirectory( File localPath , SVNURL dstURL , String commitMessage , boolean isRecursive ) throws SVNException {
		return ourClientManager.getCommitClient( ).doImport( localPath , dstURL , commitMessage , isRecursive );
	}
	
	private static SVNCommitInfo commit( File wcPath , boolean keepLocks , String commitMessage ) throws SVNException {
		return ourClientManager.getCommitClient().doCommit( new File[] { wcPath } , keepLocks , commitMessage , false , true );
	}
	
	private static long checkout( SVNURL url , SVNRevision revision , File destPath , boolean isRecursive ) throws SVNException {
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient( );
		/*
		 * sets externals not to be ignored during the checkout
		 */
		updateClient.setIgnoreExternals( false );
		/*
		 * returns the number of the revision at which the working copy is 
		 */
		return updateClient.doCheckout( url , destPath , revision , revision , isRecursive );
	}
	
	private static long update( File wcPath , SVNRevision updateToRevision , boolean isRecursive ) throws SVNException {

        SVNUpdateClient updateClient = ourClientManager.getUpdateClient( );
        /*
         * sets externals not to be ignored during the update
         */
        updateClient.setIgnoreExternals( false );
        /*
         * returns the number of the revision wcPath was updated to
         */
        return updateClient.doUpdate( wcPath , updateToRevision , isRecursive );
    }
	
	private static long switchToURL( File wcPath , SVNURL url , SVNRevision updateToRevision , boolean isRecursive ) throws SVNException {
        SVNUpdateClient updateClient = ourClientManager.getUpdateClient( );
        /*
         * sets externals not to be ignored during the switch
         */
        updateClient.setIgnoreExternals( false );
        /*
         * returns the number of the revision wcPath was updated to
         */
        return updateClient.doSwitch( wcPath , url , updateToRevision , isRecursive );
    }
	
	private static void addEntry( File wcPath ) throws SVNException {
        ourClientManager.getWCClient( ).doAdd( wcPath , false , false , false , true );
    }
	
	private static void lock( File wcPath , boolean isStealLock , String lockComment ) throws SVNException {
        ourClientManager.getWCClient( ).doLock( new File[] { wcPath } , isStealLock , lockComment );
    }
	
	private static void delete( File wcPath , boolean force ) throws SVNException {
        ourClientManager.getWCClient( ).doDelete( wcPath , force , false );
    }
	
	private static void copy( SVNURL srcURL , String dstURL , boolean isMove , String commitMessage ) throws SVNException {
        //return ourClientManager.getCopyClient().doCopy( srcURL , SVNRevision.HEAD , dstURL , isMove , commitMessage );
		SVNCopySource source = new SVNCopySource(SVNRevision.UNDEFINED, SVNRevision.WORKING, srcURL);
		ourClientManager.getCopyClient().doCopy(new SVNCopySource[] {source}, new File(dstURL) , false , false , isMove);
    }
	
	private static void showStatus( File wcPath , boolean isRecursive , boolean isRemote , boolean isReportAll ,
            boolean isIncludeIgnored , boolean isCollectParentExternals ) throws SVNException {

		ourClientManager.getStatusClient( ).doStatus(wcPath, isRecursive, isRemote, isReportAll, isIncludeIgnored, isCollectParentExternals, new StatusHandler( isRemote ) );
	}
	
	private static void showInfo( File wcPath , SVNRevision revision , boolean isRecursive ) throws SVNException {
        ourClientManager.getWCClient( ).doInfo( wcPath , revision , isRecursive , new InfoHandler( ) );
    }
	
	private static void error( String message , Exception e ) {
        System.err.println(message + ( e != null ? ": " + e.getMessage( ) : "" ) );
        System.exit( 1 );
    }
	
	private static final void createLocalDir( File aNewDir , File[] localFiles , String[] fileContents ) {
        if ( !aNewDir.mkdirs( ) ) {
            error( "failed to create a new directory '" + aNewDir.getAbsolutePath( ) + "'.", null );
        }

        for( int i = 0; i < localFiles.length; i++ ) {
            File aNewFile = localFiles[i];
            try {
                if ( !aNewFile.createNewFile( ) ) {
                    error( "failed to create a new file '" + aNewFile.getAbsolutePath( ) + "'." , null );
                }
            } catch ( IOException ioe ) {
                aNewFile.delete( );
                error( "error while creating a new file '" + aNewFile.getAbsolutePath( ) + "'" , ioe );
            }
        
            String contents = null;
            if ( i > fileContents.length - 1 ) {
                continue;
            }
            contents = fileContents[i];
            
            /*
             * writing a text into the file
             */
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream( aNewFile );
                fos.write( contents.getBytes( ) );
            } catch ( FileNotFoundException fnfe ) {
                error( "the file '" + aNewFile.getAbsolutePath( ) + "' is not found" , fnfe );
            } catch ( IOException ioe ) {
                error( "error while writing into the file '" + aNewFile.getAbsolutePath( ) + "'" , ioe );
            } finally {
                if ( fos != null ) {
                    try {
                        fos.close( );
                    } catch ( IOException ioe ) {
                        //
                    }
                }
            }
        }
    }
	
}
