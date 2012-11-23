package org.openflexo.builders;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.templates.action.ImportTemplates;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.action.GenerateWAR;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;

public class FlexoCodeGeneratorMain extends FlexoExternalMainWithProject {

	private static final Logger logger = FlexoLogger.getLogger(FlexoCodeGeneratorMain.class.getPackage().getName());

	private static final int WAR_NOT_FOUND = -8;

	public static final String CODE_TYPE_ARGUMENT_PREFIX = "-CodeType=";

	public static final String TEMPLATES_ARGUMENT_PREFIX = "-Templates=";

	public static final String WAR_DIR_ARGUMENT_PREFIX = "-WarDir=";

	public static final String WAR_NAME_ARGUMENT_PREFIX = "-WarName=";

	public static final String NO_WAR = "-nowar";

	public static final String LOGIN_ARGUMENT_PREFIX = "-login=";

	public static final String PASSWORD_ARGUMENT_PREFIX = "-password=";

	// Variables
	private File templates = null;
	private String codeType = null;
	private String login = null;
	private String password = null;
	private String outputPath = null;
	private String warName = null;
	private boolean nowar = false;

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(CODE_TYPE_ARGUMENT_PREFIX)) {
					codeType = args[i].substring(CODE_TYPE_ARGUMENT_PREFIX.length());
				} else if (args[i].startsWith(TEMPLATES_ARGUMENT_PREFIX)) {
					templates = new File(args[i].substring(TEMPLATES_ARGUMENT_PREFIX.length()));
					if (!templates.exists()) {
						templates = null;
					}
				} else if (args[i].startsWith(WAR_DIR_ARGUMENT_PREFIX)) {
					outputPath = args[i].substring(WAR_DIR_ARGUMENT_PREFIX.length());
					if (outputPath.startsWith("\"")) {
						outputPath = outputPath.substring(1);
					}
					if (outputPath.endsWith("\"")) {
						outputPath = outputPath.substring(0, outputPath.length() - 1);
					}
				} else if (args[i].startsWith(WAR_NAME_ARGUMENT_PREFIX)) {
					warName = args[i].substring(WAR_NAME_ARGUMENT_PREFIX.length());
					if (warName.startsWith("\"")) {
						warName = warName.substring(1);
					}
					if (warName.endsWith("\"")) {
						warName = warName.substring(0, warName.length() - 1);
					}
				} else if (args[i].startsWith(LOGIN_ARGUMENT_PREFIX)) {
					login = args[i].substring(LOGIN_ARGUMENT_PREFIX.length());
					if (login.startsWith("\"")) {
						login = login.substring(1);
					}
					if (login.endsWith("\"")) {
						login = login.substring(0, login.length() - 1);
					}
				} else if (args[i].startsWith(PASSWORD_ARGUMENT_PREFIX)) {
					password = args[i].substring(PASSWORD_ARGUMENT_PREFIX.length());
					if (password.startsWith("\"")) {
						password = password.substring(1);
					}
					if (password.endsWith("\"")) {
						password = password.substring(0, password.length() - 1);
					}
				} else if (args[i].equals(NO_WAR)) {
					nowar = true;
				}
			}
		}
		if ((outputPath == null || warName == null) && !nowar) {
			StringBuilder sb = new StringBuilder();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					sb.append(i).append(": ").append(args[i]).append("\n");
				}
			}
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Missing argument. Usage java " + FlexoCodeGeneratorMain.class.getName() + " "
						+ RESOURCE_PATH_ARGUMENT_PREFIX + " " + WAR_DIR_ARGUMENT_PREFIX + " " + WAR_NAME_ARGUMENT_PREFIX + " "
						+ CODE_TYPE_ARGUMENT_PREFIX + " " + TEMPLATES_ARGUMENT_PREFIX + " " + "\n"
						+ (args.length > 0 ? sb.toString() : "No arguments !!!"));
			}
			if (outputPath == null) {
				throw new MissingArgumentException(WAR_DIR_ARGUMENT_PREFIX);
			} else {
				throw new MissingArgumentException(WAR_NAME_ARGUMENT_PREFIX);
			}
		}
	}

	@Override
	protected void doRun() {
		File output = getWorkingDir();
		if (!output.exists() || !output.canWrite()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Output path: " + output.getAbsolutePath() + " either does not exist or does not have write permissions.");
			}
			output = null;
		}
		if (output == null) {
			try {
				output = FileUtils.createTempDirectory("CodeOutput", "");
			} catch (IOException e) {
				e.printStackTrace();
				setExitCodeCleanUpAndExit(LOCAL_IO_EXCEPTION);
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Working directory is: " + output.getAbsolutePath());
		}
		File srcDir = new File(output, "Source");
		File docDir = new File(output, "Doc");
		srcDir.mkdirs();
		docDir.mkdirs();
		final File warDir;
		if (!nowar) {
			warDir = new File(outputPath);
			warDir.mkdirs();
		} else {
			warDir = null;
		}
		if (project != null) {
			project.setComputeDiff(false);

			AddGeneratedCodeRepository addReaderRepo = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject()
					.getGeneratedDoc(), null, editor);
			addReaderRepo.setNewGeneratedCodeRepositoryName("ReaderRepositoryProto");
			addReaderRepo.setNewGeneratedCodeRepositoryDirectory(docDir);
			addReaderRepo.setFormat(Format.HTML);
			addReaderRepo.doAction();
			DGRepository readerRepository = (DGRepository) addReaderRepo.getNewGeneratedCodeRepository();

			AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject().getGeneratedCode(),
					null, editor);
			add.setReaderRepository(readerRepository);
			add.setNewTargetType(CodeType.codeTypeConverter.convertFromString(codeType));
			add.setNewGeneratedCodeRepositoryName("FlexoServerGeneratedCode" + new Random().nextInt(1000000));
			add.setNewGeneratedCodeRepositoryDirectory(srcDir);
			add.doAction();
			final CGRepository repository = (CGRepository) add.getNewGeneratedCodeRepository();
			repository.getWarRepository();
			repository.setWarDirectory(warDir);
			try {
				repository.setWarName(warName);
			} catch (DuplicateCodeRepositoryNameException e) {
				e.printStackTrace();
			}
			repository.setManageHistory(false);
			if (login != null) {
				repository.setPrototypeLogin(login);
			}
			if (password != null) {
				repository.setPrototypePassword(password);
			}
			if (!add.hasActionExecutionSucceeded()) {
				handleActionFailed(add);
			}
			if (templates != null) {
				AddCustomTemplateRepository custom = AddCustomTemplateRepository.actionType.makeNewAction(project.getGeneratedCode()
						.getTemplates(), null, editor);
				custom.setNewCustomTemplatesRepositoryName("FlexoServerTemplates");
				custom.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(project, "FlexoServerTemplates"));
				custom.doAction();
				if (!custom.hasActionExecutionSucceeded()) {
					handleActionFailed(custom);
				}
				ImportTemplates importTemplates = ImportTemplates.actionType.makeNewAction(project.getGeneratedCode().getTemplates()
						.getApplicationRepository(), null, editor);
				importTemplates.setExternalTemplateDirectory(templates);
				importTemplates.setRepository(custom.getNewCustomTemplatesRepository());
				importTemplates.doAction();
				if (!importTemplates.hasActionExecutionSucceeded()) {
					handleActionFailed(importTemplates);
				}
				repository.setPreferredTemplateRepository(custom.getNewCustomTemplatesRepository());
			}
			codeType = add.getNewTargetType().toString();
			SynchronizeRepositoryCodeGeneration sync = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(repository, null,
					editor);
			sync.setSaveBeforeGenerating(false);
			sync.setContinueAfterValidation(true);
			WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewAction(repository, null, editor);
			write.setSaveBeforeGenerating(false);
			final GenerateWAR war = GenerateWAR.actionType.makeNewAction(repository, null, editor);
			war.setSaveBeforeGenerating(false);
			war.setCleanImmediately(true);
			war.getProjectGenerator().addBuildListener(new BuildListener() {

				@Override
				public void taskStarted(BuildEvent event) {
					// TODO Auto-generated method stub

				}

				@Override
				public void taskFinished(BuildEvent event) {
					// TODO Auto-generated method stub

				}

				@Override
				public void targetStarted(BuildEvent event) {
					// TODO Auto-generated method stub

				}

				@Override
				public void targetFinished(BuildEvent event) {
					// TODO Auto-generated method stub

				}

				@Override
				public void messageLogged(BuildEvent event) {
					if (event.getPriority() == Project.MSG_INFO) {
						System.err.println("INFO: " + event.getMessage());
					} else if (event.getPriority() == Project.MSG_WARN) {
						System.err.println("WARNING: " + event.getMessage());
					} else if (event.getPriority() == Project.MSG_ERR) {
						reportMessage("ERROR: " + event.getMessage());
					}
				}

				@Override
				public void buildStarted(BuildEvent event) {
					// TODO Auto-generated method stub

				}

				@Override
				public void buildFinished(BuildEvent event) {
					// TODO Auto-generated method stub

				}
			});
			List<FlexoAction<?, ?, ?>> actions = new ArrayList<FlexoAction<?, ?, ?>>();
			actions.add(sync);
			actions.add(write);
			if (!nowar) {
				actions.add(war);
			}
			editor.chainActions(actions, new Runnable() {

				@Override
				public void run() {
					if (nowar) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("No war option is true, ignoring WAR generation.\nReturning now.");
						}
						setExitCodeCleanUpAndExit(0);
					}
					File warFile = war.getGeneratedWar();
					if (!warFile.exists()) {
						File[] files = warDir.listFiles(new FilenameFilter() {

							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(".war");
							}
						});
						if (files == null || files.length == 0) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("War file not found at " + warDir.getAbsolutePath());
							}
							setExitCodeCleanUpAndExit(WAR_NOT_FOUND);
						}
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("War file has been found at " + files[0].getAbsolutePath() + " but is not what was expected");
						}
					}
					if (logger.isLoggable(Level.INFO)) {
						logger.info("WAR Generated at " + warFile.getAbsolutePath());
					}
					setExitCodeCleanUpAndExit(0);
				}
			});
		}
	}

	public static void main(String[] args) {
		launch(FlexoCodeGeneratorMain.class, args);
	}

	@Override
	protected String getName() {
		return "Code generator";
	}

}
