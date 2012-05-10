package org.openflexo.builders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.dg.action.ReinjectDocx;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class FlexoReinjectDocxMain extends FlexoExternalMainWithProject {

	private static final Logger logger = FlexoLogger.getLogger(FlexoReinjectDocxMain.class.getPackage().getName());

	public static final String DOCXFILE_ARGUMENT_PREFIX = "-Docxfile=";

	// Variables
	private File docxFile = null;

	private ReinjectDocx docxAction;

	@Override
	protected void init(String[] args) throws MissingArgumentException {
		super.init(args);
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(DOCXFILE_ARGUMENT_PREFIX)) {
					docxFile = new File(args[i].substring(DOCXFILE_ARGUMENT_PREFIX.length()));
					if (!docxFile.exists()) {
						docxFile = null;
					}
				}
			}
		}

		if (docxFile == null) {
			StringBuilder sb = new StringBuilder();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					sb.append(i).append(": ").append(args[i]).append("\n");
				}
			}
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Missing argument. Usage java " + FlexoReinjectDocxMain.class.getName() + " " + RESOURCE_PATH_ARGUMENT_PREFIX
						+ " " + DOCXFILE_ARGUMENT_PREFIX + " " + "\n" + (args.length > 0 ? sb.toString() : "No arguments !!!"));
			}

			throw new MissingArgumentException(DOCXFILE_ARGUMENT_PREFIX);
		}
	}

	@Override
	protected void doRun() {
		if (project != null) {
			docxAction = ReinjectDocx.actionType.makeNewAction(editor.getProject().getGeneratedDoc(), null, editor);
			docxAction.setDocxToReinject(docxFile);
			List<FlexoAction<?, ?, ?>> actions = new ArrayList<FlexoAction<?, ?, ?>>();
			actions.add(docxAction);
			editor.chainActions(actions, new Runnable() {

				@Override
				public void run() {
					try {
						editor.getProject().save();

						// Ok, post feedback
						StringBuilder sb = new StringBuilder();
						sb.append(FlexoLocalization.localizedForKey("reinject_docx_succeed"));
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_updated_description") + ": "
								+ docxAction.getNumberOfDescriptionUpdated());
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_updated_name") + ": "
								+ docxAction.getNumberOfNameUpdated());
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_updated_tocentry_title") + ": "
								+ docxAction.getNumberOfTocEntryTitleUpdated());
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_updated_tocentry_content") + ": "
								+ docxAction.getNumberOfTocEntryContentUpdated());
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_not_found_object") + ": "
								+ docxAction.getNumberOfObjectNotFound());
						sb.append("\n" + FlexoLocalization.localizedForKey("number_of_updated_edition_pattern") + ": "
								+ docxAction.getNumberOfEPIUpdated());
						if (docxAction.hasError()) {
							sb.append("\n" + FlexoLocalization.localizedForKey("reinject_docx_warnings") + ":\n"
									+ docxAction.getErrorReport());
						}

						reportMessage(sb.toString());
						setExitCodeCleanUpAndExit(0);
					} catch (SaveResourceException e) {
						e.printStackTrace();
						if (logger.isLoggable(Level.SEVERE)) {
							logger.log(Level.SEVERE, "Could not save project after reinject", e);
						}
						setExitCodeCleanUpAndExit(FLEXO_ACTION_FAILED);
					}
				}
			});

		} else {
			setExitCodeCleanUpAndExit(PROJECT_NOT_FOUND);
		}
	}

	public static void main(String[] args) {
		launch(FlexoReinjectDocxMain.class, args);
	}

	@Override
	protected String getName() {
		return "Reinject docx";
	}

}
