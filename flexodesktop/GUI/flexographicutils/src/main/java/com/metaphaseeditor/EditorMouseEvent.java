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

import javax.swing.text.AttributeSet;

/**
 * 
 * @author Rudolf Visagie
 */
public class EditorMouseEvent {
	private AttributeSet nearestParagraphAttributes;

	public AttributeSet getNearestParagraphAttributes() {
		return nearestParagraphAttributes;
	}

	public void setNearestParagraphAttributes(AttributeSet nearestParagraphAttributes) {
		this.nearestParagraphAttributes = nearestParagraphAttributes;
	}
}
