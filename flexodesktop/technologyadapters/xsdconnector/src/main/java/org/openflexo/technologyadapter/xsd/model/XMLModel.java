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
package org.openflexo.technologyadapter.xsd.model;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.rm.XMLModelResource;

public class XMLModel extends XSOntology implements FlexoModel<XMLModel, XSDMetaModel> {

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());

	private XMLModelResource modelResource;

	public XMLModel(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI, xsdFile, adapter);
	}

	@Override
	public XSDMetaModel getMetaModel() {
		logger.warning("Access to meta model not implemented yet");
		return null;
	}

	/**
	 * Return the resource, as a {@link EMFModelResource}
	 */
	@Override
	public XMLModelResource getResource() {
		return modelResource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<XMLModel> resource) {
		modelResource = (XMLModelResource) resource;
	}

	public void save() throws SaveResourceException {
		getResource().save(null);
	}

}
