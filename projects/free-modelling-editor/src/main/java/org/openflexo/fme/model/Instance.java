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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement(xmlTag = "Instance")
public interface Instance extends FMEModelObject {

	public static final String NAME = "name";
	public static final String CONCEPT = "concept";

	@Getter(value = NAME)
	@XMLAttribute
	public String getName();

	@Setter(value = NAME)
	public void setName(String aName);

	@Getter(value = CONCEPT, inverse = Concept.INSTANCES)
	@XMLElement
	public Concept getConcept();

	@Setter(CONCEPT)
	public void setConcept(Concept concept);

}
