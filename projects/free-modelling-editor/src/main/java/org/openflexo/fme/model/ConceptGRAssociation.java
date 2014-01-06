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

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents the association between a graphical representation and a concept in emerging DataModel.<br>
 * Note that we are here a meta-level: we type here a DiagramElement
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ConceptGRAssociation")
@ImplementationClass(ConceptGRAssociationImpl.class)
public interface ConceptGRAssociation extends FMEModelObject {

	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	public static final String CONCEPT = "concept";

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	@XMLElement
	public GraphicalRepresentation getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(GraphicalRepresentation graphicalRepresentation);

	@Getter(value = CONCEPT)
	@XMLElement
	public Concept getConcept();

	@Setter(CONCEPT)
	public void setConcept(Concept concept);

	public String getName();

	/*@DelegateImplementation
	public static abstract class APartialImplementation extends DelegatedImplementation implements ConceptGRAssociation {
		public String getName() {
			return toString();
		}
		@Override
		public String toString() {
			return "Association" + Integer.toHexString(hashCode());
		}
	}*/

	// public @interface Getter2 {}

}
