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
package org.openflexo.fib.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBEditorPane.FIBEditorPaneImpl.class)
@XMLElement(xmlTag = "EditorPane")
public interface FIBEditorPane extends FIBTextWidget {

	public enum ContentType {
		PLAIN("text/plain"), HTML("text/html"), RTF("text/rtf");
		private final String contentType;

		private ContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getContentType() {
			return contentType;
		}
	}

	@PropertyIdentifier(type = ContentType.class)
	public static final String CONTENT_TYPE_KEY = "contentType";

	@Getter(value = CONTENT_TYPE_KEY)
	@XMLAttribute
	public ContentType getContentType();

	@Setter(CONTENT_TYPE_KEY)
	public void setContentType(ContentType contentType);

	public static abstract class FIBEditorPaneImpl extends FIBTextWidgetImpl implements FIBEditorPane {

		private ContentType contentType;

		@Override
		public String getBaseName() {
			return "EditorPane";
		}

		@Override
		public ContentType getContentType() {
			return contentType;
		}

		@Override
		public void setContentType(ContentType contentType) {
			FIBPropertyNotification<ContentType> notification = requireChange(CONTENT_TYPE_KEY, contentType);
			if (notification != null) {
				this.contentType = contentType;
				hasChanged(notification);
			}
		}

	}
}
