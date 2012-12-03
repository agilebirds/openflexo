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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.TemporaryFlexoModelObject;
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
import org.openflexo.technologyadapter.emf.rm.FlexoEMFModelResource;

/**
 * EMF Model.
 * 
 * @author gbesancon
 */
public class EMFModel extends TemporaryFlexoModelObject implements FlexoModel<EMFMetaModel>, IFlexoOntologyModel {

	/** Resource. */
	protected final Resource resource;
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
	public EMFModel(EMFMetaModel metaModel, EMFModelConverter converter, Resource resource) {
		this.resource = resource;
		this.metaModel = metaModel;
		this.converter = converter;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getName()
	 */
	@Override
	public String getName() {
		return EMFModelURIBuilder.getName(resource);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.ontology.IFlexoOntologyObject#getUri()
	 */
	@Override
	public String getUri() {
		return EMFModelURIBuilder.getUri(resource);
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

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.TemporaryFlexoModelObject#getProject()
	 */
	@Override
	public FlexoProject getProject() {
		return project;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#setProject(org.openflexo.foundation.rm.FlexoProject)
	 */
	@Override
	public void setProject(FlexoProject aProject) {
		this.project = aProject;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.StorageResourceData#getFlexoResource()
	 */
	@Override
	public FlexoEMFModelResource getFlexoResource() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.StorageResourceData#save()
	 */
	@Override
	public void save() throws SaveResourceException {
		try {
			resource.save(null);
		} catch (IOException e) {
			throw new SaveResourceException(getFlexoResource());
		}
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#setFlexoResource(org.openflexo.foundation.rm.FlexoResource)
	 */
	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		// TODO Auto-generated method stub

	}
}
