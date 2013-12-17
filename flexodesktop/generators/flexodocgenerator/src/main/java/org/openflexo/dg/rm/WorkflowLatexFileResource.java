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
package org.openflexo.dg.rm;

import java.util.logging.Logger;

import org.openflexo.dg.latex.DGLatexGenerator;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class WorkflowLatexFileResource extends LatexFileResource<DGLatexGenerator<FlexoWorkflow>> implements FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(WorkflowLatexFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public WorkflowLatexFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public WorkflowLatexFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public String getName() {
		if (getCGFile() == null || getCGFile().getRepository() == null || getModel() == null) {
			return super.getName();
		}
		if (super.getName() == null) {
			setName(nameForRepositoryAndModel(getCGFile().getRepository(), getModel()));
		}
		return nameForRepositoryAndModel(getCGFile().getRepository(), getModel());
	}

	public static String nameForRepositoryAndModel(GenerationRepository repository, FlexoWorkflow model) {
		return repository.getName() + ".WORKFLOW_LATEX." + model.getWorkflowName();
	}

	public FlexoWorkflow getModel() {
		if (getGenerator() != null) {
			return getGenerator().getObject();
		}
		return null;
	}

	@Override
	protected LatexFile createGeneratedResourceData() {
		return new LatexFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToDependentResources(getProject().getTOCResource());
		addToDependentResources(getProject().getFlexoWorkflowResource());
	}

}
