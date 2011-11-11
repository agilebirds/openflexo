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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.connection.AbstractConnection;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.PServerConnection;
import org.netbeans.lib.cvsclient.connection.SSHConnection;
import org.openflexo.fps.CVSRepository.ConnectionType;

public class CVSConnection {

	private static final Logger logger = Logger.getLogger(SharedProject.class.getPackage().getName());

	private SharedProject _project;
	private AbstractConnection _connection;
	private Client _client;

	private CVSConnection(SharedProject project) {
		super();
		_project = project;
	}

	public static AbstractConnection initConnection(CVSRepository repository) {
		// Les traces indiques :

		// WARNING TO DEVELOPERS:
		// Please be warned that attempting to reuse one open connection for
		// more commands is not supported by cvs servers very well.
		// You are advised to open a new Connection each time.
		// If you still want to proceed, please do:
		// System.setProperty("javacvs.multiple_commands_warning", "false")
		// That will disable this message.

		// d'ou l'initialisation systématique de la connexion.

		if (repository.getConnectionType() == ConnectionType.SSH) {

			// Ext-SSH Connection
			SSHConnection newConnection = new SSHConnection(SocketFactory.getDefault(), repository.getHostName(), repository.getPort(),
					repository.getUserName(), repository.getPassword());
			newConnection.setRepository(repository.getRepository());
			return newConnection;
		}

		else if (repository.getConnectionType() == ConnectionType.PServer) {

			// connexion pserver
			PServerConnection newConnection = new PServerConnection();
			newConnection.setUserName(repository.getUserName());
			newConnection.setEncodedPassword(repository.getEncodedPasswd());
			newConnection.setHostName(repository.getHostName());
			newConnection.setRepository(repository.getRepository());
			return newConnection;
		}

		return null;
	}

	public static CVSConnection initCVSConnection(SharedProject project) throws IOException {

		CVSConnection connection = new CVSConnection(project);

		connection._connection = initConnection(project.getCVSRepository());

		// project.getGlobalOptions().setCVSRoot(connection.getRepository());
		connection._client = new Client(connection._connection, project.getAdminHandler());
		connection._client.setLocalPath(project.getLocalDirectory().getCanonicalPath());

		connection._client.getEventManager().addCVSListener(project.getConsoleHandler());
		connection._client.getEventManager().addCVSListener(project.getCVSHandler());

		return connection;
	}

	public void executeCommand(Command command) throws CommandAbortedException, CommandException, AuthenticationException {
		executeCommand(command, new GlobalOptions());
	}

	public String getCVSRoot() {
		return getCVSRepository().getCVSRoot();
	}

	public void executeCommand(Command command, GlobalOptions options) throws CommandAbortedException, CommandException,
			AuthenticationException {
		options.setCVSRoot(getCVSRoot());
		try {
			if (logger.isLoggable(Level.FINE))
				logger.fine("[" + Thread.currentThread().getName() + "] [CVS] BEGIN " + "cvs " + command.getCVSCommand() + " "
						+ options.getCVSCommand() + " on directory " + getClient().getLocalPath());
			getProject().getConsoleHandler()
					.commandLog(
							"cvs " + command.getCVSCommand() + " " + options.getCVSCommand() + " on directory "
									+ getClient().getLocalPath() + "\n");
			getClient().executeCommand(command, options);
			if (logger.isLoggable(Level.FINE))
				logger.fine("[" + Thread.currentThread().getName() + "] [CVS] END " + "cvs " + command.getCVSCommand() + " "
						+ options.getCVSCommand() + " on directory " + getClient().getLocalPath());
		} catch (CommandAbortedException e) {
			throw e;
		} catch (CommandException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		finally {
			try {
				if (getClient().getConnection().isOpen())
					getClient().getConnection().close();
			} catch (IOException e) {
				logger.warning("Cannot close connection " + e.getMessage());
			}
		}

	}

	public CVSRepository getCVSRepository() {
		return _project.getCVSRepository();
	}

	/*public GlobalOptions getGlobalOptions() 
	{
		return _project.getGlobalOptions();
	}*/

	public File getLocalDirectory() {
		return _project.getLocalDirectory();
	}

	public CVSModule getModule() {
		return _project.getCVSModule();
	}

	public ConnectionType getConnectionType() {
		return getCVSRepository().getConnectionType();
	}

	public String getEncodedPasswd() {
		return getCVSRepository().getEncodedPasswd();
	}

	public String getHostName() {
		return getCVSRepository().getHostName();
	}

	public String getRepository() {
		return getCVSRepository().getRepository();
	}

	public String getUserName() {
		return getCVSRepository().getUserName();
	}

	public Client getClient() {
		return _client;
	}

	public AbstractConnection getConnection() {
		return _connection;
	}

	public SharedProject getProject() {
		return _project;
	}

}
