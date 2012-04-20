package org.openflexo.builders;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.FlexoRunException;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.Format;
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

	public static final String WORKING_DIR = "-WorkingDir=";

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
	private String workingDir = null;
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
				} else if (args[i].startsWith(WORKING_DIR)) {
					workingDir = args[i].substring(WORKING_DIR.length());
					if (workingDir.startsWith("\"")) {
						workingDir = workingDir.substring(1);
					}
					if (workingDir.endsWith("\"")) {
						workingDir = workingDir.substring(0, workingDir.length() - 1);
					}
				} else if (args[i].equals(NO_WAR)) {
					nowar = true;
				}
			}
		}
		if (outputPath == null || warName == null) {
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
	protected void doRun() throws FlexoRunException {
		File output = null;
		if (workingDir != null) {
			output = new File(workingDir);
			output.mkdirs();
			if (!output.exists() || !output.canWrite()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Output path: " + output.getAbsolutePath()
							+ " either does not exist or does not have write permissions.");
				}
				output = null;
			}
		}
		if (output == null) {
			try {
				output = FileUtils.createTempDirectory("CodeOutput", "");
			} catch (IOException e) {
				e.printStackTrace();
				setExitCode(LOCAL_IO_EXCEPTION);
				throw new FlexoRunException(e);
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Working directory is: " + output.getAbsolutePath());
		}
		File srcDir = new File(output, "Source");
		File docDir = new File(output, "Doc");
		File warDir = new File(outputPath);
		srcDir.mkdirs();
		docDir.mkdirs();
		warDir.mkdirs();
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
			((CGRepository) add.getNewGeneratedCodeRepository()).getWarRepository();
			((CGRepository) add.getNewGeneratedCodeRepository()).setWarDirectory(warDir);
			try {
				((CGRepository) add.getNewGeneratedCodeRepository()).setWarName(warName);
			} catch (DuplicateCodeRepositoryNameException e) {
				e.printStackTrace();
			}
			((CGRepository) add.getNewGeneratedCodeRepository()).setManageHistory(false);
			if (login != null) {
				((CGRepository) add.getNewGeneratedCodeRepository()).setPrototypeLogin(login);
			}
			if (password != null) {
				((CGRepository) add.getNewGeneratedCodeRepository()).setPrototypePassword(password);
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
				((CGRepository) add.getNewGeneratedCodeRepository()).setPreferredTemplateRepository(custom
						.getNewCustomTemplatesRepository());
			}
			codeType = add.getNewTargetType().toString();
			SynchronizeRepositoryCodeGeneration sync = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
					add.getNewGeneratedCodeRepository(), null, editor);
			sync.setSaveBeforeGenerating(false);
			sync.setContinueAfterValidation(true);
			sync.doAction();
			if (!sync.hasActionExecutionSucceeded()) {
				handleActionFailed(sync);
			}
			WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewAction(add.getNewGeneratedCodeRepository(),
					null, editor);
			write.setSaveBeforeGenerating(false);
			write.doAction();
			if (!write.hasActionExecutionSucceeded()) {
				handleActionFailed(write);
			}
			if (nowar) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("No war option is true, ignoring WAR generation.\nReturning now.");
				}
				return;
			}
			GenerateWAR war = GenerateWAR.actionType.makeNewAction((CGRepository) add.getNewGeneratedCodeRepository(), null, editor);
			war.setSaveBeforeGenerating(false);
			war.setCleanImmediately(true);
			war.doAction();
			if (!war.hasActionExecutionSucceeded()) {
				handleActionFailed(war);
			}
			File warFile = war.getGeneratedWar();// new File(warDir,((CGRepository) add.getNewGeneratedCodeRepository()).getWarName()+
													// ".war");
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
					setExitCode(WAR_NOT_FOUND);
					return;
				}
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("War file has been found at " + files[0].getAbsolutePath() + " but is not what was expected");
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("WAR Generated at " + warFile.getAbsolutePath());
			}
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
