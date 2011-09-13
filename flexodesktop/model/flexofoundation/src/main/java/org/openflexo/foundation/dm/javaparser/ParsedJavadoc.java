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
package org.openflexo.foundation.dm.javaparser;

import java.util.Vector;


public interface ParsedJavadoc
{
	public String getComment();

	public void setComment(String comment);

	public Vector<? extends ParsedJavadocItem> getDocletTags();

	public Vector<? extends ParsedJavadocItem> getTagsByName(String name) ;

	public ParsedJavadocItem getTagByName(String name);

	public ParsedJavadocItem getTagByName(String tagName, String parameterName);

	// When insert set to true, insert at first position (related to tag name), otherwise put at the end
	public ParsedJavadocItem addTagForNameAndValue(String tagName, String parameterName, String value, boolean insert);

	public String getStringRepresentation();	 

}