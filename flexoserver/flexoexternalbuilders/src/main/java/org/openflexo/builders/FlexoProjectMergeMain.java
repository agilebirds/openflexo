package org.openflexo.builders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.CorruptedProjectException;
import org.openflexo.builders.exception.FlexoRunException;
import org.openflexo.builders.exception.MergeFailedException;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.exception.ProjectNotFoundException;
import org.openflexo.builders.exception.SharedProjectNotFoundException;
import org.openflexo.builders.utils.FlexoBuilderEditor;
import org.openflexo.builders.utils.FlexoBuilderListener;
import org.openflexo.builders.utils.FlexoCVSConsoleListener;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoEditor.FlexoEditorFactory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.CommitFiles;
import org.openflexo.fps.action.MarkAsMergedFiles;
import org.openflexo.fps.action.OverrideAndCommitFiles;
import org.openflexo.fps.action.ShareProject;
import org.openflexo.fps.action.SynchronizeWithRepository;
import org.openflexo.fps.action.UpdateFiles;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public class FlexoProjectMergeMain extends FlexoExternalMain {

	protected static final Logger logger = FlexoLogger.getLogger(FlexoProjectMergeMain.class.getPackage().getName());

	public static final int CONFLICTING_FILES = -9;

	public static final String FIRST_COMMIT_ARGUMENT = "-FirstCommit";

	public static final String PROJECT_DIRECTORY_ARGUMENT_PREFIX = "-Directory=";

	public static final String CVS_REPOSITORY_FILE_ARGUMENT_PREFIX = "-CVSRepository=";

	public static final String OVERRIDE_ARGUMENT = "-Override";

	public static final String MODULE_NAME_ARGUMENT_PREFIX = "-ModuleName=";

	public static final String TAG_ARGUMENT_PREFIX = "-Tag=";

	public static final String COMMENT_ARGUMENT_PREFIX = "-Comment=";

	protected FlexoEditor EDITOR = new DefaultFlexoEditor() {
		@Override
		public boolean performResourceScanning() {
			return false;
		};
	};
	protected FlexoCVSConsoleListener cvsConsole;

	private boolean isFirstCommit = false;
	protected File projectDirectory = null;
	protected org.openflexo.fps.CVSRepository cvsRepository;
	private boolean override = false;
	protected String moduleName;
	protected String tag;
	protected String comment;

	protected CVSRepositoryList repositories;

	private Vector<CVSFile> files;

	private Vector<FPSObject> filesToCommit;

	private Vector<FPSObject> conflictingMergeableFiles;

	private Vector<CVSFile> conflictingFiles;

	private Vector<FPSObject> filesToUpdate;

	@Override
	protected String getName() {
		return "Project merger";
	}

	public static void main(String[] args) {
		launch(FlexoProjectMergeMain.class, args);
	}

	public static FlexoProjectMergeMain mainTest(String[] args) {
		return launch(FlexoProjectMergeMain.class, args);
	}

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		File repository = null;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals(FIRST_COMMIT_ARGUMENT)) {
					isFirstCommit = true;
				} else if (args[i].startsWith(PROJECT_DIRECTORY_ARGUMENT_PREFIX)) {
					projectDirectory = new File(args[i].substring(PROJECT_DIRECTORY_ARGUMENT_PREFIX.length()));
					if (!projectDirectory.exists()) {
						projectDirectory = null;
					}
				} else if (args[i].startsWith(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX)) {
					repository = new File(args[i].substring(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX.length()));
					if (!repository.exists()) {
						repository = null;
					}
				} else if (args[i].startsWith(OVERRIDE_ARGUMENT)) {
					override = true;
				} else if (args[i].startsWith(MODULE_NAME_ARGUMENT_PREFIX)) {
					moduleName = args[i].substring(MODULE_NAME_ARGUMENT_PREFIX.length());
					if (moduleName.startsWith("\"")) {
						moduleName = moduleName.substring(1);
					}
					if (moduleName.endsWith("\"")) {
						moduleName = moduleName.substring(0, moduleName.length() - 1);
					}
				} else if (args[i].startsWith(TAG_ARGUMENT_PREFIX)) {
					tag = args[i].substring(TAG_ARGUMENT_PREFIX.length());
					if (tag.startsWith("\"")) {
						tag = tag.substring(1);
					}
					if (tag.endsWith("\"")) {
						tag = tag.substring(0, tag.length() - 1);
					}
				} else if (args[i].startsWith(COMMENT_ARGUMENT_PREFIX)) {
					comment = args[i].substring(COMMENT_ARGUMENT_PREFIX.length());
					if (comment.startsWith("\"")) {
						comment = comment.substring(1);
					}
					if (comment.endsWith("\"")) {
						comment = comment.substring(0, comment.length() - 1);
					}
				}
			}
		}
		repositories = new CVSRepositoryList();
		Properties cvsProperties = new Properties();
		try {
			cvsProperties.loadFromXML(new FileInputStream(repository));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tag != null) {
			tag = tag.replaceAll("[ \\$,\\.:;@|]", "_"); // Tags cannot contain those special chars.
		}
		cvsRepository = new CVSRepository(cvsProperties);
		repositories.addToCVSRepositories(cvsRepository);
		cvsConsole = new FlexoCVSConsoleListener();
		if (moduleName == null || repository == null) {
			StringBuilder sb = new StringBuilder();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					sb.append(i).append(": ").append(args[i]).append("\n");
				}
			}
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Missing argument. Usage java " + FlexoProjectMergeMain.class.getName() + " " + RESOURCE_PATH_ARGUMENT_PREFIX
						+ " " + CVS_REPOSITORY_FILE_ARGUMENT_PREFIX + " " + PROJECT_DIRECTORY_ARGUMENT_PREFIX + " " + TAG_ARGUMENT_PREFIX
						+ " " + "\n" + (args.length > 0 ? sb.toString() : "No arguments !!!"));
			}
			if (moduleName == null) {
				throw new MissingArgumentException(MODULE_NAME_ARGUMENT_PREFIX);
			} else {
				throw new MissingArgumentException(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX);
			}
		}

	}

	@Override
	protected void run() throws FlexoRunException {
		CVSFile.xmlDiff3MergeEnabled = true;
		SharedProject project = null;
		if (isFirstCommit) {
			project = shareProject();
			checkProjectIsOpenable();
		} else {
			project = SharedProject.openProject(repositories, projectDirectory, cvsRepository, EDITOR);
			if (project == null) {
				setExitCode(PROJECT_NOT_FOUND);
				throw new SharedProjectNotFoundException();
			}
			synchronizeProject(project);
			if (override) {
				overrideConflictingFiles();
			} else {
				// Here conflicting files contains the files that cannot be
				// automatically merged
				if (conflictingFiles.size() > 0) {
					prepareMergeFailed();
				}
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Ready to commit " + (filesToCommit.size() + conflictingMergeableFiles.size()) + " files including "
							+ conflictingMergeableFiles.size() + " automatically merged.");
				}
				if (conflictingMergeableFiles.size() > 0) {
					mergeFiles();
				}
			}
			updateProject();
			checkProjectIsOpenable();
			commitProject();
		}
	}

	/**
	 * 
	 */
	protected void commitProject() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Starting to commit " + filesToCommit.size() + " files for " + moduleName);
		}
		CommitFiles commit = CommitFiles.actionType.makeNewAction(null, filesToCommit, EDITOR);
		commit.setCommitMessage(comment != null ? comment + "\n" : "");
		commit.doAction();
		if (!commit.hasActionExecutionSucceeded()) {
			handleActionFailed(commit, projectDirectory);
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Commit done for " + moduleName);
		}
	}

	/**
	 * 
	 */
	private void updateProject() {
		// at this point : all files that were trivially mergeable are merged on disk and mark as merged in CVS
		// at this point : filesToCommit contains all files that were only locally modified + all files that where trivially mergeable (and
		// physically merged)
		// at this point : filesToUpdate contains all files that where only remotely modified.
		if (filesToUpdate.size() > 0) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("It seems we are ready to commit, but there is some files to update.");
			}
			// now : what we want to do is to update all remotely modified files (i.e. : filesToUpdate) and check that the project can be
			// opened.
			UpdateFiles updateFiles = UpdateFiles.actionType.makeNewAction(null, filesToUpdate, EDITOR);
			updateFiles.doAction();
			if (!updateFiles.hasActionExecutionSucceeded()) {
				handleActionFailed(updateFiles, projectDirectory);
			}

		}
	}

	/**
	 * @throws MergeFailedException
	 */
	private void prepareMergeFailed() throws MergeFailedException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Merge cannot be performed: " + conflictingFiles.size() + " are conflicting. Set logger named " + logger.getName()
					+ " level to FINE to see files and to FINEST to see merge detail.");
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Conflicting files:");
			for (CVSFile file : conflictingFiles) {
				logger.fine(file.getFileName());
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest(file.getMerge().toString());
				}
			}
		}
		File projectCopyDirectory = null;
		FlexoEditor editor = null;
		try {
			List<FlexoStorageResource<? extends StorageResourceData>> conflictingResources = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
			projectCopyDirectory = FileUtils.createTempDirectory("Copy_", "_of_" + projectDirectory.getName());
			FileUtils.copyContentDirToDir(projectDirectory, projectCopyDirectory);
			editor = FlexoResourceManager.initializeExistingProject(projectCopyDirectory, new FlexoEditorFactory() {

				@Override
				public FlexoEditor makeFlexoEditor(FlexoProject project) {

					return new FlexoBuilderEditor(project);
				}
			}, null);
			for (CVSFile file : conflictingFiles) {
				String fileName = file.getFile().getAbsolutePath();
				for (FlexoStorageResource<? extends StorageResourceData> r : editor.getProject().getStorageResources()) {
					if (fileName.endsWith(r.getResourceFile().getRelativePath())) {
						conflictingResources.add(r);
						break;
					}
				}
			}
			writeToConsole(FlexoBuilderListener.CONFLICTING_RESSOURCES_START_TAG);
			for (FlexoStorageResource<? extends StorageResourceData> r : conflictingResources) {
				writeToConsole(r.getResourceType().getLocalizedName() + " " + r.getName() + " is in conflict");
			}
			writeToConsole(FlexoBuilderListener.CONFLICTING_RESSOURCES_END_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (editor != null && editor.getProject() != null) {
				editor.getProject().close();
			}
		}
		setExitCode(CONFLICTING_FILES);
		throw new MergeFailedException(conflictingFiles);
	}

	/**
	 * @throws MergeFailedException
	 */
	private void mergeFiles() throws MergeFailedException {
		if (conflictingMergeableFiles.size() > 0) {
			MarkAsMergedFiles markAsMerged = MarkAsMergedFiles.actionType.makeNewAction(null, conflictingMergeableFiles, EDITOR);
			markAsMerged.doAction();
			if (!markAsMerged.hasActionExecutionSucceeded()) {
				handleActionFailed(markAsMerged, projectDirectory);
			}
			// now : all conflictingMergeableFiles are physically merge on disk
			// see : CVSFile.markAsMerged()
			for (FPSObject file : conflictingMergeableFiles) { // check that all files "mark as merged" aren't conflicting any more
				if (((CVSFile) file).getStatus().isConflicting()) {
					conflictingFiles.add((CVSFile) file); // a file that has been "mark as merged" is still conflicting... that's very bad
				}
			}
			if (conflictingFiles.size() > 0) {
				prepareMergeFailed();
			}
			filesToCommit.addAll(conflictingMergeableFiles);
		}
	}

	/**
	 * 
	 */
	protected void overrideConflictingFiles() {
		if (filesToUpdate.size() > 0) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Something is not right. Project was supposed to be locked but there are " + filesToUpdate.size()
						+ " files to update.\n" + "\tI will continue but checked-out project may be different from uploaded project");
			}
		}
		// If we don't care aboute conflicts and other problems
		Vector<FPSObject> filesToOverride = new Vector<FPSObject>();
		filesToOverride.addAll(conflictingMergeableFiles);
		filesToOverride.addAll(conflictingFiles);
		OverrideAndCommitFiles overrideAndCommit = OverrideAndCommitFiles.actionType.makeNewAction(null, filesToOverride, EDITOR);
		overrideAndCommit.doAction();
		if (!overrideAndCommit.hasActionExecutionSucceeded()) {
			handleActionFailed(overrideAndCommit, projectDirectory);
		}
	}

	/**
	 * @param project
	 */
	protected void synchronizeProject(SharedProject project) {
		// First we synchronize the files with CVS--> retrieves the
		// status
		// and the merges
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Launching project synchronization for " + moduleName);
		}
		SynchronizeWithRepository synchronize = SynchronizeWithRepository.actionType
				.makeNewAction(project, new Vector<FPSObject>(), EDITOR);
		synchronize.doAction();
		if (!synchronize.hasActionExecutionSucceeded()) {
			handleActionFailed(synchronize, projectDirectory);
		}

		files = project.getAllCVSFiles();
		filesToCommit = new Vector<FPSObject>();
		conflictingMergeableFiles = new Vector<FPSObject>();
		conflictingFiles = new Vector<CVSFile>();
		filesToUpdate = new Vector<FPSObject>();
		for (CVSFile file : files) {
			if (file.getStatus().isConflicting()) {
				if (file.getMerge().isResolved()) {
					conflictingMergeableFiles.add(file);
				} else {
					if (file.getFileName().endsWith(".rmxml.ts") || file.getFileName().endsWith(".autosave")) {
						continue;
					}
					conflictingFiles.add(file);
				}
			}
			if (file.getStatus().isLocallyModified()) {
				filesToCommit.add(file);
			}
			if (file.getStatus().isRemotelyModified()) {
				filesToUpdate.add(file);
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Synchronization done for " + moduleName + " with " + filesToCommit.size() + " file to commit, "
					+ conflictingMergeableFiles.size() + " conflicting mergeable files and " + conflictingFiles.size()
					+ " really conflicting files.");
		}
	}

	/**
	 * @throws ProjectNotFoundException
	 * @throws CorruptedProjectException
	 */
	protected void checkProjectIsOpenable() throws ProjectNotFoundException, CorruptedProjectException {
		// at this point all files to update are updated.
		// so we can try to load the project... and see if it's going right !!!
		File projectCopyDirectory = null;
		try {
			projectCopyDirectory = FileUtils.createTempDirectory("Copy_", "_of_" + projectDirectory.getName());
			FileUtils.copyContentDirToDir(projectDirectory, projectCopyDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
			setExitCode(PROJECT_NOT_FOUND);
			throw new ProjectNotFoundException(e1);
		}
		FlexoEditor editor = null;
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectCopyDirectory, new FlexoEditorFactory() {

				@Override
				public FlexoEditor makeFlexoEditor(FlexoProject project) {

					return new FlexoBuilderEditor(project);
				}
			}, null);

			if (editor == null) {
				setExitCode(CORRUPTED_PROJECT_EXCEPTION);
				throw new CorruptedProjectException(
						"Try to open the merged project, but I got no editor, and the call to FlexoResourceManager.initializeExistingProject didn't throw any exception.");
			}
			if (editor.getProject() == null) {
				setExitCode(CORRUPTED_PROJECT_EXCEPTION);
				throw new CorruptedProjectException(
						"Try to open the merged project, but I got no project, and the call to FlexoResourceManager.initializeExistingProject call didn't throw any exception.");
			}
			// printTOCRepositories(editor);
		} catch (ProjectLoadingCancelledException e) {
			setExitCode(CORRUPTED_PROJECT_EXCEPTION);
			throw new CorruptedProjectException(e);
		} catch (ProjectInitializerException e) {
			setExitCode(CORRUPTED_PROJECT_EXCEPTION);
			throw new CorruptedProjectException(e);
		} finally {
			if (editor != null && editor.getProject() != null) {
				editor.getProject().close();
			}
		}
	}

	/*	private void printTOCRepositories(FlexoEditor editor) throws ProjectInitializerException{
		if(editor.getProject().getTOCData()!=null){
			try {
				String encodedTOCData = XMLCoder.encodeObjectWithMapping(editor.getProject().getTOCData(), new XMLMapping(new FileResource("Models/TOCModel/short_toc_model_0.1.xml")),StringEncoder.getDefaultInstance());
				System.err.println(FlexoBuilderListener.TOCS_START_TAG);
				System.err.println(encodedTOCData);
				System.err.println(FlexoBuilderListener.TOCS_END_TAG);
			} catch (InvalidModelException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (SAXException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (InvalidObjectSpecificationException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (AccessorInvocationException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			} catch (DuplicateSerializationIdentifierException e) {
				e.printStackTrace();
				throw new ProjectInitializerException(e);
			}

		}
	}
	 */
	/**
	 * 
	 */
	private SharedProject shareProject() {
		// If it is the first commit, then we start by sharing.
		ShareProject shareProject = ShareProject.actionType.makeNewAction(repositories, null, EDITOR);
		shareProject.setProjectDirectory(projectDirectory);
		shareProject.setRepository(cvsRepository);
		shareProject.setCvsIgnorize(true);
		shareProject.setRemoveExistingCVSDirectories(true);
		shareProject.setModuleName(moduleName);
		shareProject.setVendorTag(tag);
		shareProject.doAction();
		if (!shareProject.hasActionExecutionSucceeded()) {
			handleActionFailed(shareProject, projectDirectory);
		} else if (logger.isLoggable(Level.INFO)) {
			logger.info("Project sharing succeeded");
		}
		return shareProject.getProject();
	}

	@Override
	protected void cleanUp() {
		if (getExitCode() != 0) {
			reportMessage(cvsConsole.getLogs().toString());
		}
		super.cleanUp();
	}

}
