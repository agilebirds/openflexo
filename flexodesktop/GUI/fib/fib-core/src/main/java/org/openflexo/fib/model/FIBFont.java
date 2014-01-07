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

import java.awt.Font;
import java.lang.reflect.Type;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBFont.FIBFontImpl.class)
@XMLElement(xmlTag = "Font")
public interface FIBFont extends FIBWidget {

	@PropertyIdentifier(type = String.class)
	public static final String SAMPLE_TEXT_KEY = "sampleText";
	@PropertyIdentifier(type = boolean.class)
	public static final String ALLOWS_NULL_KEY = "allowsNull";

	@Getter(value = SAMPLE_TEXT_KEY)
	@XMLAttribute
	public String getSampleText();

	@Setter(SAMPLE_TEXT_KEY)
	public void setSampleText(String sampleText);

	@Getter(value = ALLOWS_NULL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsNull();

	@Setter(ALLOWS_NULL_KEY)
	public void setAllowsNull(boolean allowsNull);

	public static abstract class FIBFontImpl extends FIBWidgetImpl implements FIBFont {

		private String sampleText = "Sample for this font";

		private boolean allowsNull = false;

		public FIBFontImpl() {
		}

		@Override
		public String getBaseName() {
			return "FontSelector";
		}

		@Override
		public Type getDefaultDataClass() {
			return Font.class;
		}

		@Override
		public boolean getAllowsNull() {
			return allowsNull;
		}

		@Override
		public void setAllowsNull(boolean allowsNull) {
			FIBPropertyNotification<Boolean> notification = requireChange(ALLOWS_NULL_KEY, allowsNull);
			if (notification != null) {
				this.allowsNull = allowsNull;
				hasChanged(notification);
			}
		}

		/**
		 * @return the sampleText
		 */
		@Override
		public String getSampleText() {
			return sampleText;
		}

		/**
		 * @param sampleText
		 *            the sampleText to set
		 */
		@Override
		public void setSampleText(String sampleText) {
			this.sampleText = sampleText;
		}
	}
}
