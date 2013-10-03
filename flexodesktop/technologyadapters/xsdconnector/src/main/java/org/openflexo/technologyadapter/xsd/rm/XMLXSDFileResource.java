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

package org.openflexo.technologyadapter.xsd.rm;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.technologyadapter.xml.rm.XMLFileResourceImpl;
import org.openflexo.technologyadapter.xsd.XSDTechnologyContextManager;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
 

/**
 * @author xtof
 *
 */

@ModelEntity
@ImplementationClass(XMLXSDFileResourceImpl.class)
public interface XMLXSDFileResource extends FlexoFileResource<XMLXSDModel>,  TechnologyAdapterResource<XMLXSDModel>, FlexoModelResource<XMLXSDModel,XSDMetaModel> {

	public static final String TECHNOLOGY_CONTEXT_MANAGER = "XMLTechnologyContextManager";

	@Getter(value = TECHNOLOGY_CONTEXT_MANAGER, ignoreType = true)
	public XSDTechnologyContextManager getTechnologyContextManager();

	@Setter(TECHNOLOGY_CONTEXT_MANAGER)
	public void setTechnologyContextManager(XSDTechnologyContextManager technologyContextManager);
}

