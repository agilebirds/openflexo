/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.ontology.IFlexoOntologyModel;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * EMF Model.
 * 
 * @author gbesancon
 */
public class EMFModel extends FlexoObject implements FlexoModel<EMFModel, EMFMetaModel>, IFlexoOntologyModel {

	/** Resource. */
	protected EMFModelResource modelResource;
	/** MetaModel. */
	protected final EMFMetaModel metaModel;
	/** Converter. */
	protected final EMFModelConverter converter;
	/** Project. */
	protected FlexoProject project;

	/**
	 * Constructor.
	 * 
	 * @param object
	 */
	public EMFModel(EMFMetaModel metaModel, EMFModelConverter converter, EMFModelResource resource) {
		this.metaModel = metaModel;
		this.converter = converter;
		this.modelResource = resource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getName()
	 */
	@Override
	public String getName() {
		return EMFModelURIBuilder.getName(modelResource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getURI()
	 */
	@Override
	public String getURI() {
		return EMFModelURIBuilder.getUri(modelResource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getImportedOntologies()
	 */
	@Override
	public List<IFlexoOntology> getImportedOntologies() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntology#getAnnotations()
	 */
	@Override
	public List<IFlexoOntologyAnnotation> getAnnotations() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getSubContainers()
	 */
	@Override
	public List<IFlexoOntologyContainer> getSubContainers() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getConcepts()
	 */
	@Override
	public List<IFlexoOntologyConcept> getConcepts() {
		List<IFlexoOntologyConcept> concepts = new ArrayList<IFlexoOntologyConcept>();
		concepts.addAll(getConverter().getIndividuals().values());
		return Collections.unmodifiableList(concepts);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyConceptContainer#getDataTypes()
	 */
	@Override
	public List<IFlexoOntologyDataType> getDataTypes() {
		return Collections.emptyList();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyModel#getMetaModels()
	 */
	@Override
	public List<IFlexoOntologyMetaModel> getMetaModels() {
		return Collections.unmodifiableList(Collections.singletonList((IFlexoOntologyMetaModel) metaModel));
	}

	/**
	 * Getter of metaModel.
	 * 
	 * @return the metaModel value
	 */
	@Override
	public EMFMetaModel getMetaModel() {
		return metaModel;
	}

	/**
	 * Getter of converter.
	 * 
	 * @return the converter value
	 */
	public EMFModelConverter getConverter() {
		return converter;
	}

	@Override
	@Deprecated
	public FlexoProject getProject() {
		// TODO should be removed from FlexoResourceData implementation
		return null;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public EMFModelResource getFlexoResource() {
		return modelResource;
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
		if (resource instanceof EMFModelResource) {
			this.modelResource = (EMFModelResource) resource;
		}
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public org.openflexo.foundation.resource.FlexoResource<EMFModel> getResource() {
		return getFlexoResource();
	}

	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
	// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
	// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<EMFModel> resource) {
		try {
			setFlexoResource((FlexoResource) resource);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
	}

}
