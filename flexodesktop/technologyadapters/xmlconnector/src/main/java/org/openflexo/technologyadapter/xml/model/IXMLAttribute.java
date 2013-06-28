/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xml.model;

import java.lang.reflect.Type;


/**
 *
 * This interface defines additional methods to be defined by all XMLAttribute that 
 * will be manipulated by the XMLSaxHandler
 *
 * @author xtof
 *
 */

public interface IXMLAttribute {

	 public boolean isSimpleAttribute();
	 
	 public void addValue(IXMLIndividual<?, ?> indiv, Object value);
	 
	 public Type getAttributeType();
}
