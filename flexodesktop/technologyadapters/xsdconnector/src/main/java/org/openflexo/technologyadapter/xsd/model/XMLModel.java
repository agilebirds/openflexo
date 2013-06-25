/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.xml.model.IXMLModel;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.technologyadapter.xsd.XSDModelSlot;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

public class XMLModel extends XSOntology implements IXMLModel<XMLModel, XSDMetaModel> {

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());

	public XMLModel(String ontologyURI, File xmlFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI, xmlFile, adapter);
	}
	
	@Override
	public XSDMetaModel getMetaModel() {
		logger.warning("Access to meta model not implemented yet");
		return null;
	}
	
	@Override
	public List<IFlexoOntologyMetaModel> getMetaModels() {
		logger.warning("Access to meta model not implemented yet");
		return null;
	}


	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		public XMLFileResource<XSDTechnologyContextManager> getFlexoResource() {
			return (XMLFileResource<XSDTechnologyContextManager>) modelResource;
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
			if (resource instanceof XSDMetaModelResource) {
				this.modelResource = (XMLFileResource<XSDTechnologyContextManager>) resource;
			}
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		@Override
		public org.openflexo.foundation.resource.FlexoResource<XMLModel> getResource() {
			return (FlexoResource<XMLModel>) getFlexoResource();
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		@Override
		public void setResource(org.openflexo.foundation.resource.FlexoResource<XMLModel> resource) {
			try {
				setFlexoResource((FlexoResource<?>) resource);
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
		}


	public void save() throws SaveResourceException {
		getResource().save(null);
	}

	@Override
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties() {
		// Those should only be available for MetaModels
		return Collections.emptyList();
	}

	@Override
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI) {
		return null;
	}

	@Override
	public List<? extends IFlexoOntologyDataProperty> getDataProperties() {
		// Those should only be available for MetaModels
		return Collections.emptyList();
	}

}
