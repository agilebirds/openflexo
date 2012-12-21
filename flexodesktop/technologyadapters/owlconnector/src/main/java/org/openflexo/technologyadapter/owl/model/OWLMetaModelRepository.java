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
package org.openflexo.technologyadapter.owl.model;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;

/**
 * Stores all OWL ontologies which are defined in a given {@link FlexoResourceCenter}
 * 
 * @author sylvain
 * 
 */
public class OWLMetaModelRepository extends MetaModelRepository<OWLOntologyResource, OWLOntology, OWLOntology, OWLTechnologyAdapter> {
	private static final Logger logger = Logger.getLogger(OWLOntologyLibrary.class.getPackage().getName());

	public OWLMetaModelRepository(OWLTechnologyAdapter adapter, FlexoResourceCenter rc) {
		super(adapter, rc);
	}

}
