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
package org.openflexo.foundation.viewpoint.inspector;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents an inspector entry for a text area
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(TextAreaInspectorEntry.TextAreaInspectorEntryImpl.class)
@XMLElement(xmlTag = "TextArea")
public interface TextAreaInspectorEntry extends InspectorEntry {

	public static abstract class TextAreaInspectorEntryImpl extends InspectorEntryImpl implements TextAreaInspectorEntry {

		public TextAreaInspectorEntryImpl() {
			super();
		}

		@Override
		public Class getDefaultDataClass() {
			return String.class;
		}

		@Override
		public String getWidgetName() {
			return "TextArea";
		}
	}
}
