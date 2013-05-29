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
package org.openflexo.technologyadapter.xsd.model;

public interface XSOntologyURIDefinitions {

	public static final String XS_ONTOLOGY_URI = "http://www.openflexo.org/xsd.xml";

	public static final String XS_DATAPROPERTY_NAMEPREFIX = "hasValue";

	public static final String XS_ATTRIBUTEPROPERTY_NAMEPREFIX = "hasAttribute";

	public static final String XS_HASCHILD_PROPERTY_NAME = "hasChild";

	public static final String XS_HASELEMENT_PROPERTY_URI = XS_ONTOLOGY_URI + "#" + XS_HASCHILD_PROPERTY_NAME;

	public static final String XS_HASPARENT_PROPERTY_NAME = "hasParent";

	public static final String XS_ISPARTOFELEMENT_PORPERTY_URI = XS_ONTOLOGY_URI + "#" + XS_HASPARENT_PROPERTY_NAME;

	public static final String XS_THING_URI = XS_ONTOLOGY_URI + "#Thing";

}
