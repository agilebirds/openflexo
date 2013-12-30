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
package org.openflexo.technologyadapter.diagram.model;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;

/**
 * Represents a diagram in Openflexo build-in diagram technology<br>
 * 
 * Note that a {@link Diagram} may conform to a {@link DiagramSpecification}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(DiagramImpl.class)
@XMLElement(xmlTag = "Diagram")
public interface Diagram extends DiagramContainerElement<DrawingGraphicalRepresentation>, FlexoModel<Diagram, DiagramSpecification>,
		ResourceData<Diagram> {

	public static final String URI = "uri";
	public static final String TITLE = "title";
	public static final String DIAGRAM_SPECIFICATION = "diagramSpecification";
	public static final String RESOURCE = "resource";

	/**
	 * Return title of this diagram
	 * 
	 * @return
	 */
	@Getter(value = TITLE)
	@XMLAttribute
	public String getTitle();

	/**
	 * Sets title of this diagram
	 * 
	 * @param aName
	 */
	@Setter(value = TITLE)
	public void setTitle(String aTitle);

	/**
	 * Return URI of this diagram
	 * 
	 * @return
	 */
	@Override
	@Getter(value = URI)
	@XMLAttribute
	public String getURI();

	/**
	 * Sets URI of this diagram
	 * 
	 * @param aName
	 */
	@Setter(value = URI)
	public void setURI(String anURI);

	/**
	 * Return the diagram specification of this diagram (might be null)
	 * 
	 * @return
	 */
	@Getter(value = DIAGRAM_SPECIFICATION, ignoreType = true)
	public DiagramSpecification getDiagramSpecification();

	/**
	 * Sets the diagram specification of this diagram (might be null)
	 * 
	 * @param aName
	 */
	@Setter(value = DIAGRAM_SPECIFICATION)
	public void setDiagramSpecification(DiagramSpecification aDiagramSpecification);

	/**
	 * Return resource for this diagram
	 * 
	 * @return
	 */
	@Override
	@Getter(value = RESOURCE, ignoreType = true)
	public FlexoResource<Diagram> getResource();

	/**
	 * Sets resource for this diagram
	 * 
	 * @param aName
	 */
	@Override
	@Setter(value = RESOURCE)
	public void setResource(FlexoResource<Diagram> aDiagramResource);

	public DiagramFactory getDiagramFactory();
}
