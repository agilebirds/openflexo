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
package org.openflexo.oo3;

import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OO3Style implements XMLSerializable {
	public Vector<OO3StyleProperty> properties;

	public OO3InheritedStyle inheritedStyle;

	public OO3Style() {
		super();
		properties = new Vector<OO3StyleProperty>();
		inheritedStyle = null;
	}

	public OO3Style(OO3NamedStyles.OO3NamedStyle style) {
		super();
		properties = null;
		inheritedStyle = new OO3InheritedStyle(style);
	}

	public void addStringProperty(String key, String value) {
		properties.add(new OO3StyleProperty(key, value));
	}

	public void addColorProperty(String key, OO3Color color) {
		properties.add(new OO3StyleProperty(key, color));
	}

	public static OO3Style getHighLightStyle() {
		OO3Style returned = new OO3Style();
		returned.addColorProperty("text-background-color", OO3Color.rgbColor(1, 1, (float) 0.4));
		return returned;
	}

	public static OO3Style getCitationStyle() {
		OO3Style returned = new OO3Style();
		returned.addColorProperty("underline-color", OO3Color.rgbColor(0, 0, 1));
		returned.addStringProperty("underline-style", "thick");
		return returned;
	}

	public static OO3Style getEmphasisStyle() {
		OO3Style returned = new OO3Style();
		returned.addStringProperty("font-italic", "yes");
		return returned;
	}

	public static OO3Style getNoteColumnStyle() {
		OO3Style returned = new OO3Style();
		returned.addColorProperty("font-fill", OO3Color.rgbColor((float) 0.33, (float) 0.33, (float) 0.33));
		returned.addStringProperty("font-italic", "yes");
		returned.addStringProperty("font-size", "11");
		return returned;
	}

	public static OO3Style createDefaultDocumentStyle() {
		OO3Style returned = new OO3Style();
		returned.addStringProperty("underline-style", "single");
		returned.addStringProperty("font-weight", "9");
		return returned;
	}

	public static class OO3StyleProperty implements XMLSerializable {
		public String value;

		public String key;

		public OO3Color colorValue;

		public OO3StyleProperty() {
			super();
		}

		public OO3StyleProperty(String k) {
			super();
			this.key = k;
		}

		public OO3StyleProperty(String k, String v) {
			this(k);
			this.value = v;
		}

		public OO3StyleProperty(String k, OO3Color cv) {
			this(k);
			this.colorValue = cv;
		}
	}

	public static class OO3InheritedStyle implements XMLSerializable {
		public String name;

		public OO3InheritedStyle() {
			super();
		}

		public OO3InheritedStyle(OO3NamedStyles.OO3NamedStyle style) {
			super();
			name = style.name;
		}
	}
}
