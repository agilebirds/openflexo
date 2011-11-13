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
package org.netbeans.lib.cvsclient.connection;

import java.io.IOException;

import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * Provides a method for accessing a connection, in order to be able to communicate using the CVS Protocol. Instances of this interface are
 * used by the Client class to communicate with the server without being too concerned with how the communication is taking place or how it
 * was set up.
 * 
 * @see org.netbeans.lib.cvsclient.Client
 * @author Robert Greig
 */
public interface Connection {
	/**
	 * Get a data inputstream for reading data
	 * 
	 * @return an input stream
	 **/
	LoggedDataInputStream getInputStream();

	/**
	 * Get an output stream for sending data to the server
	 * 
	 * @return an output stream
	 **/
	LoggedDataOutputStream getOutputStream();

	/**
	 * Open a connection with the server. Until this method is called, no communication with the server can take place. This Client will
	 * call this method before interacting with the server. It is up to implementing classes to ensure that they are configured to talk to
	 * the server (e.g. port number etc.)
	 * 
	 * @throws AutenticationException
	 *             if the connection with the server cannot be established
	 **/
	void open() throws AuthenticationException, CommandAbortedException;

	/**
	 * Verify a cnnection with the server. Simply verifies that a connection could be made, for example that the user name and password are
	 * both acceptable. Does not create input and output stream. For that, use the open() method.
	 */
	void verify() throws AuthenticationException;

	/**
	 * Close the connection with the server
	 */
	void close() throws IOException;

	/**
	 * Returns true to indicate that the connection was successfully established.
	 */
	boolean isOpen();

	/**
	 * Get the repository
	 */
	String getRepository();

	/**
	 * Get the port number, which this connection is actually using.
	 * 
	 * @return The port number or zero, when the port number does not have sense.
	 */
	int getPort();

	/**
	 * Modify the underlying inputstream
	 * 
	 * @param modifier
	 *            the connection modifier that performs the modifications
	 * @throws IOException
	 *             if an error occurs modifying the streams
	 */
	void modifyInputStream(ConnectionModifier modifier) throws IOException;

	/**
	 * Modify the underlying outputstream
	 * 
	 * @param modifier
	 *            the connection modifier that performs the modifications
	 * @throws IOException
	 *             if an error occurs modifying the streams
	 */
	void modifyOutputStream(ConnectionModifier modifier) throws IOException;
}
