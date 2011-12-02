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
package org.netbeans.lib.cvsclient;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.netbeans.lib.cvsclient.admin.AdminHandler;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.KeywordSubstitutionOptions;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.event.CVSEvent;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.netbeans.lib.cvsclient.event.EventManager;
import org.netbeans.lib.cvsclient.file.DefaultFileHandler;
import org.netbeans.lib.cvsclient.file.FileDetails;
import org.netbeans.lib.cvsclient.file.FileHandler;
import org.netbeans.lib.cvsclient.file.GzippedFileHandler;
import org.netbeans.lib.cvsclient.request.ExpandModulesRequest;
import org.netbeans.lib.cvsclient.request.GzipFileContentsRequest;
import org.netbeans.lib.cvsclient.request.Request;
import org.netbeans.lib.cvsclient.request.RootRequest;
import org.netbeans.lib.cvsclient.request.UnconfiguredRequestException;
import org.netbeans.lib.cvsclient.request.UseUnchangedRequest;
import org.netbeans.lib.cvsclient.request.ValidRequestsRequest;
import org.netbeans.lib.cvsclient.request.ValidResponsesRequest;
import org.netbeans.lib.cvsclient.request.WrapperSendRequest;
import org.netbeans.lib.cvsclient.response.ErrorMessageResponse;
import org.netbeans.lib.cvsclient.response.Response;
import org.netbeans.lib.cvsclient.response.ResponseException;
import org.netbeans.lib.cvsclient.response.ResponseFactory;
import org.netbeans.lib.cvsclient.response.ResponseServices;
import org.netbeans.lib.cvsclient.util.BugLog;
import org.netbeans.lib.cvsclient.util.DefaultIgnoreFileFilter;
import org.netbeans.lib.cvsclient.util.IgnoreFileFilter;
import org.netbeans.lib.cvsclient.util.LoggedDataInputStream;
import org.netbeans.lib.cvsclient.util.LoggedDataOutputStream;
import org.netbeans.lib.cvsclient.util.Logger;
import org.netbeans.lib.cvsclient.util.StringPattern;

/**
 * The main way of communication with a server using the CVS Protocol. The client is not responsible for setting up the connection with the
 * server, only interacting with it.
 * 
 * @see org.netbeans.lib.cvsclient.connection.Connection
 * @author Robert Greig
 */
public class Client implements ClientServices, ResponseServices {
	/**
	 * The connection used to interact with the server.
	 */
	private Connection connection;

	/**
	 * The file handler to use.
	 */
	private FileHandler transmitFileHandler;

	private FileHandler gzipFileHandler = new GzippedFileHandler();
	private FileHandler uncompFileHandler = new DefaultFileHandler();

	private boolean dontUseGzipFileHandler;

	/**
	 * The modified date.
	 */
	private Date modifiedDate;

	/**
	 * The admin handler to use.
	 */
	private AdminHandler adminHandler;

	/**
	 * The local path, ie the path to the directory in which this command was executed.
	 */
	private String localPath;

	/**
	 * Whether any commands have been executed so far. This allows us to send some initialisation requests before the first command.
	 */
	private boolean isFirstCommand = true;

	/**
	 * The event manager.
	 */
	private final EventManager eventManager = new EventManager(this);

	/**
	 * The global options for the executing command.
	 */
	private GlobalOptions globalOptions;

	private PrintStream stderr = System.err;

	/**
	 * This is set to true if we should abort the current command.
	 */
	private boolean abort;

	private ResponseFactory responseFactory;

	private IgnoreFileFilter ignoreFileFilter;

	/*
	 * The valid list of requests that is valid for the CVS server
	 * corresponding to this client
	 */
	private Map validRequests = new HashMap();

	/**
	 * A map of file patterns and keyword substitution options
	 */
	private Map wrappersMap = null;

	/**
	 * This will be set to true after initialization requests are sent to the server. The initialization requests setup valid requests and
	 * send RootRequest to the server.
	 */
	private boolean initialRequestsSent = false;

	private boolean printConnectionReuseWarning = false;

	private static final Set ALLOWED_CONNECTION_REUSE_REQUESTS = new HashSet(Arrays.asList(new Class[] { ExpandModulesRequest.class,
			WrapperSendRequest.class }));

	// processRequests & getCounter
	private LoggedDataInputStream loggedDataInputStream;
	private LoggedDataOutputStream loggedDataOutputStream;
	private boolean warned;

	/**
	 * Construct a Client using a given connection and file handler. You must initialize the connection and adminHandler first. <code>
	 *  // establish connection to the given CVS pserver
	 *  PServerConnection connection = new PServerConnection();
	 *  connection.setUserName(userName);
	 *  connection.setHostName(hostName);
	 *  connection.setEncodedPassword(StandardScrambler.getInstance().scramble(password));
	 *  connection.setRepository(repository);
	 *  // test the connection
	 *  try {
	 *      connection.open();
	 *  } catch (AuthenticationException e) {
	 *      // do something
	 *  }
	 * 
	 *  // create a CVS client
	 *  Client cvsClient = new Client(connection,new StandardAdminHandler());
	 * 
	 *  // set the directory in which we work
	 *  cvsClient.setLocalPath(localPath);
	 * </code>
	 * 
	 * @param connection
	 *            the connection to the cvs server
	 * @param adminHandler
	 *            the admin handler to use
	 */
	public Client(Connection connection, AdminHandler adminHandler) {
		setConnection(connection);
		setAdminHandler(adminHandler);
		ignoreFileFilter = new DefaultIgnoreFileFilter();
		dontUseGzipFileHandler = false;
	}

	public void setErrorStream(PrintStream stderr) {
		this.stderr = stderr;
	}

	/**
	 * Get the connection used for communicating with the server. Connection.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Set the connection used for communicating with the server.
	 * 
	 * @param c
	 *            the connection to use for all communication with the server
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
		initialRequestsSent = false;
		setIsFirstCommand(true);
	}

	/**
	 * Get the admin handler uesd to read and write administrative information about files on the local machine.
	 * 
	 * @return the admin handler
	 */
	public AdminHandler getAdminHandler() {
		return adminHandler;
	}

	/**
	 * Set the admin handler used to read and write administrative information about files on the local machine.
	 */
	public void setAdminHandler(AdminHandler adminHandler) {
		this.adminHandler = adminHandler;
	}

	/**
	 * Get the local path; that is, the path to the directory in which the currently executing command is referring.
	 */
	@Override
	public String getLocalPath() {
		return localPath;
	}

	/**
	 * Set the local path, i.e. the path to the directory in which all commands are given (top level).
	 */
	public void setLocalPath(String localPath) {
		// remove trailing (back-) slash
		localPath = localPath.replace('\\', '/');
		while (localPath.endsWith("/")) { // NOI18N
			localPath = localPath.substring(0, localPath.length() - 1);
		}

		this.localPath = localPath;
	}

	/**
	 * Returns true if no previous command was executed using thiz.
	 */
	@Override
	public boolean isFirstCommand() {
		return isFirstCommand;
	}

	/**
	 * Set whether this is the first command. Normally you do not need to set this yourself - after execution the first command will have
	 * set this to false.
	 */
	@Override
	public void setIsFirstCommand(boolean isFirstCommand) {
		this.isFirstCommand = isFirstCommand;
	}

	/**
	 * Return the uncompressed file handler.
	 */
	@Override
	public FileHandler getUncompressedFileHandler() {
		return uncompFileHandler;
	}

	/**
	 * Set the uncompressed file handler.
	 */
	@Override
	public void setUncompressedFileHandler(FileHandler uncompFileHandler) {
		this.uncompFileHandler = uncompFileHandler;
	}

	/**
	 * Return the Gzip stream handler.
	 */
	@Override
	public FileHandler getGzipFileHandler() {
		return gzipFileHandler;
	}

	/**
	 * Set the handler for Gzip data.
	 */
	@Override
	public void setGzipFileHandler(FileHandler gzipFileHandler) {
		this.gzipFileHandler = gzipFileHandler;
	}

	/**
	 * ReSet the filehandler for Gzip compressed data. Makes sure the requests for sending gzipped data are not sent..
	 */
	@Override
	public void dontUseGzipFileHandler() {
		setGzipFileHandler(new DefaultFileHandler());
		dontUseGzipFileHandler = true;
	}

	@Override
	public boolean isAborted() {
		return abort;
	}

	/**
	 * Ensures, that the connection is open.
	 * 
	 * @throws AuthenticationException
	 *             if it wasn't possible to connect
	 */
	@Override
	public void ensureConnection() throws AuthenticationException {
		BugLog.getInstance().assertNotNull(getConnection());

		if (getConnection().isOpen()) {
			return;
		}

		// #69689 detect silent servers, possibly caused by proxy errors
		final Throwable ex[] = new Throwable[1];
		final boolean opened[] = new boolean[] { false };
		Runnable probe = new Runnable() {
			@Override
			public void run() {
				try {
					getConnection().open();
					synchronized (opened) {
						opened[0] = true;
					}
				} catch (Throwable e) {
					synchronized (ex) {
						ex[0] = e;
					}
				}
			}
		};

		Thread probeThread = new Thread(probe, "CVS Server Probe"); // NOI18N
		probeThread.start();
		try {

			probeThread.join(60 * 1000); // 1 min

			Throwable wasEx;
			synchronized (ex) {
				wasEx = ex[0];
			}
			if (wasEx != null) {
				if (wasEx instanceof CommandAbortedException) {
					// User cancelled
					abort();
					return;
				} else if (wasEx instanceof AuthenticationException) {
					throw (AuthenticationException) wasEx;
				} else if (wasEx instanceof RuntimeException) {
					throw (RuntimeException) wasEx;
				} else if (wasEx instanceof Error) {
					throw (Error) wasEx;
				} else {
					assert false : wasEx;
				}
			}

			boolean wasOpened;
			synchronized (opened) {
				wasOpened = opened[0];
			}
			if (wasOpened == false) {
				probeThread.interrupt();
				throw new AuthenticationException("Timeout, no response from server.", "Timeout, no response from server.");
			}

		} catch (InterruptedException e) {

			// User cancelled
			probeThread.interrupt();
			abort();
		}
	}

	/**
	 * Process all the requests. The connection must have been opened and set first.
	 * 
	 * @param requests
	 *            the requets to process
	 */
	@Override
	public void processRequests(List requests) throws IOException, UnconfiguredRequestException, ResponseException, CommandAbortedException {

		if (requests == null || requests.size() == 0) {
			throw new IllegalArgumentException("[processRequests] requests " + // NOI18N
					"was either null or empty."); // NOI18N
		}

		if (abort) {
			throw new CommandAbortedException("Aborted during request processing", // NOI18N
					CommandException.getLocalMessage("Client.commandAborted", null)); // NOI18N
		}

		loggedDataInputStream = null;
		loggedDataOutputStream = null;

		// send the initialisation requests if we are handling the first
		// command
		boolean filterRootRequest = true;
		if (isFirstCommand()) {
			setIsFirstCommand(false);
			int pos = 0;
			if (!initialRequestsSent) {
				pos = fillInitialRequests(requests);
				initialRequestsSent = true;
				filterRootRequest = false;
			}
			if (globalOptions != null) {
				// sends the global options that are to be sent to server (-q, -Q, -t, -n, l)
				for (Iterator it = globalOptions.createRequestList().iterator(); it.hasNext();) {
					Request request = (Request) it.next();
					requests.add(pos++, request);
				}

				if (globalOptions.isUseGzip() && globalOptions.getCompressionLevel() != 0) {
					requests.add(pos++, new GzipFileContentsRequest(globalOptions.getCompressionLevel()));
				}
			}
		} else if (printConnectionReuseWarning) {
			if (System.getProperty("javacvs.multiple_commands_warning") == null) { // NOI18N
				System.err.println("WARNING TO DEVELOPERS:"); // NOI18N
				System.err
						.println("Please be warned that attempting to reuse one open connection for more commands is not supported by cvs servers very well."); // NOI18N
				System.err.println("You are advised to open a new Connection each time."); // NOI18N
				System.err
						.println("If you still want to proceed, please do: System.setProperty(\"javacvs.multiple_commands_warning\", \"false\")"); // NOI18N
				System.err.println("That will disable this message."); // NOI18N
			}
		}

		if (!ALLOWED_CONNECTION_REUSE_REQUESTS.contains(requests.get(requests.size() - 1).getClass())) {
			printConnectionReuseWarning = true;
		}

		final boolean fireEnhancedEvents = getEventManager().isFireEnhancedEventSet();
		int fileDetailRequestCount = 0;

		if (fireEnhancedEvents) {
			for (Iterator it = requests.iterator(); it.hasNext();) {
				Request request = (Request) it.next();

				FileDetails fileDetails = request.getFileForTransmission();
				if (fileDetails != null && fileDetails.getFile().exists()) {
					fileDetailRequestCount++;
				}
			}
			CVSEvent event = new EnhancedMessageEvent(this, EnhancedMessageEvent.REQUESTS_COUNT, new Integer(fileDetailRequestCount));
			getEventManager().fireCVSEvent(event);
		}

		LoggedDataOutputStream dos = connection.getOutputStream();
		loggedDataOutputStream = dos;

		// this list stores stream modification requests, each to be called
		// to modify the input stream the next time we need to process a
		// response
		List streamModifierRequests = new LinkedList();

		// sending files does not seem to allow compression
		transmitFileHandler = getUncompressedFileHandler();

		for (Iterator it = requests.iterator(); it.hasNext();) {
			if (abort) {
				throw new CommandAbortedException("Aborted during request processing", // NOI18N
						CommandException.getLocalMessage("Client.commandAborted", null)); // NOI18N
			}

			final Request request = (Request) it.next();

			if (request instanceof GzipFileContentsRequest) {
				if (dontUseGzipFileHandler) {
					stderr.println("Warning: The server is not supporting gzip-file-contents request, no compression is used.");
					continue;
				}
			}

			// skip the root request if already sent
			if (request instanceof RootRequest) {
				if (filterRootRequest) {
					continue;
				} else { // Even if we should not filter the RootRequest now, we must filter all successive RootRequests
					filterRootRequest = true;
				}
			}
			// send request to server
			String requestString = request.getRequestString();
			dos.writeBytes(requestString);

			// we must modify the outputstream now, but defer modification
			// of the inputstream until we are about to read a response.
			// This is because some modifiers (e.g. gzip) read the header
			// on construction, and obviously no header is present when
			// no response has been sent
			request.modifyOutputStream(connection);
			if (request.modifiesInputStream()) {
				streamModifierRequests.add(request);
			}
			dos = connection.getOutputStream();

			FileDetails fileDetails = request.getFileForTransmission();
			if (fileDetails != null) {
				final File file = fileDetails.getFile();
				// only transmit the file if it exists! When committing
				// a remove request you cannot transmit the file
				if (file.exists()) {
					Logger.logOutput(new String("<Sending file: " + // NOI18N
							file.getAbsolutePath() + ">\n").getBytes("utf8")); // NOI18N

					if (fireEnhancedEvents) {
						CVSEvent event = new EnhancedMessageEvent(this, EnhancedMessageEvent.FILE_SENDING, file);
						getEventManager().fireCVSEvent(event);

						fileDetailRequestCount--;
					}

					if (fileDetails.isBinary()) {
						transmitFileHandler.transmitBinaryFile(file, dos);
					} else {
						transmitFileHandler.transmitTextFile(file, dos);
					}

					if (fireEnhancedEvents && fileDetailRequestCount == 0) {
						CVSEvent event = new EnhancedMessageEvent(this, EnhancedMessageEvent.REQUESTS_SENT, "Ok"); // NOI18N
						getEventManager().fireCVSEvent(event);
					}
				}
			}
			if (request.isResponseExpected()) {
				dos.flush();

				// now perform the deferred modification of the input stream
				Iterator modifiers = streamModifierRequests.iterator();
				while (modifiers.hasNext()) {
					System.err.println("Modifying the inputstream..."); // NOI18N
					final Request smRequest = (Request) modifiers.next();
					System.err.println("Request is a: " + // NOI18N
							smRequest.getClass().getName());
					smRequest.modifyInputStream(connection);
				}
				streamModifierRequests.clear();

				handleResponse();
			}
		}
		dos.flush();

		transmitFileHandler = null;
	}

	private ResponseFactory getResponseFactory() {
		if (responseFactory == null) {
			responseFactory = new ResponseFactory();
		}
		return responseFactory;
	}

	/**
	 * Handle the response from a request.
	 * 
	 * @throws ResponseException
	 *             if there is a problem reading the response
	 */
	private void handleResponse() throws ResponseException, CommandAbortedException {
		try {
			LoggedDataInputStream dis = connection.getInputStream();
			loggedDataInputStream = dis;

			int ch = -1;
			try {
				ch = dis.read();
			} catch (InterruptedIOException ex) {
				abort();
			}

			while (!abort && ch != -1) {
				StringBuffer responseNameBuffer = new StringBuffer();
				// read in the response name
				while (ch != -1 && (char) ch != '\n' && (char) ch != ' ') {
					responseNameBuffer.append((char) ch);
					try {
						ch = dis.read();
					} catch (InterruptedIOException ex) {
						abort();
						break;
					}
				}

				String responseString = responseNameBuffer.toString();
				Response response = getResponseFactory().createResponse(responseString);
				// Logger.logInput(new String("<" + responseString + " processing start>\n").getBytes()); // NOI18N
				response.process(dis, this);
				boolean terminal = response.isTerminalResponse();

				// handle SpecialResponses
				if (terminal && response instanceof ErrorMessageResponse) {
					ErrorMessageResponse errorResponce = (ErrorMessageResponse) response;
					String errMsg = errorResponce.getMessage();
					throw new CommandAbortedException(errMsg, errMsg);
				}
				// Logger.logInput(new String("<" + responseString + " processed " + terminal + ">\n").getBytes()); // NOI18N
				if (terminal || abort) {
					break;
				}

				try {
					ch = dis.read();
				} catch (InterruptedIOException ex) {
					abort();
					break;
				}
			}

			if (abort) {
				String localMsg = CommandException.getLocalMessage("Client.commandAborted", null); // NOI18N
				throw new CommandAbortedException("Aborted during request processing", localMsg); // NOI18N
			}
		} catch (EOFException ex) {
			throw new ResponseException(ex, CommandException.getLocalMessage("CommandException.EndOfFile", null)); // NOI18N
		} catch (IOException ex) {
			throw new ResponseException(ex);
		}
	}

	/**
	 * Execute a command. Do not forget to initialize the CVS Root on globalOptions first! Example: <code>
	 *   GlobalOptions options = new GlobalOptions();
	 *   options.setCVSRoot(":pserver:"+userName+"@"+hostName+":"+cvsRoot);
	 * </code>
	 * 
	 * @param command
	 *            the command to execute
	 * @param options
	 *            the global options to use for executing the command
	 * @throws CommandException
	 *             if an error occurs when executing the command
	 * @throws CommandAbortedException
	 *             if the command is aborted
	 */
	public boolean executeCommand(Command command, GlobalOptions globalOptions) throws CommandException, CommandAbortedException,
			AuthenticationException {
		BugLog.getInstance().assertNotNull(command);
		BugLog.getInstance().assertNotNull(globalOptions);

		this.globalOptions = globalOptions;

		getUncompressedFileHandler().setGlobalOptions(globalOptions);
		getGzipFileHandler().setGlobalOptions(globalOptions);

		try {
			eventManager.addCVSListener(command);
			command.execute(this, eventManager);
		} finally {
			eventManager.removeCVSListener(command);
		}
		return !command.hasFailed();
	}

	/**
	 * Counts {@link #processRequests(java.util.List)}. send and received bytes.
	 * 
	 * @thread it assumes that client is not run in paralel.
	 */
	public long getCounter() {
		long ret = 0;
		if (loggedDataInputStream != null) {
			ret += loggedDataInputStream.getCounter();
		}
		if (loggedDataOutputStream != null) {
			ret += loggedDataOutputStream.getCounter();
		}
		return ret;
	}

	/**
	 * Convert a <i>pathname</i> in the CVS sense (see 5.10 in the protocol document) into a local absolute pathname for the file.
	 * 
	 * @param localDirectory
	 *            the name of the local directory, relative to the directory in which the command was given
	 * @param repository
	 *            the full repository name for the file
	 */
	@Override
	public String convertPathname(String localDirectory, String repository) {
		int lastIndexOfSlash = repository.lastIndexOf('/');
		String filename = repository.substring(lastIndexOfSlash + 1);

		if (localDirectory.startsWith("./")) { // NOI18N
			// remove the dot
			localDirectory = localDirectory.substring(1);
		}
		if (localDirectory.startsWith("/")) { // NOI18N
			// remove the leading slash
			localDirectory = localDirectory.substring(1);
		}
		// note that the localDirectory ends in a slash
		return getLocalPath() + '/' + localDirectory + filename;
	}

	/**
	 * Get the repository path from the connection.
	 * 
	 * @return the repository path, e.g. /home/bob/cvs. Delegated to the Connection in this case
	 * @see Connection#getRepository()
	 */
	@Override
	public String getRepository() {
		return connection.getRepository();
	}

	/**
	 * Create or update the administration files for a particular file. This will create the CVS directory if necessary, and the Root and
	 * Repository files if necessary. It will also update the Entries file with the new entry
	 * 
	 * @param localDirectory
	 *            the local directory, relative to the directory in which the command was given, where the file in question lives
	 * @param repositoryPath
	 *            the path of the file in the repository, in absolute form.
	 * @param entry
	 *            the entry object for that file
	 */
	@Override
	public void updateAdminData(String localDirectory, String repositoryPath, Entry entry) throws IOException {
		final String absolutePath = localPath + '/' + localDirectory;
		if (repositoryPath.startsWith(getRepository())) {
			repositoryPath = repositoryPath.substring(getRepository().length() + 1);
		} else {
			if (warned == false) {
				String warning = "#65188 warning C/S protocol error (section 5.10). It's regurarly observed with cvs 1.12.xx servers.\n"; // NOI18N
				warning += "  unexpected pathname=" + repositoryPath + " missing root prefix=" + getRepository() + "\n"; // NOI18N
				warning += "  relaxing, but who knows all consequences...."; // NOI18N
				System.err.println(warning);
				warned = true;
			}
		}

		adminHandler.updateAdminData(absolutePath, repositoryPath, entry, globalOptions);
	}

	/**
	 * Set the modified date of the next file to be written. The next call to write<Type>File will use this date.
	 * 
	 * @param modifiedDate
	 *            the date the file should be marked as modified
	 */
	@Override
	public void setNextFileDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Get the modified date for the next file.
	 * 
	 * @return the date and then null the instance variable.
	 */
	@Override
	public Date getNextFileDate() {
		//
		// We null the instance variable so that future calls will not
		// retrieve a date specified for a previous file
		//
		Date copy = modifiedDate;
		modifiedDate = null;
		return copy;
	}

	/**
	 * Get the Entry for the specified file, if one exists.
	 * 
	 * @param f
	 *            the file
	 * @throws IOException
	 *             if the Entries file cannot be read
	 */
	@Override
	public Entry getEntry(File f) throws IOException {
		return adminHandler.getEntry(f);
	}

	/**
	 * Get the entries for a specified directory.
	 * 
	 * @param directory
	 *            the directory for which to get the entries
	 * @return an iterator of Entry objects
	 */
	@Override
	public Iterator getEntries(File directory) throws IOException {
		return adminHandler.getEntries(directory);
	}

	@Override
	public boolean exists(File file) {
		return adminHandler.exists(file);
	}

	/**
	 * Get the repository path for a given directory, for example in the directory /home/project/foo/bar, the repository directory might be
	 * /usr/cvs/foo/bar. The repository directory is commonly stored in the file
	 * 
	 * <pre>
	 * Repository
	 * </pre>
	 * 
	 * in the CVS directory on the client (this is the case in the standard CVS command-line tool).
	 * 
	 * If no
	 * 
	 * <pre>
	 * CVS / Repository
	 * </pre>
	 * 
	 * file was found, the specified directory, the localpath are used to "guess" the repository path.
	 * 
	 * @param directory
	 *            the directory
	 */
	@Override
	public String getRepositoryForDirectory(String directory) throws IOException {
		try {
			String repository = adminHandler.getRepositoryForDirectory(directory, getRepository());
			return repository;
		} catch (IOException ex) {
			// an IOException is thrown, if the adminHandler can't detect the repository
			// by reading the CVS/Repository file, e.g. when checking out into a new directory
			try {
				directory = new File(directory).getCanonicalPath();
			} catch (IOException ioex) {
			}
			directory = directory.replace('\\', '/');
			while (directory.endsWith("/")) { // NOI18N
				directory = directory.substring(0, directory.length() - 1);
			}

			// must also canonicalize 'localPath' to be in sync with 'directory'
			String localPathCanonical = getLocalPath();
			try {
				localPathCanonical = new File(getLocalPath()).getCanonicalPath();
			} catch (IOException ioex) {
			}
			localPathCanonical = localPathCanonical.replace('\\', '/');
			while (localPathCanonical.endsWith("/")) { // NOI18N
				localPathCanonical = localPathCanonical.substring(0, localPathCanonical.length() - 1);
			}
			int localPathLength = localPathCanonical.length();

			String repository;
			if (directory.length() >= localPathLength) {
				repository = getRepository() + directory.substring(localPathLength);
			} else { // Asking for some folder upon the local working path
				repository = getRepository();
			}
			return repository;
		}
	}

	@Override
	public String getRepositoryForDirectory(File directory) throws IOException {
		return adminHandler.getRepositoryForDirectory(directory.getAbsolutePath(), getRepository());
	}

	/**
	 * Set the Entry for the specified file.
	 * 
	 * @param file
	 *            the file
	 * @param entry
	 *            the new entry
	 * @throws IOException
	 *             if an error occurs writing the details
	 */
	@Override
	public void setEntry(File file, Entry entry) throws IOException {
		adminHandler.setEntry(file, entry);
	}

	/**
	 * Remove the Entry for the specified file.
	 * 
	 * @param file
	 *            the file whose entry is to be removed
	 * @throws IOException
	 *             if an error occurs writing the Entries file
	 */
	@Override
	public void removeEntry(File file) throws IOException {
		adminHandler.removeEntry(file);
	}

	/**
	 * Remove the specified file from the local disk. If the file does not exist, the operation does nothing.
	 * 
	 * @param pathname
	 *            the full path to the file to remove
	 * @throws IOException
	 *             if an IO error occurs while removing the file
	 */
	@Override
	public void removeLocalFile(String pathname) throws IOException {
		transmitFileHandler.removeLocalFile(pathname);
	}

	/**
	 * Removes the specified file determined by pathName and repositoryName. In this implementation the filename from repositoryPath is
	 * added to the localpath (which doesn't have the filename in it) and that file is deleted.
	 */
	@Override
	public void removeLocalFile(String pathName, String repositoryName) throws IOException {
		int ind = repositoryName.lastIndexOf('/');
		if (ind <= 0) {
			return;
		}

		String fileName = repositoryName.substring(ind + 1);
		String localFile = pathName + fileName;
		File fileToDelete = new File(getLocalPath(), localFile);
		removeLocalFile(fileToDelete.getAbsolutePath());
		removeEntry(fileToDelete);
	}

	/**
	 * Rename the local file.
	 * 
	 * @param pathname
	 *            the full path to the file to rename
	 * @param newName
	 *            the new name of the file (not the full path)
	 * @throws IOException
	 *             if an IO error occurs while renaming the file
	 */
	@Override
	public void renameLocalFile(String pathname, String newName) throws IOException {
		transmitFileHandler.renameLocalFile(pathname, newName);
	}

	/**
	 * Get the CVS event manager. This is generally called by response handlers that want to fire events.
	 * 
	 * @return the eventManager
	 */
	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Get the global options that are set to this client. Individual commands can get the global options via this method.
	 */
	@Override
	public GlobalOptions getGlobalOptions() {
		return globalOptions;
	}

	/**
	 * Call this method to abort the current command. The command will be aborted at the next suitable time
	 */
	public synchronized void abort() {
		abort = true;
	}

	/**
	 * Get all the files contained within a given directory that are <b>known to CVS</b>.
	 * 
	 * @param directory
	 *            the directory to look in
	 * @return a set of all files.
	 */
	@Override
	public Set getAllFiles(File directory) throws IOException {
		return adminHandler.getAllFiles(directory);
	}

	@Override
	public void setIgnoreFileFilter(IgnoreFileFilter ignoreFileFilter) {
		this.ignoreFileFilter = ignoreFileFilter;
	}

	@Override
	public IgnoreFileFilter getIgnoreFileFilter() {
		return ignoreFileFilter;
	}

	@Override
	public boolean shouldBeIgnored(File directory, String noneCvsFile) {
		if (ignoreFileFilter != null) {
			return ignoreFileFilter.shouldBeIgnored(directory, noneCvsFile);
		}
		return false;
	}

	/**
	 * Checks for presence of CVS/Tag file and returns it's value.
	 * 
	 * @return the value of CVS/Tag file for the specified directory null if file doesn't exist
	 */
	@Override
	public String getStickyTagForDirectory(File directory) {
		return adminHandler.getStickyTagForDirectory(directory);
	}

	/**
	 * This method is called when a response for the ValidRequests request is received.
	 * 
	 * @param requests
	 *            A List of requests that is valid for this CVS server separated by spaces.
	 */
	@Override
	public void setValidRequests(String requests) {
		// We need to tokenize the requests and add it to our map

		StringTokenizer tokenizer = new StringTokenizer(requests);
		String token;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			// we just add an object with the corresponding request
			// as the key.
			validRequests.put(token, this);
		}

	}

	private int fillInitialRequests(List requests) {
		int pos = 0;
		requests.add(pos++, new RootRequest(getRepository()));
		requests.add(pos++, new UseUnchangedRequest());
		requests.add(pos++, new ValidRequestsRequest());
		requests.add(pos++, new ValidResponsesRequest());
		return pos;
	}

	/**
	 * This method is called by WrapperSendResponse for each wrapper setting sent back by the CVS server
	 * 
	 * @param pattern
	 *            A StringPattern indicating the pattern for which the wrapper applies
	 * @param option
	 *            A KeywordSubstituionOption corresponding to the setting
	 */
	@Override
	public void addWrapper(StringPattern pattern, KeywordSubstitutionOptions option) {
		if (wrappersMap == null) {
			throw new IllegalArgumentException("This method should be called " + "by WrapperSendResponse only.");
		}
		wrappersMap.put(pattern, option);
	}

	/**
	 * Returns the wrappers map associated with the CVS server The map is valid only after the connection is established
	 */
	@Override
	public Map getWrappersMap() throws CommandException {
		if (wrappersMap == null) {
			wrappersMap = new HashMap();
			ArrayList requests = new ArrayList();
			requests.add(new WrapperSendRequest());
			boolean isFirst = isFirstCommand();
			try {
				processRequests(requests);
			} catch (Exception ex) {
				throw new CommandException(ex, "An error during obtaining server wrappers.");
			} finally {
				// Do not alter isFirstCommand property
				setIsFirstCommand(isFirst);
			}
			wrappersMap = Collections.unmodifiableMap(wrappersMap);
		}
		return wrappersMap;
	}

	/**
	 * Factory for creating clients.
	 */
	public static interface Factory {

		/**
		 * Creates new client instance. Never null. It uses fresh connection.
		 */
		Client createClient();
	}
}
