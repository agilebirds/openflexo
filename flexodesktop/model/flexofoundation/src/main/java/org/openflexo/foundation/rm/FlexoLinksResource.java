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
package org.openflexo.foundation.rm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoLinks;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoLinksBuilder;


public class FlexoLinksResource extends FlexoXMLStorageResource<FlexoLinks> {

    private static final Logger logger = Logger.getLogger(FlexoLinksResource.class.getPackage().getName());

	public FlexoLinksResource(FlexoProjectBuilder builder) {
		this(builder.project);
	}

	public FlexoLinksResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoLinksResource(FlexoProject project, FlexoLinks links, FlexoProjectFile linksFile) throws InvalidFileNameException {
		this(project);
		setResourceFile(linksFile);
		_resourceData = links;
	}

	@Override
	public String getName() {
		return getProject().getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.LINKS;
	}

	@Override
	public Class<FlexoLinks> getResourceDataClass() {
		return FlexoLinks.class;
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Object instanciateNewBuilder() {
		FlexoLinksBuilder builder = new FlexoLinksBuilder(this);
		builder.links = _resourceData;
		return builder;
	}

	@Override
	protected void saveResourceData(boolean clearIsModified)
			throws org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException, SaveResourcePermissionDeniedException {
		if (!FlexoProject.SKOS_ENABLE) {
    		if (logger.isLoggable(Level.WARNING))
				logger.warning("SAVE IS NOT ENABLE on "+getResourceType());
    		return;
    	}
		super.saveResourceData(clearIsModified);
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

}
