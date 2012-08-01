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
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class CommitEventHandler implements ISVNEventHandler {

    public void handleEvent( SVNEvent event , double progress ) {
        SVNEventAction action = event.getAction( );
        if ( action == SVNEventAction.COMMIT_MODIFIED ) {
            System.out.println( "Sending   " + event.getFile( ) );
        } else if ( action == SVNEventAction.COMMIT_DELETED ) {
            System.out.println( "Deleting   " + event.getFile( ) );
        } else if ( action == SVNEventAction.COMMIT_REPLACED ) {
            System.out.println( "Replacing   " + event.getFile( ) );
        } else if ( action == SVNEventAction.COMMIT_DELTA_SENT ) {
            System.out.println( "Transmitting file data...." );
        } else if ( action == SVNEventAction.COMMIT_ADDED ) {
            /*
             * Gets the MIME-type of the item.
             */
            String mimeType = event.getMimeType( );
            if ( SVNProperty.isBinaryMimeType( mimeType ) ) {
                /*
                 * If the item is a binary file
                 */
                System.out.println( "Adding  (bin)  " + event.getFile( ) );
            } else {
                System.out.println( "Adding         " + event.getFile( ) );
            }
        }

    }
    
    public void checkCancelled( ) throws SVNCancelException {
    }
}
