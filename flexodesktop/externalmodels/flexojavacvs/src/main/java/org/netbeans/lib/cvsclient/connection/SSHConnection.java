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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.net.SocketFactory;

import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * Provides SSH tunnel for :ext: connection method.
 * 
 * @author Maros Sandor
 */
public class SSHConnection extends AbstractConnection {

	private static final String CVS_SERVER_COMMAND = System.getenv("CVS_SERVER") != null ? System.getenv("CVS_SERVER") + " server"
			: "cvs server"; // NOI18N

	private final SocketFactory socketFactory;
	private final String host;
	private final int port;
	private final String username;
	private final String password;

	private Session session;
	private ChannelExec channel;

	/**
	 * Creates new SSH connection object.
	 * 
	 * @param socketFactory
	 *            socket factory to use when connecting to SSH server
	 * @param host
	 *            host names of the SSH server
	 * @param port
	 *            port number of SSH server
	 * @param username
	 *            SSH username
	 * @param password
	 *            SSH password
	 */
	public SSHConnection(SocketFactory socketFactory, String host, int port, String username, String password) {
		this.socketFactory = socketFactory;
		this.host = host;
		this.port = port;
		this.username = username != null ? username : System.getProperty("user.name"); // NOI18N
		this.password = password;
	}

	@Override
	public void open() throws AuthenticationException, CommandAbortedException {

		if (password == null) {
			System.out.println("NULL passwd");
			throw new AuthenticationException("null password", "null password");
		}

		Properties props = new Properties();
		props.put("StrictHostKeyChecking", "no"); // NOI18N

		JSch jsch = new JSch();
		try {
			session = jsch.getSession(username, host, port);
			session.setSocketFactory(new SocketFactoryBridge());
			session.setConfig(props);
			session.setUserInfo(new SSHUserInfo());
			session.connect();
		} catch (JSchException e) {
			throw new AuthenticationException(e, "NbBundle.getMessage(SSHConnection.class, 'BK3001'");
		}

		try {
			channel = (ChannelExec) session.openChannel("exec"); // NOI18N
			channel.setCommand(CVS_SERVER_COMMAND);
			setInputStream(new LoggedDataInputStream(new SshChannelInputStream(channel)));
			setOutputStream(new LoggedDataOutputStream(channel.getOutputStream()));
			channel.connect();
		} catch (JSchException e) {
			IOException ioe = new IOException("NbBundle.getMessage(SSHConnection.class, 'BK3002'");
			ioe.initCause(e);
			throw new AuthenticationException(ioe, "NbBundle.getMessage(SSHConnection.class, 'BK3003'");
		} catch (IOException e) {
			throw new AuthenticationException(e, "NbBundle.getMessage(SSHConnection.class, 'BK3003'");
		}

	}

	/**
	 * Verifies that we can successfuly connect to the SSH server and run 'cvs server' command on it.
	 * 
	 * @throws AuthenticationException
	 *             if connection to the SSH server cannot be established (network problem)
	 */
	@Override
	public void verify() throws AuthenticationException {
		try {
			open();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
			if (channel.getExitStatus() != -1) {
				throw new AuthenticationException(CVS_SERVER_COMMAND, "NbBundle.getMessage(SSHConnection.class, 'BK3004'");
			}
			close();
		} catch (CommandAbortedException e) {
			throw new AuthenticationException(e, "NbBundle.getMessage(SSHConnection.class, 'BK3005'");
		} catch (IOException e) {
			throw new AuthenticationException(e, "NbBundle.getMessage(SSHConnection.class, 'Bk3006'");
		} finally {
			reset();
		}
	}

	private void reset() {
		session = null;
		channel = null;
		setInputStream(null);
		setOutputStream(null);
	}

	@Override
	public void close() throws IOException {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
		reset();
	}

	@Override
	public boolean isOpen() {
		return channel != null && channel.isConnected();
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void modifyInputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyInputStream(getInputStream());
	}

	@Override
	public void modifyOutputStream(ConnectionModifier modifier) throws IOException {
		modifier.modifyOutputStream(getOutputStream());
	}

	/**
	 * Provides JSch with SSH password.
	 */
	private class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptYesNo(String message) {
			return false;
		}

		@Override
		public void showMessage(String message) {
		}

		@Override
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
			String[] response = new String[prompt.length];
			if (prompt.length == 1) {
				response[0] = password;
			}
			return response;
		}
	}

	/**
	 * Bridges com.jcraft.jsch.SocketFactory and javax.net.SocketFactory.
	 */
	private class SocketFactoryBridge implements com.jcraft.jsch.SocketFactory {

		@Override
		public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
			return socketFactory.createSocket(host, port);
		}

		@Override
		public InputStream getInputStream(Socket socket) throws IOException {
			return socket.getInputStream();
		}

		@Override
		public OutputStream getOutputStream(Socket socket) throws IOException {
			return socket.getOutputStream();
		}
	}

	private static class SshChannelInputStream extends FilterInputStream {

		private final Channel channel;

		public SshChannelInputStream(Channel channel) throws IOException {
			super(channel.getInputStream());
			this.channel = channel;
		}

		@Override
		public int available() throws IOException {
			checkChannelState();
			return super.available();
		}

		private void checkChannelState() throws IOException {
			int exitStatus = channel.getExitStatus();
			if (exitStatus > 0 || exitStatus < -1) {
				throw new IOException("NbBundle.getMessage(SSHConnection.class, 'BK3004'");
			}
			if (exitStatus == 0 || channel.isEOF()) {
				throw new EOFException("NbBundle.getMessage(SSHConnection.class, 'BK3007'");
			}
		}
	}
}
