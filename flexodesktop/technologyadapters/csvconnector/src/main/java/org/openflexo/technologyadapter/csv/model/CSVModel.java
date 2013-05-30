/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2013 Openflexo
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

package org.openflexo.technologyadapter.csv.model;

import java.io.File;
import java.util.List;
import org.openflexo.foundation.ontology.FlexoOntologyObjectImpl;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyAnnotation;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyContainer;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.ontology.IFlexoOntologyModel;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.csv.CSVTechnologyAdapter;
import org.openflexo.technologyadapter.csv.rm.CSVModelResource;

public class CSVModel extends FlexoOntologyObjectImpl
implements FlexoModel<CSVModel, CSVMetaModel>, IFlexoOntologyModel
{
	private CSVModelResource csvResource;

	public CSVModel(String uri, File file, CSVTechnologyAdapter technologyAdapter)
	{
	}

	public FlexoResource<CSVModel> getResource()
	{
		return this.csvResource;
	}

	public void setResource(FlexoResource<CSVModel> resource)
	{
		this.csvResource = ((CSVModelResource)resource);
	}

	public String getVersion()
	{
		return null;
	}

	public List<? extends IFlexoOntology> getImportedOntologies()
	{
		return null;
	}

	public List<? extends IFlexoOntologyAnnotation> getAnnotations()
	{
		return null;
	}

	public List<? extends IFlexoOntologyClass> getAccessibleClasses()
	{
		return null;
	}

	public List<? extends IFlexoOntologyIndividual> getAccessibleIndividuals()
	{
		return null;
	}

	public List<? extends IFlexoOntologyObjectProperty> getAccessibleObjectProperties()
	{
		return null;
	}

	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties()
	{
		return null;
	}

	public IFlexoOntologyConcept getDeclaredOntologyObject(String objectURI)
	{
		return null;
	}

	public IFlexoOntologyClass getDeclaredClass(String classURI)
	{
		return null;
	}

	public IFlexoOntologyIndividual getDeclaredIndividual(String individualURI)
	{
		return null;
	}

	public IFlexoOntologyObjectProperty getDeclaredObjectProperty(String propertyURI)
	{
		return null;
	}

	public IFlexoOntologyDataProperty getDeclaredDataProperty(String propertyURI)
	{
		return null;
	}

	public IFlexoOntologyStructuralProperty getDeclaredProperty(String objectURI)
	{
		return null;
	}

	public IFlexoOntologyClass getRootConcept()
	{
		return null;
	}

	public void setName(String name)
			throws Exception
			{
			}

	public List<? extends IFlexoOntologyContainer> getSubContainers()
	{
		return null;
	}

	public List<? extends IFlexoOntologyConcept> getConcepts()
	{
		return null;
	}

	public List<? extends IFlexoOntologyDataType> getDataTypes()
	{
		return null;
	}

	public IFlexoOntologyConcept getOntologyObject(String objectURI)
	{
		return null;
	}

	public IFlexoOntologyClass getClass(String classURI)
	{
		return null;
	}

	public IFlexoOntologyIndividual getIndividual(String individualURI)
	{
		return null;
	}

	public IFlexoOntologyObjectProperty getObjectProperty(String propertyURI)
	{
		return null;
	}

	public IFlexoOntologyDataProperty getDataProperty(String propertyURI)
	{
		return null;
	}

	public IFlexoOntologyStructuralProperty getProperty(String objectURI)
	{
		return null;
	}

	public List<? extends IFlexoOntologyClass> getClasses()
	{
		return null;
	}

	public List<? extends IFlexoOntologyIndividual> getIndividuals()
	{
		return null;
	}

	public List<? extends IFlexoOntologyDataProperty> getDataProperties()
	{
		return null;
	}

	public List<? extends IFlexoOntologyObjectProperty> getObjectProperties()
	{
		return null;
	}

	public List<IFlexoOntologyMetaModel> getMetaModels()
	{
		return null;
	}

	public CSVMetaModel getMetaModel()
	{
		return null;
	}

	public String getURI()
	{
		return null;
	}

	public Object getObject(String objectURI)
	{
		return null;
	}

	public TechnologyAdapter<?, ?> getTechnologyAdapter()
	{
		return null;
	}

	public String getName()
	{
		return null;
	}

	public IFlexoOntology getFlexoOntology()
	{
		return null;
	}

	public String getDisplayableDescription()
	{
		return null;
	}


	public String getFullyQualifiedName()
	{
		return null;
	}

	public void loadWhenUnloaded()
	{
	}
}
