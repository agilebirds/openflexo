package org.openflexo.builders;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.openflexo.builders.exception.FlexoRunException;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.exception.SharedProjectNotFoundException;
import org.openflexo.fps.CVSExplorable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSExplorerListener;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.CheckoutProject;
import org.openflexo.toolbox.FileUtils;

public class FlexoForceProjectMain extends FlexoProjectMergeMain implements CVSExplorerListener {

	public static void main(String[] args) {
		launch(FlexoForceProjectMain.class, args);
	}

	public static FlexoForceProjectMain mainTest(String[] args) {
		return launch(FlexoForceProjectMain.class, args);
	}

	private boolean explorationIsDone = false;

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
	}

	@Override
	protected void run() throws FlexoRunException {
		CVSFile.xmlDiff3MergeEnabled = true;
		File checkoutDir = checkoutProject();
		File cvsProjectDirectory = searchProjectDirectory(checkoutDir);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("CVS Project dir is: "+cvsProjectDirectory.getAbsolutePath());
		}
		SharedProject.copyCVSFolders(cvsProjectDirectory,projectDirectory);
		touchProject(projectDirectory, System.currentTimeMillis());
		SharedProject project = SharedProject.openProject(repositories, projectDirectory, cvsRepository, EDITOR);
		if (project == null) {
			setExitCode(PROJECT_NOT_FOUND);
			throw new SharedProjectNotFoundException();
		}
		checkProjectIsOpenable();
		synchronizeProject(project);
		overrideConflictingFiles();
		commitProject();
	}

	private void touchProject(File projectDir, long touchTime) {
		File[] files = projectDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			file.setLastModified(touchTime);
			if (file.isDirectory() && !file.equals("CVS")) {
				touchProject(file, touchTime);
			}
		}
	}

	private File checkoutProject() throws FlexoRunException {
		CVSModule module = cvsRepository.getModuleNamed(moduleName);
		if (module == null) {
			setExitCode(-1);
			throw new NullPointerException("Module named " +  moduleName + " cannot be found");
		}
		String localName = moduleName;

		if (localName.indexOf('/')>-1) {
			localName = localName.substring(localName.lastIndexOf('/')+1);
		}
		exploreModules(module);
		File checkoutDirectory;
		try {
			checkoutDirectory = FileUtils.createTempDirectory(localName, null);
			checkoutDirectory = checkoutDirectory.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
			setExitCode(LOCAL_IO_EXCEPTION);
			throw new FlexoRunException(e);
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
		return checkoutDirectory;
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

}
