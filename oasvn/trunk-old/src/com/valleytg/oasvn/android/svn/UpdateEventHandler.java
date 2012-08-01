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

package com.valleytg.oasvn.android.svn;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

public class UpdateEventHandler implements ISVNEventHandler {

    public void handleEvent( SVNEvent event , double progress ) {
        /*
         * Gets the current action. An action is represented by SVNEventAction.
         * In case of an update an  action  can  be  determined  via  comparing 
         * SVNEvent.getAction() and SVNEventAction.UPDATE_-like constants. 
         */
        SVNEventAction action = event.getAction( );
        String pathChangeType = " ";
        if ( action == SVNEventAction.UPDATE_ADD ) {
            /*
             * the item was added
             */
            pathChangeType = "A";
        } else if ( action == SVNEventAction.UPDATE_DELETE ) {
            /*
             * the item was deleted
             */
            pathChangeType = "D";
        } else if ( action == SVNEventAction.UPDATE_UPDATE ) {
            /*
             * Find out in details what  state the item is (after  having  been 
             * updated).
             * 
             * Gets  the  status  of  file/directory  item   contents.  It   is 
             * SVNStatusType  who contains information on the state of an item.
             */
            SVNStatusType contentsStatus = event.getContentsStatus( );
            if ( contentsStatus == SVNStatusType.CHANGED ) {
                /*
                 * the  item  was  modified in the repository (got  the changes 
                 * from the repository
                 */
                pathChangeType = "U";
            } else if ( contentsStatus == SVNStatusType.CONFLICTED ) {
                /*
                 * The file item is in  a  state  of Conflict. That is, changes
                 * received from the repository during an update, overlap  with 
                 * local changes the user has in his working copy.
                 */
                pathChangeType = "C";
            } else if ( contentsStatus == SVNStatusType.MERGED ) {
                /*
                 * The file item was merGed (those  changes that came from  the 
                 * repository  did  not  overlap local changes and were  merged 
                 * into the file).
                 */
                pathChangeType = "G";
            }
        } else if ( action == SVNEventAction.UPDATE_EXTERNAL ) {
            /*for externals definitions*/
            System.out.println( "Fetching external item into '" + event.getFile( ).getAbsolutePath( ) + "'" );
            System.out.println( "External at revision " + event.getRevision( ) );
            return;
        } else if ( action == SVNEventAction.UPDATE_COMPLETED ) {
            /*
             * Working copy update is completed. Prints out the revision.
             */
            System.out.println( "At revision " + event.getRevision( ) );
            return;
        } else if ( action == SVNEventAction.ADD ) {
            System.out.println( "A     " + event.getFile() );
            return;
        } else if ( action == SVNEventAction.DELETE ) {
            System.out.println( "D     " + event.getFile( ) );
            return;
        } else if ( action == SVNEventAction.LOCKED ) {
            System.out.println( "L     " + event.getFile( ) );
            return;
        } else if ( action == SVNEventAction.LOCK_FAILED ) {
            System.out.println( "failed to lock    " + event.getFile( ) );
            return;
        }

        /*
         * Status of properties of an item. SVNStatusType  also
         * contains information on the properties state.
         */
        SVNStatusType propertiesStatus = event.getPropertiesStatus( );
        String propertiesChangeType = " ";
        if ( propertiesStatus == SVNStatusType.CHANGED ) {
            /*
             * Properties were updated.
             */
            propertiesChangeType = "U";
        } else if ( propertiesStatus == SVNStatusType.CONFLICTED ) {
            /*
             * Properties are in conflict with the repository.
             */
            propertiesChangeType = "C";
        } else if ( propertiesStatus == SVNStatusType.MERGED ) {
            /*
             * Properties that came from the repository were  merged  with  the
             * local ones.
             */
            propertiesChangeType = "G";
        }

        /*
         * Gets the status of the lock.
         */
        String lockLabel = " ";
        SVNStatusType lockType = event.getLockStatus();
        
        if ( lockType == SVNStatusType.LOCK_UNLOCKED ) {
            /*
             * The lock is broken by someone.
             */
            lockLabel = "B";
        }
        
        System.out.println( pathChangeType + propertiesChangeType + lockLabel + "       " + event.getFile( ) );
    }

    public void checkCancelled( ) throws SVNCancelException {
    }
}
