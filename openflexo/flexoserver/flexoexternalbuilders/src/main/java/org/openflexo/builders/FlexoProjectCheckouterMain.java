package org.openflexo.builders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.FlexoRunException;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.exception.ProjectNotFoundException;
import org.openflexo.builders.utils.FlexoCVSConsoleListener;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSExplorerListener;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.action.CheckoutProject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public class FlexoProjectCheckouterMain extends FlexoExternalMain implements CVSExplorerListener {

	private static final Logger logger = FlexoLogger.getLogger(FlexoProjectCheckouterMain.class.getPackage().getName());

	public static final String CHECKOUT_DIRECTORY_ARGUMENT_PREFIX = "-Directory=";

	public static final String CVS_REPOSITORY_FILE_ARGUMENT_PREFIX = "-CVSRepository=";

	public static final String MODULE_NAME_ARGUMENT_PREFIX = "-ModuleName=";

	public static final String LOCAL_NAME_ARGUMENT_PREFIX = "-LocalName=";

	private FlexoEditor EDITOR = new DefaultFlexoEditor() {
		@Override
		public boolean performResourceScanning() {
			return false;
		};
	};

	private File checkoutDirectory;
	private String moduleName;
	private String localName;
	private CVSRepositoryList repositories;
	private CVSRepository cvsRepository;

	private boolean explorationIsDone;
	private FlexoCVSConsoleListener cvsConsole;

	public FlexoProjectCheckouterMain() {
	}

	@Override
	protected String getName() {
		return "Project checkouter";
	}

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		File repository = null;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(CHECKOUT_DIRECTORY_ARGUMENT_PREFIX)) {
					checkoutDirectory = new File(args[i].substring(CHECKOUT_DIRECTORY_ARGUMENT_PREFIX.length()));
					if (!checkoutDirectory.exists()) {
						checkoutDirectory = null;
					}
				} else if (args[i].startsWith(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX)) {
					repository = new File(args[i].substring(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX.length()));
					if (!repository.exists()) {
						repository = null;
					}
				} else if (args[i].startsWith(MODULE_NAME_ARGUMENT_PREFIX)) {
					moduleName = args[i].substring(MODULE_NAME_ARGUMENT_PREFIX.length());
					if (moduleName.startsWith("\"")) {
						moduleName = moduleName.substring(1);
					}
					if (moduleName.endsWith("\"")) {
						moduleName = moduleName.substring(0,moduleName.length()-1);
					}
				} else if (args[i].startsWith(LOCAL_NAME_ARGUMENT_PREFIX)) {
					localName = args[i].substring(LOCAL_NAME_ARGUMENT_PREFIX.length());
					if (localName.startsWith("\"")) {
						localName = localName.substring(1);
					}
					if (localName.endsWith("\"")) {
						localName = localName.substring(0,localName.length()-1);
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
				logger.severe("Missing argument. Usage java " + FlexoProjectMergeMain.class.getName() + " "
						+ RESOURCE_PATH_ARGUMENT_PREFIX + " " + CVS_REPOSITORY_FILE_ARGUMENT_PREFIX + " " + CHECKOUT_DIRECTORY_ARGUMENT_PREFIX
						+ "\n" + (args.length > 0 ? sb.toString() : "No arguments !!!"));
			}
			if (moduleName==null) {
				throw new MissingArgumentException(MODULE_NAME_ARGUMENT_PREFIX);
			} else {
				throw new MissingArgumentException(CVS_REPOSITORY_FILE_ARGUMENT_PREFIX);
			}
		}
		if (localName==null) {
			localName = moduleName;
		}
		if (localName.indexOf('/')>-1) {
			localName = localName.substring(localName.lastIndexOf('/')+1);
		}
	}

	@Override
	protected void run() throws FlexoRunException {
		CVSFile.xmlDiff3MergeEnabled = true;
		CVSModule module = cvsRepository.getModuleNamed(moduleName);
		if (module == null) {
			throw new NullPointerException("Module named " +  moduleName + " cannot be found");
		}
		exploreModules(module);
		if (!checkoutDirectory.exists()) {
			checkoutDirectory.mkdirs();
		} else {
			FileUtils.deleteFilesInDir(checkoutDirectory);
		}
		CheckoutProject checkout = CheckoutProject.actionType.makeNewAction(module, null, EDITOR);
		checkout.setLocalDirectory(checkoutDirectory);
		checkout.setLocalName(localName);
		checkoutDirectory = new File(checkoutDirectory, localName);
		if (checkoutDirectory.exists()) {
			FileUtils.deleteDir(checkoutDirectory);
		}
		checkoutDirectory.mkdirs();
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Checkout directory is: " + checkoutDirectory.getAbsolutePath());
		}
		checkout.doAction();
		if (!checkout.hasActionExecutionSucceeded()) {
			handleActionFailed(checkout);
		}
		File projectDirectory = searchProjectDirectory(checkoutDirectory);
		if (projectDirectory != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Project Directory is " + projectDirectory.getAbsolutePath());
			}
		}
		if (projectDirectory == null || !projectDirectory.exists()) {
			setExitCode(PROJECT_NOT_FOUND);
			throw new ProjectNotFoundException();
		}
	}

	@Override
	protected void cleanUp() {
		if (getExitCode()!=0) {
			reportMessage(cvsConsole.getLogs().toString());
		}
		super.cleanUp();
	}

	@Override
	public void exploringFailed(CVSExplorable explorable, CVSExplorer explorer, Exception exception) {
		explorationIsDone = true;
	}

	@Override
	public void exploringSucceeded(CVSExplorable explorable, CVSExplorer explorer) {
		explorationIsDone = true;
	}

	private void exploreModules(CVSExplorable explorable) {
		if (explorable.getParent() != null) {
			exploreModules(explorable.getParent());
		}
		explorationIsDone = false;
		explorable.explore(this);
		while (!explorationIsDone) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(FlexoProjectCheckouterMain.class, args);
	}

	public static FlexoProjectCheckouterMain mainTest(String[] args) {
		return launch(FlexoProjectCheckouterMain.class, args);
	}

}
