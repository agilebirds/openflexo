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

/**
 * Represents a property value, as s String value associated with a String key
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "PropertyValue")
public interface PropertyValue extends FMEModelObject {

	public static final String KEY = "key";
	public static final String VALUE = "value";

	@Getter(value = KEY)
	@XMLAttribute
	public String getKey();

	@Setter(value = KEY)
	public void setKey(String aName);

	@Getter(value = VALUE)
	@XMLAttribute
	public String getValue();

	@Setter(value = VALUE)
	public void setValue(String aName);

}