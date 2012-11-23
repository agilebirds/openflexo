package org.openflexo.builders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.dg.action.GenerateDocx;
import org.openflexo.dg.action.GeneratePDF;
import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.action.AddDocType;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.cg.action.ConnectCGRepository;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.templates.action.ImportTemplates;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.action.GenerateArtefact;
import org.openflexo.generator.action.GenerateZip;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.LatexUtils;

public class FlexoDocGeneratorMain extends FlexoExternalMainWithProject {

	private static final Logger logger = FlexoLogger.getLogger(FlexoDocGeneratorMain.class.getPackage().getName());

	public static final String DOC_TYPE_ARGUMENT_PREFIX = "-DocType=";

	public static final String TEMPLATES_ARGUMENT_PREFIX = "-Templates=";

	public static final String OUTPUT_FILE_ARGUMENT_PREFIX = "-Output=";

	public static final String NO_POST_BUILD = "-nopostbuild";

	public static final String TOC_FLEXOID = "-tocflexoid";

	public static final String TOC_UID = "-tocuserid";

	public static final String FORMAT = "-Format=";

	public static final int TOC_REPOSITORY_NOT_FOUND = -9;

	private static final int POST_BUILD_ERROR = -10;

	// Variables
	private File templates = null;
	private String docType = DefaultDocType.Business.name();
	private Format format = Format.LATEX;
	private String outputPath = null;
	private boolean noPostBuild = false;
	private String tocUserID;
	private long tocFlexoID = -1;

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(DOC_TYPE_ARGUMENT_PREFIX)) {
					docType = args[i].substring(DOC_TYPE_ARGUMENT_PREFIX.length());
				} else if (args[i].startsWith(TOC_FLEXOID)) {
					String tocString = args[i].substring(TOC_FLEXOID.length()).trim();
					tocFlexoID = Long.valueOf(tocString);
				} else if (args[i].startsWith(TOC_UID)) {
					tocUserID = args[i].substring(TOC_UID.length()).trim();
				} else if (args[i].startsWith(TEMPLATES_ARGUMENT_PREFIX)) {
					templates = new File(args[i].substring(TEMPLATES_ARGUMENT_PREFIX.length()));
					if (!templates.exists()) {
						templates = null;
					}
				} else if (args[i].startsWith(OUTPUT_FILE_ARGUMENT_PREFIX)) {
					outputPath = args[i].substring(OUTPUT_FILE_ARGUMENT_PREFIX.length());
					if (outputPath.startsWith("\"")) {
						outputPath = outputPath.substring(1);
					}
					if (outputPath.endsWith("\"")) {
						outputPath = outputPath.substring(0, outputPath.length() - 1);
					}
				} else if (args[i].startsWith(FORMAT)) {
					String t = args[i].substring(FORMAT.length());
					if (t.startsWith("\"")) {
						t = t.substring(1);
					}
					if (t.endsWith("\"")) {
						t = t.substring(0, t.length() - 1);
					}
					try {
						format = Format.valueOf(t);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				} else if (args[i].equals(NO_POST_BUILD)) {
					noPostBuild = true;
				}
			}
		}
		if (outputPath == null) {
			StringBuilder sb = new StringBuilder();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					sb.append(i).append(": ").append(args[i]).append("\n");
				}
			}
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Missing argument. Usage java " + FlexoDocGeneratorMain.class.getName() + " " + RESOURCE_PATH_ARGUMENT_PREFIX
						+ " " + OUTPUT_FILE_ARGUMENT_PREFIX + " " + DOC_TYPE_ARGUMENT_PREFIX + " " + TEMPLATES_ARGUMENT_PREFIX + " " + "\n"
						+ (args.length > 0 ? sb.toString() : "No arguments !!!"));
			}
			if (outputPath == null) {
				throw new MissingArgumentException(OUTPUT_FILE_ARGUMENT_PREFIX);
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
				output = FileUtils.createTempDirectory("DocOutput", "");
			} catch (IOException e) {
				e.printStackTrace();
				setExitCodeCleanUpAndExit(LOCAL_IO_EXCEPTION);
				return;
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Working directory is: " + output.getAbsolutePath());
		}
		File latexDir = new File(output, "Doc" + format.name() + "Source");
		File pdfDir = new File(output, format == Format.LATEX ? "PDF" : "ZIP");
		latexDir.mkdirs();
		pdfDir.mkdirs();
		if (project != null) {
			project.setComputeDiff(false);
			AddGeneratedCodeRepository add = AddGeneratedCodeRepository.actionType.makeNewAction(editor.getProject().getGeneratedDoc(),
					null, editor);
			if (editor.getProject().getDocTypeNamed(docType) == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Doc type '" + docType + "' cannot be found in project! Let's create it temporarily in this project");
				}
				AddDocType addDocType = AddDocType.actionType.makeNewAction(editor.getProject(), null, editor);
				addDocType.setNewName(docType);
				addDocType.doAction();
				add.setNewDocType(addDocType.getNewDocType());
			} else {
				add.setNewDocType(editor.getProject().getDocTypeNamed(docType));
			}
			add.setNewGeneratedCodeRepositoryName("FlexoServerGeneratedDoc" + new Random().nextInt(1000000));
			add.setNewGeneratedCodeRepositoryDirectory(latexDir);
			add.setFormat(format);
			add.doAction();
			DGRepository repository = (DGRepository) add.getNewGeneratedCodeRepository();
			repository.getPostBuildRepository(); // Initiates
			// the
			// repository
			repository.setPostBuildDirectory(pdfDir);
			repository.setPostProductName(repository.getName() + format.getPostBuildFileExtension());
			repository.setManageHistory(false);
			if (!add.hasActionExecutionSucceeded()) {
				handleActionFailed(add);
			}
			if (templates != null) {
				AddCustomTemplateRepository custom = AddCustomTemplateRepository.actionType.makeNewAction(project.getGeneratedDoc()
						.getTemplates(), null, editor);
				custom.setNewCustomTemplatesRepositoryName("FlexoServerTemplates");
				custom.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(project, "FlexoServerTemplates"));
				custom.doAction();
				if (!custom.hasActionExecutionSucceeded()) {
					handleActionFailed(custom);
				}
				ImportTemplates importTemplates = ImportTemplates.actionType.makeNewAction(project.getGeneratedDoc().getTemplates()
						.getApplicationRepository(), null, editor);
				importTemplates.setExternalTemplateDirectory(templates);
				importTemplates.setRepository(custom.getNewCustomTemplatesRepository());
				importTemplates.doAction();
				if (!importTemplates.hasActionExecutionSucceeded()) {
					handleActionFailed(importTemplates);
				}
				repository.setPreferredTemplateRepository(custom.getNewCustomTemplatesRepository());
				/*for (CGTemplate templateFile:custom.getNewCustomTemplatesRepository().getAllTemplateFiles()) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Imported "+templateFile.getTemplateFile().getAbsolutePath()+" as "+templateFile.getFileName()+" ["+templateFile.getSet().getName()+"]");
					}
				}*/
			}
			TOCRepository rep = null;
			if (tocUserID != null && tocFlexoID != -1 && project.getTOCData() != null) {
				rep = project.getTOCData().getRepositoryWithIdentifier(tocUserID, tocFlexoID);
				if (rep == null) {
					logger.severe("Cannot find TOCRepository : flexoID=" + tocFlexoID + " userID=" + tocUserID);
					reportMessage("Cannot find TOCRepository : flexoID=" + tocFlexoID + " userID=" + tocUserID);
					setExitCodeCleanUpAndExit(TOC_REPOSITORY_NOT_FOUND);
					return;
				}

			}
			if (rep == null) {
				AddTOCRepository tocRepository = AddTOCRepository.actionType.makeNewAction(project.getTOCData(), null, editor);
				tocRepository.setRepositoryName("Automatically generated table of content");
				tocRepository.setDocType(add.getNewDocType());
				tocRepository.doAction();
				if (!tocRepository.hasActionExecutionSucceeded()) {
					handleActionFailed(tocRepository);
				}
				rep = tocRepository.getNewRepository();
			}
			repository.setTocRepository(rep);
			docType = add.getNewDocType().toString();
			ConnectCGRepository connect = ConnectCGRepository.actionType.makeNewAction(add.getNewGeneratedCodeRepository(), null, editor);
			connect.doAction();
			List<FlexoAction<?, ?, ?>> actions = new ArrayList<FlexoAction<?, ?, ?>>();
			SynchronizeRepositoryCodeGeneration sync = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
					add.getNewGeneratedCodeRepository(), null, editor);
			sync.setSaveBeforeGenerating(false);
			actions.add(sync);
			WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewAction(add.getNewGeneratedCodeRepository(),
					null, editor);
			write.setSaveBeforeGenerating(false);
			actions.add(write);
			final GenerateArtefact<?, ?> generateArtefact;
			if (!noPostBuild) {
				switch (format) {
				case LATEX:
					String latexCommand = LatexUtils.getDefaultLatex2PDFCommand(false); // Little hack for server (see bug #1006055)
					GeneratePDF pdf = GeneratePDF.actionType
							.makeNewAction((DGRepository) add.getNewGeneratedCodeRepository(), null, editor);
					pdf.setLatexCommand(latexCommand);
					pdf.setSaveBeforeGenerating(false);
					pdf.setLatexTimeOutInSeconds(120);
					actions.add(pdf);
					generateArtefact = pdf;
					break;
				case HTML:
					GenerateZip zip = GenerateZip.actionType.makeNewAction(add.getNewGeneratedCodeRepository(), null, editor);
					zip.setSaveBeforeGenerating(false);
					actions.add(zip);
					generateArtefact = zip;

					break;
				case DOCX:
					GenerateDocx docxAction = GenerateDocx.actionType.makeNewAction(add.getNewGeneratedCodeRepository(), null, editor);
					docxAction.setSaveBeforeGenerating(false);
					actions.add(docxAction);
					generateArtefact = docxAction;
					break;
				default:
					generateArtefact = null;
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("Unknown doc format");
					}
					setExitCodeCleanUpAndExit(UNEXPECTED_EXCEPTION);
					break;
				}
			} else {
				generateArtefact = null;
			}
			Runnable whenDone = new Runnable() {

				@Override
				public void run() {
					if (noPostBuild) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("No post build option is true, ignoring post build generation.\nReturning now.");
						}
						setExitCodeCleanUpAndExit(0);
						return;
					}
					if (generateArtefact instanceof GeneratePDF) {
						GeneratePDF pdf = (GeneratePDF) generateArtefact;
						if (pdf.getLatexErrorMessage() != null) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("PDF generation error: " + pdf.getLatexErrorMessage());
							}
						}
					}
					File postBuildFile = null;
					if (generateArtefact != null) {
						postBuildFile = generateArtefact.getArtefactFile();
					}
					if (postBuildFile == null) {
						setExitCodeCleanUpAndExit(POST_BUILD_ERROR);
						return;
					}
					try {
						FileUtils.copyFileToFile(postBuildFile, new File(outputPath));
					} catch (IOException e) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.severe("Could not copy " + postBuildFile.getAbsolutePath() + " to " + outputPath);
						}
						setExitCodeCleanUpAndExit(LOCAL_IO_EXCEPTION);
						return;
					}
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Documentation generated at " + outputPath);
					}
					setExitCodeCleanUpAndExit(0);
				}
			};
			editor.chainActions(actions, whenDone);
		} else {
			setExitCodeCleanUpAndExit(PROJECT_NOT_FOUND);
		}
	}

	public static void main(String[] args) {
		launch(FlexoDocGeneratorMain.class, args);
	}

	@Override
	protected String getName() {
		return "Doc generator";
	}

}
