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

import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

public class XSDMetaModel extends XSOntology implements FlexoMetaModel<XSDMetaModel> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDMetaModel.class.getPackage()
			.getName());

	private XSDMetaModelResource metaModelResource;

	public XSDMetaModel(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI, xsdFile, adapter);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setIsReadOnly(boolean b) {
	}

	public void save() throws SaveResourceException {
		logger.warning("XSDMetaModels are not supposed to be saved !!!");
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	public XSDMetaModelResource getFlexoResource() {
		return metaModelResource;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof XSDMetaModelResource) {
			this.metaModelResource = (XSDMetaModelResource) resource;
		}
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public org.openflexo.foundation.resource.FlexoResource<XSDMetaModel> getResource() {
		return getFlexoResource();
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<XSDMetaModel> resource) {
		try {
			setFlexoResource((FlexoResource<?>) resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

}
