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

public class WCEventHandler implements ISVNEventHandler {

    public void handleEvent( SVNEvent event , double progress ) {
        SVNEventAction action = event.getAction( );

        if ( action == SVNEventAction.ADD ) {
            /*
             * The item is scheduled for addition.
             */
            System.out.println( "A     " + event.getFile() );
            return;
        }else if ( action == SVNEventAction.COPY ){
            /*
             * The  item  is  scheduled for addition  with history (copied,  in 
             * other words).
             */
            System.out.println( "A  +  " + event.getFile( ) );
            return;
        }else if ( action == SVNEventAction.DELETE ) {
            /*
             * The item is scheduled for deletion. 
             */
            System.out.println( "D     " + event.getFile( ) );
            return;
        } else if ( action == SVNEventAction.LOCKED ){
            /*
             * The item is locked.
             */
            System.out.println( "L     " + event.getFile( ) );
            return;
        } else if ( action == SVNEventAction.LOCK_FAILED ) {
            /*
             * Locking operation failed.
             */
            System.out.println( "failed to lock    " + event.getFile( ) );
            return;
        }
    }

    public void checkCancelled( ) throws SVNCancelException {
    }
}
