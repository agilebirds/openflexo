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
package org.openflexo.fme.model;

import java.util.List;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a Diagram
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "Diagram")
public interface Diagram extends DiagramElement<Diagram, DrawingGraphicalRepresentation> {

	public static final String DATA_MODEL = "dataModel";
	public static final String ASSOCIATIONS = "associations";

	@Getter(value = DATA_MODEL, inverse = DataModel.DIAGRAM)
	@XMLElement
	public DataModel getDataModel();

	@Setter(value = DATA_MODEL)
	public void setDataModel(DataModel aDataModel);

	@Getter(value = ASSOCIATIONS, cardinality = Cardinality.LIST)
	@XMLElement(context = "Contained", primary = true)
	@Embedded
	public List<ConceptGRAssociation> getAssociations();

	@Setter(ASSOCIATIONS)
	public void setAssociations(List<ConceptGRAssociation> someAssociations);

	@Adder(ASSOCIATIONS)
	@PastingPoint
	public void addToAssociations(ConceptGRAssociation anAssociation);

	@Remover(ASSOCIATIONS)
	public void removeFromAssociations(ConceptGRAssociation anAssociation);

	public List<Concept> getUsedConcepts();
}
