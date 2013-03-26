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


public class FIBEditorPane extends FIBTextWidget {

	public enum ContentType {
		PLAIN("text/plain"), HTML("text/html"), RTF("text/rtf");
		private String contentType;

		private ContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getContentType() {
			return contentType;
		}
	}

	private ContentType contentType;

	public static enum Parameters implements FIBModelAttribute {
		contentType;
	}

	public FIBEditorPane() {
	}

	@Override
	protected String getBaseName() {
		return "EditorPane";
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		FIBAttributeNotification<ContentType> notification = requireChange(Parameters.contentType, contentType);
		if (notification != null) {
			this.contentType = contentType;
			hasChanged(notification);
		}
	}

}
