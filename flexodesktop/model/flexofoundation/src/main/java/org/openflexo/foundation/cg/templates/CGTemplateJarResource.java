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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.Inspectors;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.JavaResourceUtil;

/**
 * @author Nicolas Daniels
 *
 */
public class CGTemplateJarResource extends CGTemplate {

	protected static final Logger logger = FlexoLogger.getLogger(CGTemplateJarResource.class.getPackage().getName());

	private String resourcePath;
	private String relativePath;
	private String content;
	private Date lastUpdate;

	public CGTemplateJarResource(CGTemplateSet set, String folderPath, String resourcePath) {
		super(set);
		this.resourcePath = resourcePath;

		this.relativePath = resourcePath;
		if (this.relativePath.startsWith("/")) {
			this.relativePath = this.relativePath.substring(1);
		}

		if (folderPath != null) {
			if (folderPath.startsWith("/")) {
				folderPath = folderPath.substring(1);
			}

			this.relativePath = folderPath + (folderPath.endsWith("/") ? "" : "/") + this.relativePath;
		}

		lastUpdate = JavaResourceUtil.getResourceLastModifiedDate(resourcePath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContent() {
		if (content == null) {
			InputStream inputStream = null;
			try {
				inputStream = getClass().getResourceAsStream(resourcePath);
				content = FileUtils.fileContents(inputStream, null);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
		return content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getIsVersionOnDiskSeemsNewer() {
		return JavaResourceUtil.getResourceLastModifiedDate(resourcePath).after(lastUpdate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRelativePath() {
		return relativePath;
	}

	@Override
	public String getRelativePathWithoutSetPrefix() {
		return getRelativePath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTemplateName() {
		return resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(boolean forceUpdate) {
		if (forceUpdate || content == null || getIsVersionOnDiskSeemsNewer()) {
			// Then reload
			logger.info("Load content for " + getTemplateName());
			content = null;
			content = getContent();
			setChanged();
			notifyObservers(new TemplateFileChanged(this));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.GENERATORS.CG_TEMPLATE_FILE;
	}

	@Override
	public void setIsModified() {
		logger.info("setIsModified() for " + this);
		super.setIsModified();
		lastUpdate = new Date();
		// Do this to reset dependant resources cache, in order to get up-to_date
		// needsGeneration information on generated resources
		getProject().notifyResourceStatusChanged(null);
	}
}
