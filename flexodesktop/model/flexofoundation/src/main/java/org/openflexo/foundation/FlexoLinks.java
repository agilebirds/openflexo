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
package org.openflexo.foundation;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoLinksResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoLinksBuilder;
import org.openflexo.logging.FlexoLogger;

public class FlexoLinks extends FlexoModelObject implements XMLStorageResourceData {

	private static final Logger logger = FlexoLogger.getLogger(FlexoLinks.class.getPackage().getName());

	public static FlexoLinks createLinks(FlexoProject project) {
		FlexoLinks links = new FlexoLinks(project);
		File linksFile = ProjectRestructuration.getExpectedLinksFile(project);
		FlexoProjectFile linksModelFile = new FlexoProjectFile(linksFile, project);
		FlexoLinksResource linksRes;
		try {
			linksRes = new FlexoLinksResource(project, links, linksModelFile);
		} catch (InvalidFileNameException e) {
			linksModelFile = new FlexoProjectFile(FileUtils.getValidFileName(linksModelFile.getRelativePath()));
			linksModelFile.setProject(project);
			try {
				linksRes = new FlexoLinksResource(project, links, linksModelFile);
			} catch (InvalidFileNameException e1) {
				if (logger.isLoggable(Level.SEVERE))
					logger.severe("Could not create Links resource. Name: " + linksModelFile.getRelativePath()
							+ " is not valid. This should never happen.");
				return null;
			}
		}
		try {
			linksRes.saveResourceData();
			project.registerResource(linksRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			e1.printStackTrace();
		}
		return links;
	}

	private FlexoLinksResource resource;
	private Vector<FlexoLink> links;

	private Hashtable<FlexoModelObject, Vector<FlexoLink>> referencesToObjects;

	public FlexoLinks(FlexoLinksBuilder builder) {
		this(builder.getProject());
	}

	protected FlexoLinks(FlexoProject project) {
		super(project);
		links = new Vector<FlexoLink>();
		referencesToObjects = new Hashtable<FlexoModelObject, Vector<FlexoLink>>();
	}

	@Override
	public Vector<FlexoLink> getLinks() {
		return links;
	}

	public void setLinks(Vector<FlexoLink> links) {
		this.links = links;
	}

	public void addToLinks(FlexoLink link) {
		links.add(link);
		link.setLinks(this);
		setChanged();
		if (link.getObject1().getObject() != null) {
			updateReferencesForObject(link.getObject1().getObject(), link);
		}
		if (link.getObject2().getObject() != null) {
			updateReferencesForObject(link.getObject2().getObject(), link);
		}
	}

	protected void updateReferencesForObject(FlexoModelObject object, FlexoLink link) {
		Vector<FlexoLink> l = referencesToObjects.get(object);
		if (l == null)
			referencesToObjects.put(object, l = new Vector<FlexoLink>());
		l.add(link);
	}

	public void removeFromLinks(FlexoLink link) {
		links.remove(link);
		link.setLinks(null);
		if (link.getObject1().getObject() != null) {
			Vector<FlexoLink> l = referencesToObjects.get(link.getObject1().getObject());
			if (l != null) {
				l.remove(link);
				if (link.getObject2().getObject() != null) {
					link.getObject2().getObject().notifyLinksChanged();
				}
			}
		}
		if (link.getObject2().getObject() != null) {
			Vector<FlexoLink> l = referencesToObjects.get(link.getObject2().getObject());
			if (l != null) {
				l.remove(link);
				if (link.getObject1().getObject() != null) {
					link.getObject1().getObject().notifyLinksChanged();
				}
			}
		}
		setChanged();
	}

	@Override
	public String getClassNameKey() {
		return "flexo_links";
	}

	@Override
	public String getFullyQualifiedName() {
		return "FLEXO_LINKS";
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getProject().getXmlMappings().getLinksMapping();
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return this;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() {
		return resource;
	}

	@Override
	public FlexoStorageResource getFlexoResource() {
		return resource;
	}

	@Override
	public void save() throws SaveResourceException {

	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		this.resource = (FlexoLinksResource) resource;
	}

	@Override
	public void setProject(FlexoProject aProject) {

	}

	public Vector<FlexoLink> getLinksFor(FlexoModelObject object) {
		Vector<FlexoLink> ret = referencesToObjects.get(object);

		return ret;
	}

}
