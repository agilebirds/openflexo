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
package org.openflexo.foundation.cg.templates;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.StringUtils;

public class CGTemplateFile extends CGTemplate {

	protected static final Logger logger = FlexoLogger.getLogger(CGTemplateFile.class.getPackage().getName());

	private File _templateFile;
	private String fileContent = null;
	private TemplateFileContentEditor _templateFileContentEditor;
	private String _additionalPath = null;

	// Template last update
	private Date _lastUpdate;

	public CGTemplateFile(File templateFile, CGTemplateSet set, String relativePath) {
		super(set);
		_templateFile = templateFile;
		String fileName = templateFile.getName();
		_additionalPath = relativePath.substring(0, relativePath.indexOf(fileName));
		if (templateFile.exists()) {
			_lastUpdate = FileUtils.getDiskLastModifiedDate(templateFile);
		} else {
			logger.warning("Template declared for a non-existant file: " + templateFile.getAbsolutePath());
			_lastUpdate = new Date(0);
		}
	}

	public File getTemplateFile() {
		return _templateFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRelativePath() {
		if (getSet() instanceof TargetSpecificCGTemplateSet) {
			return ((TargetSpecificCGTemplateSet) getSet()).getTargetType().getName() + "/" + getRelativePathWithoutSetPrefix();
		}
		return getRelativePathWithoutSetPrefix();
	}

	@Override
	public String getRelativePathWithoutSetPrefix() {
		return getAdditionalPath() + getTemplateName();
	}

	private String getAdditionalPath() {
		return StringUtils.isEmpty(_additionalPath) ? "" : _additionalPath;
	}

	public boolean isEdited() {
		return _templateFileContentEditor != null;
	}

	public void edit(TemplateFileContentEditor templateFileContentEditor) {
		_templateFileContentEditor = templateFileContentEditor;
		templateFileContentEditor.setEditedContent(getContent());
		setChanged(false);
		notifyObservers(new TemplateFileEdited(this));
	}

	public void save() {
		try {
			synchronized (this) {
				FileUtils.saveToFile(getTemplateFile(), getEditedContent());
			}
			fileContent = getEditedContent();
			_templateFileContentEditor = null;
			setChanged();
			notifyObservers(new TemplateFileSaved(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cancelEdition() {
		_templateFileContentEditor.setEditedContent(getContent());
		_templateFileContentEditor = null;
		setChanged(false);
		notifyObservers(new TemplateFileEditionCancelled(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContent() {
		if (fileContent == null) {
			try {
				fileContent = FileUtils.fileContents(getTemplateFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent;
	}

	public String getEditedContent() {
		if (_templateFileContentEditor != null) {
			return _templateFileContentEditor.getEditedContent();
		} else {
			return getContent();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(boolean forceUpdate) {
		if (forceUpdate || fileContent == null || getIsVersionOnDiskSeemsNewer()) {
			// Then reload
			try {
				logger.info("Load content for " + getTemplateFile().getAbsolutePath());
				fileContent = FileUtils.fileContents(getTemplateFile());
				setChanged();
				notifyObservers(new TemplateFileChanged(this));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getIsVersionOnDiskSeemsNewer() {
		return getTemplateFile().lastModified() != getLastUpdate().getTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTemplateName() {
		return getTemplateFile().getName();
	}

	public interface TemplateFileContentEditor {
		public String getEditedContent();

		public void setEditedContent(String content);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.CG_TEMPLATE_FILE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}

	@Override
	public void setIsModified() {
		logger.info("setIsModified() for " + this);
		super.setIsModified();
		_lastUpdate = new Date();
		// Do this to reset dependant resources cache, in order to get up-to_date
		// needsGeneration information on generated resources
		getProject().notifyResourceStatusChanged(null);
	}

	@Override
	public final void delete() {
		if (getRepository().isApplicationRepository()) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot delete an application template!: " + getTemplateFile().getAbsolutePath());
			}
			return;
		}
		if (getTemplateFile() != null && getTemplateFile().exists()) {
			getTemplateFile().delete();
		}
		// _templateFile = null;
		super.delete();
		getRepository().refresh();
		setChanged();
		notifyObservers(new TemplateDeleted(this));
		deleteObservers();
	}

}
