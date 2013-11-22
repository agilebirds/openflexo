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

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "DataModel")
public interface DataModel extends FMEModelObject {

	public static final String CONCEPTS = "concepts";

	@Getter(value = CONCEPTS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@Embedded
	public List<Concept> getConcepts();

	@Setter(CONCEPTS)
	public void setConcepts(List<Concept> someConcepts);

	@Adder(CONCEPTS)
	@PastingPoint
	public void addToConcepts(Concept aConcept);

	@Remover(CONCEPTS)
	public void removeFromConcepts(Concept aConcept);

	@Finder(attribute = Concept.NAME, collection = CONCEPTS)
	public Concept getConceptNamed(String conceptName);

}
