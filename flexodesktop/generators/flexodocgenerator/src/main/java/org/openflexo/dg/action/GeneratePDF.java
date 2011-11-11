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
package org.openflexo.dg.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.openflexo.dg.exception.PDFGenerationFailedException;
import org.openflexo.dg.latex.ProjectDocLatexGenerator;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.action.GCAction;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.IOExceptionOccuredException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.LatexUtils;
import org.openflexo.toolbox.LogListener;

public class GeneratePDF extends GCAction<GeneratePDF, DGRepository> implements LogListener {

	private static final Logger logger = Logger.getLogger(GeneratePDF.class.getPackage().getName());

	public static FlexoActionType<GeneratePDF, DGRepository, CGObject> actionType = new FlexoActionType<GeneratePDF, DGRepository, CGObject>(
			"generate_PDF", GENERATE_MENU, WAR_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public GeneratePDF makeNewAction(DGRepository repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new GeneratePDF(repository, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DGRepository repository, Vector<CGObject> globalSelection) {
			return repository.getFormat() == Format.LATEX;
		}

		@Override
		protected boolean isEnabledForSelection(DGRepository repository, Vector<CGObject> globalSelection) {
			if (repository.getFormat() != Format.LATEX)
				return false;
			ProjectDocLatexGenerator pg = (ProjectDocLatexGenerator) getProjectGenerator(repository);
			return pg != null && repository.getPostBuildDirectory() != null && pg.getProjectDocResource() != null
					&& pg.getProjectDocResource().getFile() != null && pg.getProjectDocResource().getFile().exists();
		}

	};

	static {
		FlexoModelObject.addActionForClass(GeneratePDF.actionType, DGRepository.class);
	}

	private String latexCommand;

	private File generatedPDF;

	private Integer latexTimeOutInSeconds;

	protected GeneratePDF(DGRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public ProjectDocLatexGenerator getProjectGenerator() {
		return (ProjectDocLatexGenerator) super.getProjectGenerator();
	}

	@Override
	public DGRepository getRepository() {
		return (DGRepository) super.getRepository();
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException {
		ProjectDocLatexGenerator pg = getProjectGenerator();
		pg.setAction(this);

		if (getSaveBeforeGenerating()) {
			getRepository().getProject().save();
		}

		logger.info("Generate PDF for " + getFocusedObject());
		makeFlexoProgress(FlexoLocalization.localizedForKey("generate") + " " + getFocusedObject().getPostProductName() + " "
				+ FlexoLocalization.localizedForKey("into") + " " + getFocusedObject().getPostBuildDirectory().getAbsolutePath(), 15);
		convertGifToPng();
		try {
			pg.addToLogListeners(this);
			pg.setLatexTimeOutInMillis(getLatexTimeOutInSeconds() * 1000);
			generatedPDF = pg.generatePDF(getLatexCommand());
			if (generatedPDF == null)
				throw new PDFGenerationFailedException(pg, getLatexErrorMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOExceptionOccuredException(e, pg);
		} finally {
			pg.removeFromLogListeners(this);
		}
		hideFlexoProgress();
	}

	private void convertGifToPng() {
		if (getRepository().isConnected()) {
			for (CGFile file : getRepository().getFiguresSymbolicDirectory().getFiles()) {
				if (file.getResource() != null && file.getResource().getFile() != null && file.getResource().getFile().exists()
						&& file.getResource().getFile().getName().toLowerCase().endsWith(".gif")) {
					String name = file.getResource().getFile().getName();
					if (name.toLowerCase().endsWith("gif"))
						name = "CONVERTED-GIF" + name.substring(0, name.length() - 3) + "png";
					File f = new File(file.getResource().getFile().getParentFile(), name);
					if (!f.exists() || f.lastModified() < file.getResource().getFile().lastModified()) {
						if (logger.isLoggable(Level.INFO))
							logger.info("Converting " + file.getResourceName() + " to " + f.getName());
						try {
							ImageIO.write(ImageIO.read(file.getResource().getFile()), "png", f);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			if (logger.isLoggable(Level.INFO))
				logger.info("Repository is not connected");
		}
	}

	public String getLatexCommand() {
		if (latexCommand == null)
			latexCommand = LatexUtils.getDefaultLatex2PDFCommand();
		return latexCommand;
	}

	public void setLatexCommand(String latexCommand) {
		this.latexCommand = latexCommand;
	}

	public File getGeneratedPDF() {
		return generatedPDF;
	}

	/**
	 * Overrides err
	 * 
	 * @see org.openflexo.toolbox.LogListener#err(java.lang.String)
	 */
	@Override
	public void err(String line) {
		errs.append(line).append('\n');
	}

	@Override
	public void warn(String line) {
		warn.append(line).append('\n');
	}

	/**
	 * Overrides log
	 * 
	 * @see org.openflexo.toolbox.LogListener#log(java.lang.String)
	 */
	@Override
	public synchronized void log(String line) {
		logs.append(line).append('\n');
	}

	public String getLatexErrorMessage() {
		if (errs.length() > 0) {
			int start = errs.indexOf("texify: ");
			if (start > -1) {
				int end = errs.indexOf("\n", start + 9);
				if (end > start)
					return errs.substring(start + 8, end);
				else
					return errs.substring(start + 8);
			}
			return errs.toString();
		}
		if (logs.indexOf("LaTeX Error: ") > -1) {
			int index = logs.lastIndexOf("LaTeX Error: ");
			int end = logs.indexOf("\n", index);
			if (end > index)
				return logs.substring(index, end);
			else
				return logs.substring(index);
		}
		return null;
	}

	private StringBuffer logs = new StringBuffer();

	private StringBuffer warn = new StringBuffer();

	private StringBuffer errs = new StringBuffer();

	public Integer getLatexTimeOutInSeconds() {
		if (latexTimeOutInSeconds == null)
			latexTimeOutInSeconds = 15;
		return latexTimeOutInSeconds;
	}

	public void setLatexTimeOutInSeconds(Integer latexTimeOutInSeconds) {
		this.latexTimeOutInSeconds = latexTimeOutInSeconds;
	}
}
