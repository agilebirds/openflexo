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
import java.io.PrintStream;
import java.util.Iterator;

import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AbstractConnection;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.PServerConnection;
import org.netbeans.lib.cvsclient.connection.StandardScrambler;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.openflexo.toolbox.FileUtils;

/**
 * http://lists.labs.libre-entreprise.org/pipermail/isis-fish-cvscommit/2005-November/000261.html
 * http://isis-fish.labs.libre-entreprise.org/xref/fr/ifremer/isisfish/versionning/Ssh2Connexion.html
 * 
 * @author sylvain
 * 
 */
public class TestCVS {

	private static AbstractConnection connection;

	private static GlobalOptions globalOptions;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		globalOptions = new GlobalOptions();
		globalOptions.setCVSRoot("/usr/local/CVS");

		PServerConnection c = new PServerConnection();
		connection = c;
		c.setUserName("sylvain");
		c.setEncodedPassword(/*"zSXX/NKUWoah6"*/StandardScrambler.getInstance().scramble("poulout"));
		c.setHostName("localhost");
		c.setRepository("/usr/local/CVS");
		System.out.println("hostname=" + c.getHostName());
		try {
			c.open();
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Client client = new Client(c, new StandardAdminHandler());
		client.setLocalPath("/tmp/UnNouvelEssau");

		client.getEventManager().addCVSListener(new BasicListener());

		UpdateCommand command = new UpdateCommand();
		command.setBuilder(null);

		command.setRecursive(true);
		command.setBuildDirectories(true);
		command.setPruneDirectories(true);

		try {
			System.out.println("*************** On fait un update : " + command.getCVSCommand());
			client.executeCommand(command, globalOptions);
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			for (Iterator it = client.getEntries(new File("/tmp/CoucouLesGars/TestPrjCVS")); it.hasNext();) {
				Entry next = (Entry) it.next();
				System.out.println("Entry: " + next.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("*************** On modifie le fichier !");
		try {
			FileUtils.saveToFile(new File("/tmp/CoucouLesGars/TestPrjCVS/coucou2"),
					FileUtils.fileContents(new File("/tmp/CoucouLesGars/TestPrjCVS/coucou2")) + "\nAdded on " + (new java.util.Date()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c = new PServerConnection();
		connection = c;
		c.setUserName("sylvain");
		c.setEncodedPassword(/*"zSXX/NKUWoah6"*/StandardScrambler.getInstance().scramble("poulout"));
		c.setHostName("localhost");
		c.setRepository("/usr/local/CVS");
		System.out.println("hostname=" + c.getHostName());
		try {
			c.open();
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client = new Client(c, new StandardAdminHandler());
		client.setLocalPath("/tmp/CoucouLesGars");

		client.getEventManager().addCVSListener(new BasicListener());

		command = new UpdateCommand();
		command.setBuilder(null);

		command.setRecursive(true);
		command.setBuildDirectories(true);
		command.setPruneDirectories(true);

		try {
			System.out.println("*************** On fait un update : " + command.getCVSCommand());
			client.executeCommand(command, globalOptions);
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c = new PServerConnection();
		connection = c;
		c.setUserName("sylvain");
		c.setEncodedPassword(/*"zSXX/NKUWoah6"*/StandardScrambler.getInstance().scramble("poulout"));
		c.setHostName("localhost");
		c.setRepository("/usr/local/CVS");
		System.out.println("hostname=" + c.getHostName());
		try {
			c.open();
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client = new Client(c, new StandardAdminHandler());
		client.setLocalPath("/tmp/CoucouLesGars");

		client.getEventManager().addCVSListener(new BasicListener());

		CommitCommand commit = new CommitCommand();
		commit.setBuilder(null);
		commit.setRecursive(true);
		File[] toCommit = new File[1];
		toCommit[0] = new File("/tmp/CoucouLesGars/TestPrjCVS/coucou2");
		commit.setFiles(toCommit);

		try {
			System.out.println("*************** On fait un commit : " + commit.getCVSCommand());
			client.executeCommand(commit, globalOptions);
		} catch (CommandAbortedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			for (Iterator it = client.getEntries(new File("/tmp/CoucouLesGars/TestPrjCVS")); it.hasNext();) {
				Entry next = (Entry) it.next();
				System.out.println("Entry: " + next.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * A struct containing the various bits of information in a CVS root string, allowing easy retrieval of individual items of information
	 */
	private static class CVSRoot {
		public String connectionType;
		public String user;
		public String host;
		public String repository;

		public CVSRoot(String root) throws IllegalArgumentException {
			if (!root.startsWith(":")) {
				throw new IllegalArgumentException();
			}

			int oldColonPosition = 0;
			int colonPosition = root.indexOf(':', 1);
			if (colonPosition == -1) {
				throw new IllegalArgumentException();
			}
			connectionType = root.substring(oldColonPosition + 1, colonPosition);
			oldColonPosition = colonPosition;
			colonPosition = root.indexOf('@', colonPosition + 1);
			if (colonPosition == -1) {
				throw new IllegalArgumentException();
			}
			user = root.substring(oldColonPosition + 1, colonPosition);
			oldColonPosition = colonPosition;
			colonPosition = root.indexOf(':', colonPosition + 1);
			if (colonPosition == -1) {
				throw new IllegalArgumentException();
			}
			host = root.substring(oldColonPosition + 1, colonPosition);
			repository = root.substring(colonPosition + 1);
			if (connectionType == null || user == null || host == null || repository == null) {
				throw new IllegalArgumentException();
			}
		}
	}

	public static class BasicListener extends CVSAdapter {
		/**
		 * Stores a tagged line
		 */
		private final StringBuffer taggedLine = new StringBuffer();

		/**
		 * Called when the server wants to send a message to be displayed to the user. The message is only for information purposes and
		 * clients can choose to ignore these messages if they wish.
		 * 
		 * @param e
		 *            the event
		 */
		@Override
		public void messageSent(MessageEvent e) {
			String line = e.getMessage();
			PrintStream stream = e.isError() ? System.err : System.out;

			if (e.isTagged()) {
				String message = MessageEvent.parseTaggedMessage(taggedLine, line);
				// if we get back a non-null line, we have something
				// to output. Otherwise, there is more to come and we
				// should do nothing yet.
				if (message != null) {
					stream.println(message);
				}
			} else {
				stream.println(line);
			}
		}
	}
}
