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
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents a concept in emerging data model
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(ConceptImpl.class)
@XMLElement(xmlTag = "Concept")
public interface Concept extends FMEModelObject {

	public static final String NONE_CONCEPT = "None";

	public static final String NAME = "name";
	public static final String DATA_MODEL = "dataModel";
	public static final String INSTANCES = "instances";
	public static final String PROPERTIES = "properties";
	public static final String READ_ONLY = "read_only";
	public static final String HTML_LABEL = "html_label";

	@Getter(value = NAME)
	@XMLAttribute
	public String getName();

	@Setter(value = NAME)
	public void setName(String aName);

	@Getter(value = DATA_MODEL, inverse = DataModel.CONCEPTS)
	@XMLElement
	public DataModel getDataModel();

	@Setter(value = DATA_MODEL)
	public void setDataModel(DataModel aDataModel);

	@Getter(value = INSTANCES, cardinality = Cardinality.LIST, inverse = Instance.CONCEPT)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.IGNORE)
	@Embedded
	public List<Instance> getInstances();

	@Setter(INSTANCES)
	public void setInstances(List<Instance> someInstances);

	@Adder(INSTANCES)
	@PastingPoint
	public void addToInstances(Instance anInstance);

	@Remover(INSTANCES)
	public void removeFromInstances(Instance anInstance);

	@Finder(attribute = Instance.NAME, collection = INSTANCES)
	public Instance getInstanceNamed(String instanceName);

	public boolean isUsed();
	
	@Getter(value = READ_ONLY, defaultValue="true")
	@XMLAttribute
	public boolean getReadOnly();

	@Setter(value = READ_ONLY)
	public void setReadOnly(boolean readOnly);
	
	public String produceHtmlLabel(String label);

	@Getter(value = HTML_LABEL)
	@XMLAttribute
	public String getHtmlLabel();
	
	@Setter(value = HTML_LABEL)
	public void setHtmlLabel(String name);
	
	@Getter(value = PROPERTIES, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.IGNORE)
	@Embedded
	public List<PropertyDefinition> getProperties();

	@Setter(PROPERTIES)
	public void setProperties(List<PropertyDefinition> someProperties);

	@Adder(PROPERTIES)
	@PastingPoint
	public void addToProperties(PropertyDefinition aProperty);

	@Remover(PROPERTIES)
	public void removeFromProperties(PropertyDefinition aProperty);
	
}
