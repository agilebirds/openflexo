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
package org.openflexo.generator.rm;

import java.util.Date;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGTextFile;
import org.openflexo.generator.utils.PrototypeProcessBusinessDataSamplesGenerator;
import org.openflexo.toolbox.FileFormat;

public class PrototypeProcessBusinessDataSamplesFileResource extends
		TextFileResource<PrototypeProcessBusinessDataSamplesGenerator, CGTextFile> implements GenerationAvailableFileResource,
		FlexoObserver {

	public PrototypeProcessBusinessDataSamplesFileResource(FlexoProjectBuilder builder) {
		super(builder);
		setResourceFormat(FileFormat.TEXT);
	}

	public PrototypeProcessBusinessDataSamplesFileResource(FlexoProject aProject) {
		super(aProject);
		setResourceFormat(FileFormat.TEXT);
	}

	public void registerObserverWhenRequired() {
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {

	}

	@Override
	protected PrototypeProcessBusinessDataSamplesFile createGeneratedResourceData() {
		return new PrototypeProcessBusinessDataSamplesFile(getFile(), this);
	}

	@Override
	public PrototypeProcessBusinessDataSamplesFile getGeneratedResourceData() {
		return (PrototypeProcessBusinessDataSamplesFile) super.getGeneratedResourceData();
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof TemplateLocator) {
			return ((TemplateLocator) resource).needsUpdateForResource(this);
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}
}
