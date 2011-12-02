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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

/**
 * Provides support for the :ext: connection method.
 * 
 * @author Maros Sandor
 */
public class ExtConnection extends AbstractConnection {

	private final String command;

	private Process process;

	/**
	 * Creates new EXT connection method support class. Given command will be used for getting I/O streams to CVS server.
	 * 
	 * @param command
	 *            command to execute
	 */
	public ExtConnection(String command) {
		this.command = command;
	}

	@Override
	public void open() throws AuthenticationException, CommandAbortedException {
		try {
			process = Runtime.getRuntime().exec(command);
			setInputStream(new LoggedDataInputStream(new BufferedInputStream(process.getInputStream())));
			setOutputStream(new LoggedDataOutputStream(new BufferedOutputStream(process.getOutputStream())));
		} catch (IOException e) {
			throw new AuthenticationException(e, "Failed to execute: " + command);
		}
	}

	@Override
	public void verify() throws AuthenticationException {
		try {
			open();
			verifyProtocol();
			process.destroy();
		} catch (Exception e) {
			throw new AuthenticationException(e, "Failed to execute: " + command);
		}
	}

	@Override
	public void close() throws IOException {
		if (isOpen()) {
			process.destroy();
		}
	}

	@Override
	public boolean isOpen() {
		if (process == null) {
			return false;
		}
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	@Override
	public int getPort() {
		return 0;
	}

	@Override
	public void modifyInputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyInputStream(getInputStream());
	}

	@Override
	public void modifyOutputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyOutputStream(getOutputStream());
	}
}
