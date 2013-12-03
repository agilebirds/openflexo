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
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter.Cardinality;

/**
 * Represents an instance of a concept in emerging data model
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "Instance")
public interface Instance extends FMEModelObject {

	public static final String NAME = "name";
	public static final String CONCEPT = "concept";
	public static final String PROPERTY_VALUES = "propertyValues";
	public static final String DESCRIPTION = "description";

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

	public boolean containsKeyNamed(String name);
	
	public String buildDescription();
	
	@Getter(value = DESCRIPTION)
	@XMLAttribute
	public String getDescription();

	@Setter(value = DESCRIPTION)
	public void setDescription(String description);
	
	@Getter(value = PROPERTY_VALUES, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<PropertyValue> getPropertyValues();

	@Setter(PROPERTY_VALUES)
	public void setPropertyValues(List<PropertyValue> somePropertyValues);

	@Adder(PROPERTY_VALUES)
	@PastingPoint
	public void addToPropertyValues(PropertyValue aPropertyValue);

	@Remover(PROPERTY_VALUES)
	public void removeFromPropertyValues(PropertyValue aPropertyValue);
	
	@Finder(attribute = PropertyValue.KEY, collection = PROPERTY_VALUES)
	public PropertyValue getPropertyNamed(String propertyKey);
}
