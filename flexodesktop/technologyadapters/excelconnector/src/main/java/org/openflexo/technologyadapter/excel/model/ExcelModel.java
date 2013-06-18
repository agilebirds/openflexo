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
package org.openflexo.technologyadapter.excel.model;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.xsd.rm.ExcelModelResource;

public class ExcelModel extends FlexoOntologyObjectImpl implements FlexoModel<ExcelModel, ExcelMetaModel> {

	protected static final Logger logger = Logger.getLogger(ExcelModel.class.getPackage().getName());

	private ExcelModelResource modelResource;


	/**
	 * Return the resource, as a {@link ExcelModelResource}
	 */
	@Override
	public ExcelModelResource getResource() {
		return modelResource;
	}
	
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFlexoOntology getFlexoOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayableDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResource(FlexoResource<ExcelModel> resource) {
		// TODO Auto-generated method stub
		
	}

	public ExcelMetaModel getMetaModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
