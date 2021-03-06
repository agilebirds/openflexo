package org.openflexo.builders;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.utils.FlexoBuilderListener;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.logging.FlexoLogger;

/**
 * This class allows to check the consistency of a project.
 * 
 * @author gpolet
 * 
 */
public class FlexoConsistencyCheckerMain extends FlexoExternalMainWithProject {

	private static final Logger logger = FlexoLogger.getLogger(FlexoConsistencyCheckerMain.class.getPackage().getName());

	public static final int CONSISTENCY_FAILED_RETURN_CODE = 1;

	public static final String CODE_TYPE_ARGUMENT_PREFIX = "-CodeType=";

	private String codeType;

	private ValidationReport[] reports;

	public FlexoConsistencyCheckerMain() {

	}

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(CODE_TYPE_ARGUMENT_PREFIX)) {
					codeType = args[i].substring(CODE_TYPE_ARGUMENT_PREFIX.length());
				}
			}
		}
	}

	@Override
	protected void doRun() {
		CodeType target = CodeType.get(codeType);
		if (target == null) {
			target = CodeType.PROTOTYPE;
		}
		reports = checkConsistency(projectDirectory, target);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < reports.length; i++) {
			ValidationReport report = reports[i];
			for (ValidationError error : report.getErrors()) {
				addError(error, sb);
			}
		}
		if (sb.length() > 0) {
			reportMessage(sb.toString());
			setExitCodeCleanUpAndExit(CONSISTENCY_FAILED_RETURN_CODE);
		}
		setExitCodeCleanUpAndExit(0);

	}

	public ValidationReport[] getReports() {
		return reports;
	}

	private void printTOCRepositories(FlexoEditor editor) throws ProjectInitializerException {
		if (editor.getProject().getTOCData() != null) {
			TOCData tocData = editor.getProject().getTOCData();
			if (tocData.getRepositories().size() > 0) {
				StringBuffer buffer = new StringBuffer("");
				Enumeration<TOCRepository> en = tocData.getRepositories().elements();
				while (en.hasMoreElements()) {
					TOCRepository rep = en.nextElement();
					buffer.append(rep.getTitle()).append(";");
					buffer.append(rep.getUserIdentifier()).append(";");
					buffer.append(rep.getFlexoID());
					if (en.hasMoreElements()) {
						buffer.append(";");
					}
				}
				writeToConsole(FlexoBuilderListener.TOCS_START_TAG);
				writeToConsole(buffer.toString());
				writeToConsole(FlexoBuilderListener.TOCS_END_TAG);
			}
		}
	}

	private void addError(ValidationError error, StringBuilder sb) {
		if (sb.length() == 0) {
			sb.append("The following issues have been found within your project:\n");
		}
		if (error.getCause() != null) {
			sb.append("* ").append(error.getCause().getLocalizedName()).append(": ").append(error.getLocalizedMessage()).append("\n");
		} else {
			sb.append("* ").append(error.getLocalizedMessage()).append("\n");
		}
	}

	@Override
	protected String getName() {
		return "Consistency checker";
	}

	/**
	 * This method checks the consistency for the project located within <code>projectDirectory</code> for the specified <code>target</code>
	 * 
	 * @param projectDirectory
	 *            the directory containing the project (the .rmxml file)
	 * @param target
	 *            the target to check against
	 * @return true if the consistency checks pass for the specified target.
	 * @throws CorruptedProjectException
	 * @throws ProjectLoadingCancelledException
	 */
	private ValidationReport[] checkConsistency(File projectDirectory, CodeType target) {
		long start = System.currentTimeMillis();
		FlexoProject project = editor.getProject();
		try {
			project.setTargetType(target);
			List<ValidationReport> reports = new ArrayList<ValidationReport>();
			if (project.getFlexoComponentLibrary(false) != null) {
				reports.add(project.getFlexoComponentLibrary().validate());
			}
			if (project.getFlexoWorkflow(false) != null) {
				reports.add(project.getFlexoWorkflow().validate());
			}
			if (project.getDataModel(false) != null) {
				reports.add(project.getDataModel().validate());
			}
			if (project.getDKVModel(false) != null) {
				reports.add(project.getDKVModel().validate());
			}
			long end = System.currentTimeMillis();
			boolean success = true;
			for (ValidationReport report : reports) {
				success &= report.getErrorNb() == 0;
				if (!success) {
					break;
				}
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Consistency checking took " + (end - start) / 1000 + " seconds for project located at: "
						+ projectDirectory.getAbsolutePath() + "\nResult: " + (success ? "Success" : "Failure"));
			}
			printTOCRepositories(editor);
			return reports.toArray(new ValidationReport[reports.size()]);
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			setExitCodeCleanUpAndExit(CORRUPTED_PROJECT_EXCEPTION);
		} finally {
			// Only executed if no error occur
			if (editor != null) {
				project.close();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		launch(FlexoConsistencyCheckerMain.class, args);
	}

}
