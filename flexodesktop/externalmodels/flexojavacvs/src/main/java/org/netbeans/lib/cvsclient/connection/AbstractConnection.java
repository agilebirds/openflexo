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

import org.netbeans.lib.cvsclient.request.RootRequest;
import org.netbeans.lib.cvsclient.request.UnconfiguredRequestException;
import org.netbeans.lib.cvsclient.request.UseUnchangedRequest;
import org.netbeans.lib.cvsclient.request.ValidRequestsRequest;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * This class abstracts the common features and functionality that all connection protocols to CVS share
 * 
 * @author Sriram Seshan
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * The name of the repository this connection is made to
	 */
	private String repository = null;

	/**
	 * The socket's input stream.
	 */
	private LoggedDataInputStream inputStream;

	/**
	 * The socket's output stream.
	 */
	private LoggedDataOutputStream outputStream;

	/** Creates a new instance of AbstractConnection */
	public AbstractConnection() {
	}

	/**
	 * Get an input stream for receiving data from the server.
	 * 
	 * @return a data input stream
	 */
	@Override
	public LoggedDataInputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Set an input stream for receiving data from the server. The old stream (if any) is closed.
	 * 
	 * @param inputStream
	 *            The data input stream
	 */
	protected final void setInputStream(LoggedDataInputStream inputStream) {
		if (this.inputStream == inputStream) {
			return;
		}
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			} catch (IOException ioex) {/*Ignore*/
			}
		}
		this.inputStream = inputStream;
	}

	/**
	 * Get an output stream for sending data to the server.
	 * 
	 * @return an output stream
	 */
	@Override
	public LoggedDataOutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Set an output stream for sending data to the server. The old stream (if any) is closed.
	 * 
	 * @param outputStream
	 *            The data output stream
	 */
	protected final void setOutputStream(LoggedDataOutputStream outputStream) {
		if (this.outputStream == outputStream) {
			return;
		}
		if (this.outputStream != null) {
			try {
				this.outputStream.close();
			} catch (IOException ioex) {/*Ignore*/
			}
		}
		this.outputStream = outputStream;
	}

	/**
	 * Get the repository path.
	 * 
	 * @return the repository path, e.g. /home/banana/foo/cvs
	 */
	@Override
	public String getRepository() {
		return repository;
	}

	/**
	 * Set the repository path.
	 * 
	 * @param repository
	 *            the repository
	 */
	public void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Verifies that this open connection is a connetion to a working CVS server. Clients should close this connection after verifying.
	 */
	protected void verifyProtocol() throws IOException {
		try {
			outputStream.writeBytes(new RootRequest(repository).getRequestString(), "US-ASCII");
			outputStream.writeBytes(new UseUnchangedRequest().getRequestString(), "US-ASCII");
			outputStream.writeBytes(new ValidRequestsRequest().getRequestString(), "US-ASCII");
			outputStream.writeBytes("noop \n", "US-ASCII");
		} catch (UnconfiguredRequestException e) {
			throw new RuntimeException("Internal error verifying CVS protocol: " + e.getMessage());
		}
		outputStream.flush();

		StringBuffer responseNameBuffer = new StringBuffer();
		int c;
		while ((c = inputStream.read()) != -1) {
			responseNameBuffer.append((char) c);
			if (c == '\n') {
				break;
			}
		}

		String response = responseNameBuffer.toString();
		if (!response.startsWith("Valid-requests")) {
			throw new IOException("Unexpected server response: " + response);
		}
	}
}
