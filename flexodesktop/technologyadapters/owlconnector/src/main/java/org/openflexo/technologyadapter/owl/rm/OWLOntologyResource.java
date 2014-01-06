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
package org.openflexo.technologyadapter.owl.rm;

import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;

/**
 * Represents the resource associated to a {@link OWLOntology}
 * 
 * @author sguerin
 * 
 */
@ModelEntity
@ImplementationClass(OWLOntologyResourceImpl.class)
public interface OWLOntologyResource extends FlexoFileResource<OWLOntology>,
		FlexoModelResource<OWLOntology, OWLOntology, OWLTechnologyAdapter>,
		FlexoMetaModelResource<OWLOntology, OWLOntology, OWLTechnologyAdapter> {

	public static final String ONTOLOGY_LIBRARY = "ontologyLibrary";

	@Getter(value = ONTOLOGY_LIBRARY, ignoreType = true)
	public OWLOntologyLibrary getOntologyLibrary();

	@Setter(ONTOLOGY_LIBRARY)
	public void setOntologyLibrary(OWLOntologyLibrary ontologyLibrary);

}
