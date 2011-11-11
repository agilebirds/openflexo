/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor;

public enum LinkTarget {
	NONE("None", null),
	NEW_WINDOW("New Window", "_blank"),
	TOPMOST_WINDOW("Topmost Window", "_top"),
	SAME_WINDOW("Same Window", "_self"),
	PARENT_WINDOW("Parent Window", "_parent");

	private String text;
	private String attrValue;

	LinkTarget(String text, String attrValue) {
		this.text = text;
		this.attrValue = attrValue;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}
};