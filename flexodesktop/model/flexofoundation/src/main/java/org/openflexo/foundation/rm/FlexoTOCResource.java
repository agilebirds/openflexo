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

import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public class FlexoTOCResource extends FlexoXMLStorageResource<TOCData> {

	public FlexoTOCResource(FlexoProject project, TOCData tocData) {
		this(project);
		_resourceData = tocData;
		tocData.setFlexoResource(this);
	}

	public FlexoTOCResource(FlexoProject project) {
		super(project, project.getServiceManager());
	}

	public FlexoTOCResource(FlexoProjectBuilder projectBuilder) {
		super(projectBuilder);
		projectBuilder.notifyResourceLoading(this);
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.TOC;
	}

	@Override
	public Class getResourceDataClass() {
		return TOCData.class;
	}

	@Override
	public Object instanciateNewBuilder() {
		FlexoTOCBuilder builder = new FlexoTOCBuilder(this);
		builder.tocData = _resourceData;
		return builder;
	}

	@Override
	public boolean hasBuilder() {
		return true;
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
