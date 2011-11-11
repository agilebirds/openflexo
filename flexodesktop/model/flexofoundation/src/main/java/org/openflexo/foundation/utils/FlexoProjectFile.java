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
package org.openflexo.foundation.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectExternalRepository;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents a file relative to a project
 * 
 * @author sguerin
 * 
 */
public class FlexoProjectFile extends FlexoObject implements StringConvertable, Cloneable {

	private static final Logger logger = Logger.getLogger(FlexoProjectFile.class.getPackage().getName());

	private static final char fileSeparator = File.separator.charAt(0);

	private static final char alternateFileSeparator = (fileSeparator == '\\' ? '/' : '\\');

	public static String transformIntoValidPath(String aRelativePath) {
		return aRelativePath.replace(alternateFileSeparator, fileSeparator);
	}

	public static final StringEncoder.Converter<FlexoProjectFile> converter = new Converter<FlexoProjectFile>(FlexoProjectFile.class) {

		@Override
		public FlexoProjectFile convertFromString(String value) {
			return new FlexoProjectFile(transformIntoValidPath(value));
		}

		@Override
		public String convertToString(FlexoProjectFile value) {
			return value.toString();
		}

	};

	protected String relativePath;

	protected FlexoProject project;

	public FlexoProjectFile(File absoluteFile, File aProjectDirectory) {
		super();
		File temp = absoluteFile;
		relativePath = null;
		while ((!temp.equals(aProjectDirectory)) && (temp.getParentFile() != null)) {
			if (relativePath == null) {
				relativePath = temp.getName();
			} else {
				relativePath = temp.getName() + "/" + relativePath;
			}
			temp = temp.getParentFile();
		}
		if (temp.getParentFile() == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("File " + absoluteFile.getAbsolutePath() + " is not contained in project " + aProjectDirectory);
			// TODO: try to look into external repository
		}
	}

	public FlexoProjectFile(File absoluteFile, FlexoProject aProject) {
		this(absoluteFile, aProject.getProjectDirectory());
		project = aProject;
	}

	public FlexoProjectFile(String relativePathToProject) {
		super();
		if (relativePathToProject.indexOf(":") > -1) {
			repositoryName = relativePathToProject.substring(0, relativePathToProject.lastIndexOf(":"));
			relativePathToProject = relativePathToProject.substring(relativePathToProject.lastIndexOf(":") + 1);
		}
		relativePath = FileUtils.convertBackslashesToSlash(relativePathToProject);
	}

	public FlexoProjectFile(FlexoProject project, ProjectExternalRepository repository, String relativePathToRepository) {
		this(relativePathToRepository);
		setProject(project);
		setExternalRepository(repository);
	}

	public FlexoProjectFile(FlexoProject project, String relativePathToRepository) {
		this(relativePathToRepository);
		setProject(project);
	}

	private String repositoryName;

	// used as cache since getExternalRepositoryWithKey is very costly.
	private ProjectExternalRepository _externalRep;

	public ProjectExternalRepository getExternalRepository() {
		if (_externalRep != null)
			return _externalRep;
		if ((getProject() != null) && (repositoryName != null)) {
			return _externalRep = getProject().getExternalRepositoryWithKey(repositoryName);
		}
		return null;
	}

	public void setExternalRepository(ProjectExternalRepository repository) {
		this.repositoryName = repository.getIdentifier();
	}

	public FlexoProject getProject() {
		return project;
	}

	public void setProject(FlexoProject aProject) {
		project = aProject;
	}

	private File _cachedFile;

	public void clearCachedFile() {
		_cachedFile = null;
	}

	public File getFile() {
		if (_cachedFile != null)
			return _cachedFile;
		if (project != null) {
			ProjectExternalRepository rep = getExternalRepository();
			if (rep != null) {
				if (rep.getDirectory() != null)
					return _cachedFile = new File(rep.getDirectory(), relativePath);
				else {
					if (logger.isLoggable(Level.FINE))
						logger.fine("No directory defined for external repository");
					return null;
				}
			} else {
				return _cachedFile = new File(project.getProjectDirectory(), relativePath);
			}
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Project was not set for this FlexoProjectFile!");
			return null;
		}
	}

	public void setFile(File aFile) {
		_cachedFile = null;
		if (project != null) {
			File target = null;
			if (getExternalRepository() != null) {
				target = getExternalRepository().getDirectory();
			} else {
				target = project.getProjectDirectory();
			}
			File temp = aFile;
			String newRelativePath = null;
			while ((!temp.equals(target)) && (temp.getParentFile() != null)) {
				if (newRelativePath == null) {
					newRelativePath = temp.getName();
				} else {
					newRelativePath = temp.getName() + "/" + newRelativePath;
				}
				temp = temp.getParentFile();
			}
			if (temp.getParentFile() == null) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("File " + aFile.getAbsolutePath() + " is not contained in project " + project.getProjectDirectory()
							+ " or repository (target is: " + target.getAbsolutePath() + ")");
			} else
				relativePath = newRelativePath;
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Project was not set for this FlexoProjectFile!");
		}
	}

	public File getFile(File directory) {
		return new File(directory, relativePath);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = FileUtils.convertBackslashesToSlash(relativePath);
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public String getStringRepresentation() {
		ProjectExternalRepository rep = getExternalRepository();
		if (rep != null) {
			return rep.getIdentifier() + ":" + getRelativePath();
		}
		return getRelativePath();
	}

	public boolean nameIsValid() {
		return FileUtils.isStringValidForFileName(relativePath);
	}

	public void fixName() {
		if (!nameIsValid() && relativePath != null) {
			relativePath = FileUtils.getValidFileName(relativePath);
		}
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return converter;
	}

	@Override
	public Object clone() {
		return new FlexoProjectFile(getProject(), getExternalRepository(), getRelativePath());
	}
}
