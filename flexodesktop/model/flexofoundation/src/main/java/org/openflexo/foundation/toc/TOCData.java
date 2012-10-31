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
package org.openflexo.foundation.toc;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoTOCResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class TOCData extends TOCObject implements XMLStorageResourceData {

	private FlexoTOCResource resource;

	private FlexoProject project;

	private Vector<TOCRepository> repositories;

	public TOCData(FlexoTOCBuilder builder) {
		this(builder.getProject());
		initializeDeserialization(builder);
		builder.tocData = this;
		resource = builder.resource;
	}

	public TOCData(FlexoProject project) {
		super(project);
		this.project = project;
		repositories = new Vector<TOCRepository>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		for (TOCRepository rep : getRepositories()) {
			String attempt = rep.getTitle();
			int i = 1;
			while (getRepositoryWithTitle(attempt) != null && getRepositoryWithTitle(attempt) != rep) {
				attempt = rep.getTitle() + "-" + i++;
			}
			rep.setTitle(attempt);
		}
		super.finalizeDeserialization(builder);
	}

	@Override
	public TOCData getData() {
		return this;
	}

	@Override
	public String getClassNameKey() {
		return "toc_data";
	}

	@Override
	public FlexoTOCResource getFlexoResource() {
		return resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) {
		this.resource = (FlexoTOCResource) resource;
	}

	@Override
	public FlexoProject getProject() {
		return project;
	}

	@Override
	public void setProject(FlexoProject project) {
		this.project = project;
	}

	@Override
	public String getFullyQualifiedName() {
		return "TOC-DATA";
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return getFlexoResource();
	}

	@Override
	public void save() throws SaveResourceException {
		resource.saveResourceData();
	}

	public Vector<TOCRepository> getRepositories() {
		return repositories;
	}

	public void setRepositories(Vector<TOCRepository> repositories) {
		this.repositories = repositories;
	}

	public void addToRepositories(TOCRepository repository) {
		if (!repositories.contains(repository)) {
			repositories.add(repository);
			setChanged();
			notifyObservers(new TOCModification("repositories", null, repository));
		}
	}

	public void removeFromRepositories(TOCRepository repository) {
		repositories.remove(repository);
		setChanged();
		notifyObservers(new TOCModification("repositories", repository, null));
	}

	public static TOCData createNewTOCData(FlexoProject project) {
		TOCData newCG = new TOCData(project);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("createNewTOCData(), project=" + project + " " + newCG);
		}

		File cgFile = ProjectRestructuration.getExpectedTOCFile(project);
		FlexoProjectFile generatedCodeFile = new FlexoProjectFile(cgFile, project);
		FlexoTOCResource cgRes;
		try {
			cgRes = new FlexoTOCResource(project, newCG);
			cgRes.setResourceFile(generatedCodeFile);
		} catch (InvalidFileNameException e2) {
			e2.printStackTrace();
			generatedCodeFile = new FlexoProjectFile("TOC");
			generatedCodeFile.setProject(project);
			try {
				cgRes = new FlexoTOCResource(project, newCG);
				cgRes.setResourceFile(generatedCodeFile);
			} catch (InvalidFileNameException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not create TOC.");
				}
				e.printStackTrace();
				return null;
			}
		}

		try {
			cgRes.saveResourceData();
			project.registerResource(cgRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newCG;
	}

	public TOCRepository getRepositoryWithTitle(String title) {
		if (title == null) {
			return null;
		}
		for (TOCRepository rep : getRepositories()) {
			if (title.equals(rep.getTitle())) {
				return rep;
			}
		}
		return null;
	}

	public TOCRepository getRepositoryWithIdentifier(String uid, long flexoID) {
		if (uid == null) {
			return null;
		}
		Enumeration<TOCRepository> en = getRepositories().elements();
		while (en.hasMoreElements()) {
			TOCRepository rep = en.nextElement();
			if (uid.equals(rep.getUserIdentifier()) && flexoID == rep.getFlexoID()) {
				return rep;
			}
		}
		return null;
	}

}
