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

import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * Implements a connection to a local server. See the cvs documents for more information about different connection methods. Local is
 * popular where the CVS repository exists on the machine where the client library is running.
 * <p>
 * Because this library implements just the client part, it can not operate directly on the repository. It needs a server to talk to.
 * Therefore it needs to execute the server process on the local machine.
 * 
 * @author Robert Greig
 */
public class LocalConnection extends AbstractConnection {

	private static final String CVS_EXE_COMMAND = System.getenv("CVS_EXE") != null ? System.getenv("CVS_EXE") + " server" : "cvs server"; // NOI18N

	/**
	 * The CVS process that is being run.
	 */
	protected Process process;

	/**
	 * Creates a instance of ServerConnection.
	 */
	public LocalConnection() {
		reset();
	}

	/**
	 * Authenticate a connection with the server.
	 * 
	 * @throws AuthenticationException
	 *             if an error occurred
	 */
	private void openConnection() throws AuthenticationException {
		try {
			process = Runtime.getRuntime().exec(CVS_EXE_COMMAND);
			setOutputStream(new LoggedDataOutputStream(process.getOutputStream()));
			setInputStream(new LoggedDataInputStream(process.getInputStream()));
		} catch (IOException t) {
			reset();
			String locMessage = AuthenticationException.getBundleString("AuthenticationException.ServerConnection"); // NOI18N
			throw new AuthenticationException("Connection error", t, locMessage); // NOI18N
		}
	}

	private void reset() {
		process = null;
		setInputStream(null);
		setOutputStream(null);
	}

	/**
	 * Authenticate with the server. Closes the connection immediately. Clients can use this method to ensure that they are capable of
	 * authenticating with the server. If no exception is thrown, you can assume that authentication was successful
	 * 
	 * @throws AuthenticationException
	 *             if the connection with the server cannot be established
	 */
	@Override
	public void verify() throws AuthenticationException {
		try {
			openConnection();
			verifyProtocol();
			process.destroy();
		} catch (Exception e) {
			String locMessage = AuthenticationException.getBundleString("AuthenticationException.ServerVerification"); // NOI18N
			throw new AuthenticationException("Verification error", e, locMessage); // NOI18N
		} finally {
			reset();
		}
	}

	/**
	 * Authenticate with the server and open a channel of communication with the server. This Client will call this method before
	 * interacting with the server. It is up to implementing classes to ensure that they are configured to talk to the server (e.g. port
	 * number etc.)
	 * 
	 * @throws AuthenticationException
	 *             if the connection with the server cannot be established
	 */
	@Override
	public void open() throws AuthenticationException {
		openConnection();
	}

	/**
	 * Returns true to indicate that the connection was successfully established.
	 */
	@Override
	public boolean isOpen() {
		return process != null;
	}

	/**
	 * Close the connection with the server.
	 */
	@Override
	public void close() throws IOException {
		try {
			if (process != null) {
				process.destroy();
			}
		} finally {
			reset();
		}
	}

	/**
	 * @return 0, no port is used by the local connection.
	 */
	@Override
	public int getPort() {
		return 0; // No port
	}

	/**
	 * Modify the underlying inputstream.
	 * 
	 * @param modifier
	 *            the connection modifier that performs the modifications
	 * @throws IOException
	 *             if an error occurs modifying the streams
	 */
	@Override
	public void modifyInputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyInputStream(getInputStream());
	}

	/**
	 * Modify the underlying outputstream.
	 * 
	 * @param modifier
	 *            the connection modifier that performs the modifications
	 * @throws IOException
	 *             if an error occurs modifying the streams
	 */
	@Override
	public void modifyOutputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyOutputStream(getOutputStream());
	}

}
