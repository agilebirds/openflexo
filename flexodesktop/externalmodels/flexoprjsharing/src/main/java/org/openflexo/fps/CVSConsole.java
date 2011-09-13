/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fps;

import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.event.BinaryMessageEvent;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.event.MessageEvent;

public class CVSConsole extends CVSAdapter
{
	public static final Logger logger = Logger.getLogger(CVSConsole.class.getPackage().getName());

	private final StringBuffer taggedLine = new StringBuffer();

	   private static CVSConsole _instance = null;
	   
	   private Vector<ConsoleListener> _listeners;
	   
	   public static CVSConsole getCVSConsole()
	   {
		   if (_instance == null) 
			   _instance = new CVSConsole();
		   return _instance;
	   }
	   
	   public interface ConsoleListener
	   {
		   public void log (String logString);
		   public void errorLog (String logString);
		   public void commandLog (String logString);
	   }
	   
	    private CVSConsole() 
	    {
	        super();
	        _listeners = new Vector<ConsoleListener>();
	        	addToConsoleListeners(new StandardConsole());
	    }
	    
	    public void addToConsoleListeners(ConsoleListener l)
	    {
	    	_listeners.add(l);
	    }
	    
	    public void removeFromConsoleListeners(ConsoleListener l)
	    {
	    	_listeners.remove(l);
	    }
	    
	    private class StandardConsole implements ConsoleListener
	    {
	    	PrintStream stderr = System.err;
	    	PrintStream stdout = System.out;
	    	@Override
			public void errorLog(String logString) {
	    		if (logger.isLoggable(Level.FINE)) {
	    			stderr.println(logString);
	    		}
	    	}
	    	@Override
			public void log(String logString) {
	    		if (logger.isLoggable(Level.FINER)) {
	    			stdout.println(logString);
	    		}
	    	}
	    	@Override
			public void commandLog(String logString) {
	    		if (logger.isLoggable(Level.FINE)) {
	    			stdout.println(logString);
	    		}
	    	}
	    }

	    /**
	     * Called when the server wants to send a message to be displayed to
	     * the user. The message is only for information purposes and clients
	     * can choose to ignore these messages if they wish.
	     * @param e the event
	     */
	    @Override
		public void messageSent(MessageEvent e) 
	    {
	        String line = e.getMessage();
	        if (e instanceof EnhancedMessageEvent) {
	            return ;
	        }

	        if (e.isTagged()) {
	            String message = MessageEvent.parseTaggedMessage(taggedLine, e.getMessage());
	            if (message != null) {
	            	if (e.isError()) errorLog(message); else log(message);
	            }
	        }
	        else {
            	if (e.isError()) errorLog(line); else log(line);
	        }
	    }

	    private void log (String logString)
	    {
	    	for (ConsoleListener l : _listeners) {
	    		l.log(logString);
	    	}
	    }
	    
	    private void errorLog (String logString)
	    {
	    	for (ConsoleListener l : _listeners) {
	    		l.errorLog(logString);
	    	}
	    }

	    public void commandLog (String logString)
	    {
	    	for (ConsoleListener l : _listeners) {
	    		l.commandLog(logString);
	    	}
	    }

	    /**
	     * Called when the server wants to send a binary message to be displayed to
	     * the user. The message is only for information purposes and clients
	     * can choose to ignore these messages if they wish.
	     * @param e the event
	     */
	    @Override
		public void messageSent(BinaryMessageEvent e) 
	    {
	        byte[] bytes = e.getMessage();
	        int len = e.getMessageLength();
	        log(new String(bytes));
	    }

	    /**
	     * Called when file status information has been received
	     */
	    @Override
		public void fileInfoGenerated(FileInfoEvent e)
	    {
//	      FileInfoContainer fileInfo = e.getInfoContainer();
//	        if (fileInfo.getClass().equals(StatusInformation.class)) {
//	          System.err.println("A file status event was received.");
//	          System.err.println("The status information object is: " +
//	                             fileInfo);
//	        }
	    }
}

