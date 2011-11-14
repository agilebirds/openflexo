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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.command.importcmd.ImportCommand;
import org.netbeans.lib.cvsclient.command.status.StatusCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.FileInfoEvent;
import org.netbeans.lib.cvsclient.util.DefaultIgnoreFileFilter;
import org.netbeans.lib.cvsclient.util.IgnoreFileFilter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.fps.CVSFile.RevisionRetrieverListener;
import org.openflexo.fps.action.CommitFiles;
import org.openflexo.fps.dm.CVSStatusChanged;
import org.openflexo.fps.dm.CVSStructureUpdated;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoRunnable;

public class SharedProject extends CVSAbstractFile implements CVSContainer {

	private static final Logger logger = Logger.getLogger(SharedProject.class.getPackage().getName());

	private static final int THREAD_POOL_SIZE = 10;

	private CVSRepository _cvsRepository;
	private CVSModule _module;
	private File _localDirectory;
	private String _localName;
	private FlexoXMLMappings _mappings; // used by xml diff algo
	// private GlobalOptions _globalOptions;

	private StandardAdminHandler _adminHandler;
	private CVSConsole _consoleHandler;
	private CVSListener _cvsHandler;

	private ThreadPoolExecutor connectionThreadPool;

	private SharedProject(CVSRepository cvsRepository, CVSModule module, File localDirectory, final String localName) {
		super(new File(localDirectory, localName), null);
		_sharedProject = this;
		_directories = new Vector<CVSDirectory>();
		_files = new Vector<CVSFile>();

		_cvsRepository = cvsRepository;
		_module = module;
		_localDirectory = localDirectory;
		_localName = localName;
		// _globalOptions = new GlobalOptions();
		_adminHandler = new StandardAdminHandler();
		_consoleHandler = CVSConsole.getCVSConsole();
		_cvsHandler = new CVSListener(this);
		connectionThreadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

					private int threadCount = 0;

					@Override
					public Thread newThread(Runnable r) {
						threadCount++;
						Thread t = null;
						if (r instanceof FlexoRunnable) {
							t = new Thread(r, ((FlexoRunnable) r).getName());
						} else {
							t = new Thread(r, "Thread-" + threadCount + " in pool for " + localName);
						}
						if (t.isDaemon()) {
							t.setDaemon(false);
						}
						if (t.getPriority() != Thread.NORM_PRIORITY) {
							t.setPriority(Thread.NORM_PRIORITY);
						}
						return t;
					}
				});
	}

	public File getModuleDirectory() {
		return getFile();
	}

	public static final transient String CVS_REPOSITORY_LOCATION_FILE = ".cvsrepository";

	/**
	 * Create an instance of SharedProject by checkouting a CVSModule
	 * 
	 * @param cvsRepository
	 * @param module
	 * @param localDirectory
	 * @param localName
	 * @return
	 * @throws IOException
	 * @throws CommandException
	 * @throws AuthenticationException
	 */
	public static SharedProject checkoutProject(CVSRepository cvsRepository, CVSModule module, File localDirectory, String localName,
			CVSAdapter listener) throws IOException, CommandException, AuthenticationException {
		SharedProject returned = new SharedProject(cvsRepository, module, localDirectory, localName);
		CVSConnection connection = returned.openConnection();
		connection.getClient().getEventManager().addCVSListener(listener);

		CheckoutCommand command = new CheckoutCommand();
		command.setModule(module.getFullQualifiedModuleName());
		command.setCheckoutDirectory(localName/*returned.getModuleDirectory().getCanonicalPath()*/);
		try {
			connection.executeCommand(command);
		} catch (CommandAbortedException e) {
			throw e;
		} catch (CommandException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		}

		/*if (module.isSubModule()) {
			// We have here to handle move
			File targetDirectory = new File(localDirectory,module.getModuleName());
			File checkoutDirectory = new File(localDirectory,module.getFullQualifiedModuleName());
			logger.info("Rename from "+checkoutDirectory+" to "+targetDirectory);
			if (!checkoutDirectory.renameTo(targetDirectory)) {
				logger.warning("Could to rename from "+checkoutDirectory+" to "+targetDirectory);
			}
			File originalCheckoutDirectory = (new File(localDirectory,module.getFullQualifiedModuleName())).getParentFile();
			logger.info("Delete "+originalCheckoutDirectory);
			if (!FileUtils.recursiveDeleteFile(originalCheckoutDirectory)) {
				logger.warning("Could to delete "+originalCheckoutDirectory);
			}
		}*/

		File cvsRepositoryFile = new File(returned.getModuleDirectory(), CVS_REPOSITORY_LOCATION_FILE);
		cvsRepository.saveCVSRepositoryLocation(cvsRepositoryFile);

		logger.info("Checkout project DONE");
		returned.rebuildStructureFromFS();
		return returned;
	}

	public static SharedProject openProject(CVSRepositoryList repositoryList, File projectDirectory, CVSRepository repository,
			FlexoEditor editor) {
		if (projectDirectory == null || !projectDirectory.exists()) {
			return null;
		}

		boolean isFound = false;
		for (CVSRepository rep : repositoryList.getCVSRepositories()) {
			if (rep.equals(repository)) {
				isFound = true;
				repository = rep;
			}
		}

		if (!isFound) {
			repositoryList.addToCVSRepositories(repository);
			repository.getCVSExplorer(null).explore();
			// SwingUtilities.invokeLater(new CVSRepositoryList.RetrieveModuleRunnable(repository,editor));
		}

		CVSModule module = null;
		;
		File cvsRepFile = new File(projectDirectory, "CVS/Repository");
		if (cvsRepFile.exists()) {
			String moduleName;
			try {
				moduleName = FileUtils.fileContents(cvsRepFile).trim();
				module = repository.getModuleNamed(moduleName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (module == null) {
			logger.warning("Could not find " + cvsRepFile.getAbsolutePath());
			module = repository.getModuleNamed(projectDirectory.getName());
		}

		SharedProject returned = new SharedProject(repository, module, projectDirectory.getParentFile(), projectDirectory.getName());

		returned.rebuildStructureFromFS();
		logger.info("Open project DONE");

		File cvsRepositoryFile = new File(returned.getModuleDirectory(), CVS_REPOSITORY_LOCATION_FILE);
		repository.saveCVSRepositoryLocation(cvsRepositoryFile);

		return returned;
	}

	public static SharedProject openProject(CVSRepositoryList repositoryList, File projectDirectory, FlexoEditor editor) {
		if (projectDirectory == null || !projectDirectory.exists()) {
			return null;
		}

		File cvsRepositoryLocation = new File(projectDirectory, SharedProject.CVS_REPOSITORY_LOCATION_FILE);
		CVSRepository relatedCVSRepository = new CVSRepository(cvsRepositoryLocation);

		return openProject(repositoryList, projectDirectory, relatedCVSRepository, editor);
	}

	public static SharedProject shareProject(CVSRepositoryList repositoryList, File projectDirectory, CVSRepository repository,
			String moduleName, // Full qualified
			String vendorTag, String releaseTag, String logMessage, FlexoEditor editor) throws IOException, CommandException,
			AuthenticationException {
		if (projectDirectory == null || !projectDirectory.exists()) {
			return null;
		}
		long start = System.currentTimeMillis();
		boolean isFound = false;
		for (CVSRepository rep : repositoryList.getCVSRepositories()) {
			if (rep.equals(repository)) {
				isFound = true;
				repository = rep;
			}
		}

		if (!isFound) {
			repositoryList.addToCVSRepositories(repository);
			repository.getCVSExplorer(null).explore();
		}

		// CVSModule module = repository.getModuleNamed(projectDirectory.getName());
		logger.info("Module name: " + moduleName);
		CVSModule module = repository.getModuleNamed(moduleName);
		logger.info("Module: " + module.getModuleName() + " " + module.getFullQualifiedModuleName());

		SharedProject returned = new SharedProject(repository, module, projectDirectory.getParentFile(), projectDirectory.getName());

		CVSConnection connection = returned.openConnection();

		ImportCommand command = new ImportCommand();
		command.setModule(module.getFullQualifiedModuleName());
		command.setLogMessage(logMessage);
		command.setVendorTag(vendorTag);
		command.setReleaseTag(releaseTag);
		for (String binaryFilePattern : CVSConstants.binaryFilesPatterns) {
			command.addIgnoredFile(binaryFilePattern);
		}
		connection.getClient().setLocalPath(projectDirectory.getCanonicalPath());

		try {
			connection.executeCommand(command);
		} catch (CommandAbortedException e) {
			throw e;
		} catch (CommandException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		}

		// Now, do a checkout in a temp directory in order to get CVS tree
		// Create an empty temporary folder
		File aTempFile = File.createTempFile("TemporaryCheckout", "").getCanonicalFile();
		String aTempFileName = aTempFile.getAbsolutePath();
		aTempFile.delete();
		File aTempDir = new File(aTempFileName);
		aTempDir.mkdir();
		CVSConnection coConnection = returned.openConnection();
		CheckoutCommand coCommand = new CheckoutCommand();
		coCommand.setModule(module.getFullQualifiedModuleName());
		coConnection.getClient().setLocalPath(aTempDir.getAbsolutePath());
		try {
			// No need to notify file adding (temporary folder)
			returned.getCVSHandler().disableFileAddingNotification();
			coConnection.executeCommand(coCommand);
			returned.getCVSHandler().enableFileAddingNotification();
		} catch (CommandAbortedException e) {
			throw e;
		} catch (CommandException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		}
		// And copy CVS folders to initial
		copyCVSFolders(new File(aTempDir, module.getFullQualifiedModuleName()), returned.getModuleDirectory());
		// Dont forget to delete temp checkout
		FileUtils.recursiveDeleteFile(aTempDir);

		returned.rebuildStructureFromFS();

		File cvsRepositoryFile = new File(returned.getModuleDirectory(), CVS_REPOSITORY_LOCATION_FILE);
		repository.saveCVSRepositoryLocation(cvsRepositoryFile);

		logger.info("Now lets handle binary files");

		Vector<FPSObject> binaryFiles = new Vector<FPSObject>();
		for (CVSFile f : returned.getAllCVSFiles()) {
			if (f.isBinary()) {
				binaryFiles.add(f);
			}
		}

		if (binaryFiles.size() > 0) {
			CommitFiles commitBinaryFiles = CommitFiles.actionType.makeNewAction(null, binaryFiles, editor);
			commitBinaryFiles.setCommitMessage(logMessage);
			commitBinaryFiles.doAction();
		}
		long end = System.currentTimeMillis();
		logger.info("Project sharing DONE in " + ((end - start) / 1000) + " seconds.");

		return returned;
	}

	public void shutdownThreadPool() {
		if (connectionThreadPool != null) {
			if (!connectionThreadPool.isShutdown()) {
				connectionThreadPool.shutdownNow();
			}
			connectionThreadPool = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		shutdownThreadPool();
		super.finalize();
	}

	public static void copyCVSFolders(File source, File destination) {
		// logger.info("copyCVSFolders() from "+source.getAbsolutePath()+" to "+destination.getAbsolutePath());
		File sourceCVSDir = new File(source, "CVS");
		if (sourceCVSDir.exists()) {
			File destCVSDir = new File(destination, "CVS");
			try {
				FileUtils.copyContentDirToDir(sourceCVSDir, destCVSDir);
			} catch (IOException e) {
				logger.warning("Unexpected IOException: " + e.getMessage());
				e.printStackTrace();
			}
		}
		File[] allFiles = source.listFiles(new CVSNotIgnoredFileFilter(source));
		if (allFiles == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("listFiles() returned null for path: " + source.getAbsolutePath());
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("source exists? " + source.exists() + "\n" + "source is file? " + source.isFile() + "\n"
						+ "source is directory? " + source.isDirectory());
			}

		} else {
			for (File f : allFiles) {
				if (f.isDirectory()) {
					copyCVSFolders(f, new File(destination, f.getName()));
				}
			}
		}
	}

	protected CVSConnection openConnection() throws IOException {
		return CVSConnection.initCVSConnection(this);
	}

	public CVSRepository getCVSRepository() {
		return _cvsRepository;
	}

	public void setCVSRepository(CVSRepository cvsRepository) {
		_cvsRepository = cvsRepository;
	}

	/*public GlobalOptions getGlobalOptions()
	{
		return _globalOptions;
	}*/

	public File getLocalDirectory() {
		return _localDirectory;
	}

	public void setLocalDirectory(File localDirectory) {
		_localDirectory = localDirectory;
	}

	public String getLocalName() {
		return _localName;
	}

	public CVSModule getCVSModule() {
		return _module;
	}

	public void setCVSModule(CVSModule module) {
		_module = module;
	}

	public StandardAdminHandler getAdminHandler() {
		return _adminHandler;
	}

	public CVSConsole getConsoleHandler() {
		return _consoleHandler;
	}

	public CVSListener getCVSHandler() {
		return _cvsHandler;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.FPS.SHARED_PROJECT_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "shared_project";
	}

	private void rebuildStructureFromFS() {
		clear();
		try {
			rebuildStructureFor(this, getModuleDirectory());
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Error while building CVS structure: " + e.getMessage());
		}
		// Check for changes in local version
		checkModifiedFiles(this, getModuleDirectory());
		checkNewFilesAndDirectories(this, getModuleDirectory());
		checkDeletedFiles(this, getModuleDirectory());
		checkIgnoredFiles(this, getModuleDirectory());
	}

	private void rebuildStructureFor(CVSContainer container, File directory) throws IOException {
		Iterator allEntries = getAdminHandler().getEntries(directory);

		// logger.info("Rebuild structure for "+directory.getAbsolutePath()+" for "+container);

		for (Iterator it = allEntries; it.hasNext();) {
			Entry entry = (Entry) it.next();
			// logger.info("Entry: "+entry.toString());
			File file = new File(directory, entry.getName());
			if (entry.isDirectory()) {
				CVSDirectory newDirectory = new CVSDirectory(file, entry, this);
				container.addToDirectories(newDirectory);
				rebuildStructureFor(newDirectory, file);
			} else {
				CVSFile newFile = new CVSFile(file, entry, this);
				container.addToFiles(newFile);
			}
		}
		setChanged();
		notifyObservers(new CVSStructureUpdated(this));

	}

	private Vector<CVSDirectory> _directories;

	@Override
	public Vector<CVSDirectory> getDirectories() {
		return _directories;
	}

	@Override
	public void setDirectories(Vector<CVSDirectory> directories) {
		_directories = directories;
	}

	@Override
	public void addToDirectories(CVSDirectory aDirectory) {
		_directories.add(aDirectory);
		aDirectory.setContainer(this);
	}

	@Override
	public void removeFromDirectories(CVSDirectory aDirectory) {
		_directories.remove(aDirectory);
		aDirectory.setContainer(null);
	}

	private Vector<CVSFile> _files;

	@Override
	public Vector<CVSFile> getFiles() {
		return _files;
	}

	@Override
	public void setFiles(Vector<CVSFile> files) {
		_files = files;
	}

	@Override
	public void addToFiles(CVSFile aFile) {
		_files.add(aFile);
		aFile.setContainer(this);
	}

	@Override
	public void removeFromFiles(CVSFile aFile) {
		_files.remove(aFile);
		aFile.setContainer(null);
	}

	public Vector<CVSFile> getAllCVSFiles() {
		Vector<CVSFile> returned = new Vector<CVSFile>();
		appendAllCVSFiles(returned, this);
		return returned;
	}

	private void appendAllCVSFiles(Vector<CVSFile> files, CVSContainer container) {
		for (CVSFile f : container.getFiles()) {
			files.add(f);
		}
		for (CVSDirectory d : container.getDirectories()) {
			appendAllCVSFiles(files, d);
		}
	}

	@Override
	public boolean isRegistered(File aFile) {
		for (CVSFile f : getFiles()) {
			if (f.getFile().equals(aFile)) {
				return true;
			}
		}
		for (CVSDirectory d : getDirectories()) {
			if (d.getFile().equals(aFile)) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		// TODO remove properly (think about observers)
		for (CVSDirectory d : getDirectories()) {
			d.clear();
		}
		conflictingFiles.clear();
		_directories.clear();
		_files.clear();
		locallyModifiedCount = 0;
		remotelyModifiedCount = 0;
		conflictsCount = 0;
	}

	private static class CVSNotIgnoredFileFilter implements FileFilter {
		private File _directory;
		private IgnoreFileFilter ignoreFileFilter = new DefaultIgnoreFileFilter();

		private CVSNotIgnoredFileFilter(File directory) {
			_directory = directory;
		}

		@Override
		public boolean accept(File file) {
			if (file.getName().equals("CVS")) {
				return false;
			}
			if (file.getName().equals(CVS_REPOSITORY_LOCATION_FILE)) {
				return false;
			}
			return !ignoreFileFilter.shouldBeIgnored(_directory, file.getName());
		}
	}

	private static class CVSIgnoredFileFilter implements FileFilter {
		private File _directory;
		private IgnoreFileFilter ignoreFileFilter = new DefaultIgnoreFileFilter();

		private CVSIgnoredFileFilter(File directory) {
			_directory = directory;
		}

		@Override
		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}
			return ignoreFileFilter.shouldBeIgnored(_directory, file.getName());
		}
	}

	private static void checkNewFilesAndDirectories(CVSContainer container, final File directory) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("checkNewFilesAndDirectories() for " + directory.getAbsolutePath() + " for " + container);
		}

		File[] allUnignoredFiles = directory.listFiles(new CVSNotIgnoredFileFilter(directory));

		for (File f : allUnignoredFiles) {
			if (!container.isRegistered(f)) {
				if (f.isDirectory()) {
					CVSDirectory newDir = new CVSDirectory(f, container.getSharedProject());
					container.addToDirectories(newDir);
				} else {
					CVSFile newFile = new CVSFile(f, container.getSharedProject());
					container.addToFiles(newFile);
					newFile.setStatus(CVSStatus.LocallyAdded);
				}
			}
		}

		for (File f : directory.listFiles(new CVSNotIgnoredFileFilter(directory))) {
			if (f.isDirectory()) {
				CVSDirectory cvsDir = (CVSDirectory) container.getCVSAbstractFile(f);
				if (cvsDir == null) {
					logger.warning("Cannot find cvs dir " + f);
				} else {
					checkNewFilesAndDirectories(cvsDir, f);
				}
			}
		}
	}

	private static void checkDeletedFiles(CVSContainer container, final File directory) {
		// logger.info("checkDeletedFiles() for "+directory.getAbsolutePath()+" for "+container);

		for (CVSFile f : container.getFiles()) {
			if (!f.getFile().exists()) {
				f.setStatus(CVSStatus.LocallyRemoved);
			}
		}

		for (CVSDirectory d : container.getDirectories()) {
			checkDeletedFiles(d, d.getFile());
		}
	}

	private static void checkModifiedFiles(CVSContainer container, final File directory) {
		// logger.info("checkModifiedFiles "+directory.getAbsolutePath());
		for (CVSFile f : container.getFiles()) {
			f.checkLocallyModified();
		}

		for (CVSDirectory d : container.getDirectories()) {
			checkModifiedFiles(d, d.getFile());
		}
	}

	private static void checkIgnoredFiles(CVSContainer container, final File directory) {
		// logger.info("checkNewFilesAndDirectories() for "+directory.getAbsolutePath()+" for "+container);

		File[] allIgnoredFiles = directory.listFiles(new CVSIgnoredFileFilter(directory));

		for (File f : allIgnoredFiles) {
			if (!container.isRegistered(f)) {
				CVSFile newFile = new CVSFile(f, container.getSharedProject());
				newFile.setStatus(CVSStatus.CVSIgnored);
				container.addToFiles(newFile);
			}
		}

		for (File f : directory.listFiles(new CVSNotIgnoredFileFilter(directory))) {
			if (f.isDirectory()) {
				CVSDirectory cvsDir = (CVSDirectory) container.getCVSAbstractFile(f);
				if (cvsDir == null) {
					logger.warning("Cannot find cvs dir " + f);
				} else {
					checkIgnoredFiles(cvsDir, f);
				}
			}
		}
	}

	public void refresh() {
		rebuildStructureFromFS();
	}

	public void synchronizeWithRepository() {
		synchronizeWithRepository(null);
	}

	private boolean _isSynchronizing = false;

	protected boolean isSynchronizing() {
		return _isSynchronizing;
	}

	public void synchronizeWithRepository(final FlexoProgress progress) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Synchronize with repository " + getModuleDirectory().getAbsolutePath());
		}

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("check_changes_on_local_file_system"));
		}
		rebuildStructureFromFS();

		// Check for changes in remote location

		_isSynchronizing = true;

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("check_changes_on_cvs_repository"));
			progress.resetSecondaryProgress(getAllCVSFiles().size());
		}

		getCVSHandler().setReceiveRemoteUpdateRequest(true);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("[" + Thread.currentThread().getName() + "] Perform update command");
		}

		UpdateCommand updateCommand = new UpdateCommand();
		updateCommand.setRecursive(true);
		File[] moduleDir = new File[1];
		moduleDir[0] = getModuleDirectory();
		updateCommand.setFiles(moduleDir);
		GlobalOptions options = new GlobalOptions();
		options.setDoNoChanges(true); // no changes on files '-n'

		try {
			CVSConnection connection = openConnection();
			connection.getClient().getEventManager().addCVSListener(new CVSAdapter() {
				@Override
				public void fileInfoGenerated(FileInfoEvent e) {
					if (progress != null) {
						progress.setSecondaryProgress(FlexoLocalization.localizedForKey("received_info_for") + " "
								+ e.getInfoContainer().getFile().getName());
					}
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("[" + Thread.currentThread().getName() + "] synchronizeWithRepository: Received update info for file "
								+ e.getInfoContainer().getFile() + " " + e.getInfoContainer().getClass().getSimpleName());
					}
				}
			});
			connection.executeCommand(updateCommand, options);
		} catch (CommandAbortedException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		getCVSHandler().setReceiveRemoteUpdateRequest(false);

		// Finally perform a status command

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("[" + Thread.currentThread().getName() + "] Now perform a status command");
		}

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("retrieving_status"));
			progress.resetSecondaryProgress(getAllCVSFiles().size());
			// logger.info("Files nb="+getAllCVSFiles().size());
		}

		StatusCommand statusCommand = new StatusCommand();
		// statusCommand.setRecursive(true);
		try {
			CVSConnection connection = openConnection();
			connection.getClient().setLocalPath(getModuleDirectory().getAbsolutePath());
			connection.getClient().getEventManager().addCVSListener(new CVSAdapter() {
				@Override
				public void fileInfoGenerated(FileInfoEvent e) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("[" + Thread.currentThread().getName() + "] synchronizeWithRepository: Received status for file "
								+ e.getInfoContainer().getFile() + " " + e.getInfoContainer().getClass().getSimpleName());
					}
					if (progress != null) {
						progress.setSecondaryProgress(FlexoLocalization.localizedForKey("received_status_info_for") + " "
								+ e.getInfoContainer().getFile().getName());
					}
				}
			});
			connection.executeCommand(statusCommand);
		} catch (CommandAbortedException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("[" + Thread.currentThread().getName() + "] Now retrieve all necessary versions to compute merges");
		}

		_isSynchronizing = false;

		// Now retrieve all necessary versions to compute merges

		int conflictingFileNumber = 0;
		for (CVSFile f : getAllCVSFiles()) {
			if (f.getStatus().isConflicting()) {
				conflictingFileNumber++;
			}
		}

		if (conflictingFileNumber > 0) {

			if (progress != null) {
				progress.setProgress(FlexoLocalization.localizedForKey("retrieving_remote_versions_for_conflicting_files"));
				progress.resetSecondaryProgress(conflictingFileNumber * 2);
			}

			originalContentRevisionRetrieverListener = new ContentRevisionRetrieverListener(progress);
			contentOnRepositoryRevisionRetrieverListener = new ContentRevisionRetrieverListener(progress);

			for (CVSFile f : getAllCVSFiles()) {
				if (f.getStatus().isConflicting()) {
					if (logger.isLoggable(Level.FINER)) {
						logger.finer("[" + Thread.currentThread().getName() + "] Start obtaining necessary revisions for "
								+ f.getFileName());
					}
					f.getContentOnDisk();
					originalContentRevisionRetrieverListener.addFileToWait(f);
					contentOnRepositoryRevisionRetrieverListener.addFileToWait(f);
					f.getOriginalContent(originalContentRevisionRetrieverListener);
					f.getContentOnRepository(contentOnRepositoryRevisionRetrieverListener);
				}
			}

			waitRevisionRetrievingResponses(progress);

		}

		setChanged();
		notifyObservers(new CVSStructureUpdated(this));

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("[" + Thread.currentThread().getName() + "] Synchronize with repository DONE");
		}

	}

	protected Map<CVSFile, CVSRevisionIdentifier> filesToNotify = Collections
			.synchronizedMap(new HashMap<CVSFile, CVSRevisionIdentifier>());

	private long lastReception;
	private static final long TIME_OUT = CVSConstants.TIME_OUT; // 60 s
	private boolean timeOutReceived = false;

	private ContentRevisionRetrieverListener originalContentRevisionRetrieverListener;
	private ContentRevisionRetrieverListener contentOnRepositoryRevisionRetrieverListener;

	protected class ContentRevisionRetrieverListener implements RevisionRetrieverListener {
		private Vector<CVSFile> filesToWait = new Vector<CVSFile>();
		private FlexoProgress _progress;

		protected ContentRevisionRetrieverListener(FlexoProgress progress) {
			_progress = progress;
		}

		@Override
		public void notifyRevisionRetrieved(CVSFile file, CVSRevisionIdentifier revision) {
			filesToWait.remove(file);
			if (_progress != null) {
				filesToNotify.put(file, revision);
				if (revision == null) {

				} else {

				}
			}
			lastReception = System.currentTimeMillis();
		}

		public void addFileToWait(CVSFile file) {
			filesToWait.add(file);
		}

		public int getNumberOfFileToWait() {
			return filesToWait.size();
		}
	}

	private synchronized void waitRevisionRetrievingResponses(FlexoProgress progress) {
		lastReception = System.currentTimeMillis();

		int filesToWait = originalContentRevisionRetrieverListener.getNumberOfFileToWait()
				+ contentOnRepositoryRevisionRetrieverListener.getNumberOfFileToWait();

		while (filesToWait > 0 && System.currentTimeMillis() - lastReception < TIME_OUT) {
			synchronized (this) {
				try {
					wait(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			filesToWait = originalContentRevisionRetrieverListener.getNumberOfFileToWait()
					+ contentOnRepositoryRevisionRetrieverListener.getNumberOfFileToWait();
			synchronized (filesToNotify) {
				if (progress != null) {
					Iterator<CVSFile> i = filesToNotify.keySet().iterator();
					while (i.hasNext()) {
						CVSFile key = i.next();
						CVSRevisionIdentifier revision = filesToNotify.get(key);
						if (revision == null) {
							progress.setSecondaryProgress(FlexoLocalization.localizedForKeyWithParams(
									"received_repository_revision_for_file_($0)", key.getFileName()));
						} else {
							progress.setSecondaryProgress(FlexoLocalization.localizedForKeyWithParams(
									"received_revision_($0)_for_file_($1)", revision.versionAsString(), key.getFileName()));
						}
					}
				}
				filesToNotify.clear();
			}
		}

		if (filesToWait > 0) {
			timeOutReceived = true;
			logger.warning("Synchronize with repository finished with time-out expired: still waiting for " + filesToWait + " files");
		}

	}

	/**
	 * 
	 * @param aPath
	 *            : might be absolute or relative to module directory
	 * @return
	 */
	public CVSAbstractFile getCVSAbstractFile(String aPath) {
		// First look if absolute path
		File searchedFile = new File(aPath);
		CVSAbstractFile returned = getCVSAbstractFile(searchedFile);
		if (returned != null) {
			return returned;
		}
		// Then look if relative path
		searchedFile = new File(getModuleDirectory(), aPath);
		return getCVSAbstractFile(searchedFile);
	}

	public CVSFile getCVSFile(String aPath) {
		CVSAbstractFile returned = getCVSAbstractFile(aPath);
		if (returned instanceof CVSFile) {
			return (CVSFile) returned;
		}
		return null;
	}

	public CVSAbstractFile createCVSFile(File file) {
		try {
			// Retrieve cannonical file to be sure to work on same FS-tree (think about symb links!)
			file = file.getCanonicalFile();
		} catch (IOException e) {
			logger.warning("Could not retrieve cannonical file for " + file);
			e.printStackTrace();
		}
		if (!FileUtils.isFileContainedIn(file, getModuleDirectory())) {
			logger.warning("createCVSFile() requested for " + file + " which is not contained in " + getModuleDirectory()
					+ ". Something strange happened !");
			return null;
		}
		CVSAbstractFile returned;
		if (getCVSAbstractFile(file) != null) {
			return getCVSAbstractFile(file);
		}
		CVSContainer cvsParent = (CVSContainer) getCVSAbstractFile(file.getParentFile());
		if (cvsParent == null) {
			cvsParent = (CVSDirectory) createCVSFile(file.getParentFile());
		}
		if (file.isDirectory()) {
			returned = new CVSDirectory(file, this);
			cvsParent.addToDirectories((CVSDirectory) returned);
		} else {
			returned = new CVSFile(file, this);
			cvsParent.addToFiles((CVSFile) returned);
		}
		return returned;
	}

	private int locallyModifiedCount = 0;
	private int remotelyModifiedCount = 0;
	private int conflictsCount = 0;

	public int getLocallyModifiedCount() {
		return locallyModifiedCount;
	}

	protected void incLocallyModifiedCount() {
		locallyModifiedCount++;
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	protected void decLocallyModifiedCount() {
		locallyModifiedCount--;
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	public int getRemotelyModifiedCount() {
		return remotelyModifiedCount;
	}

	protected void incRemotelyModifiedCount() {
		remotelyModifiedCount++;
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	protected void decRemotelyModifiedCount() {
		remotelyModifiedCount--;
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	public int getConflictsCount() {
		return conflictsCount;
	}

	// TODO: performance issue
	// Please implement this better
	public int getResolvedConflictsCount() {
		int returned = 0;
		for (CVSFile f : conflictingFiles) {
			if (f.getMerge() != null && f.getMerge().isResolved()) {
				returned++;
			}
		}
		return returned;
	}

	protected void incConflictsCount(CVSFile file) {
		conflictsCount++;
		conflictingFiles.add(file);
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	protected void decConflictsCount(CVSFile file) {
		conflictsCount--;
		conflictingFiles.remove(file);
		setChanged();
		notifyObservers(new CVSStatusChanged(null, null, null));
	}

	private Vector<CVSFile> conflictingFiles = new Vector<CVSFile>();

	/*************************
	 * Thread pool management
	 *************************/

	public void addToThreads(Runnable runnable) {
		addToThreadPool(runnable);
	}

	/*************************
	 * Thread pool management
	 *************************/

	public void addToThreadPool(Runnable runnable) {
		connectionThreadPool.execute(runnable);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Thread pool size " + connectionThreadPool.getPoolSize() + " there are " + connectionThreadPool.getQueue().size()
					+ " threads waiting");
		}
	}

	public FlexoXMLMappings getFlexoXMLMappings() {
		if (_mappings == null) {
			_mappings = new FlexoXMLMappings();
		}
		return _mappings;
	}

}
