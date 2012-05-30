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
package org.openflexo.docxparser;

import org.dom4j.Element;

public enum OpenXmlTag {
	w_sdt,
	w_sdtContent,
	w_p,
	w_pPr,
	w_numPr,
	w_ilvl,
	w_numId,
	w_pStyle,
	w_hyperlink,
	w_r,
	w_rPr,
	w_drawing,
	w_rStyle,
	w_color,
	w_shd,
	w_highlight,
	w_sz,
	w_szCs,
	w_b,
	w_u,
	w_i,
	w_jc,
	w_t,
	w_val,
	w_fill,
	w_tgtFrame,
	w_tooltip,
	w_anchor,
	r_id,
	r_embed,
	w_abstractNumId,
	w_tbl,
	w_tr,
	w_tc,
	OTHER;

	public static OpenXmlTag getOpenXmlTag(Element element) {
		try {
			return OpenXmlTag.valueOf(element.getQualifiedName().replaceAll(":", "_"));
		} catch (IllegalArgumentException e) {
			return OTHER;
		}
	}

	public static OpenXmlTag[] getStylePropertyTags() {
		return new OpenXmlTag[] { w_pStyle, w_rStyle, w_color, w_shd, w_highlight, w_sz, w_szCs, w_b, w_u, w_i, w_jc };
	}

	public String getPrefix() {
		return this.name().substring(0, this.name().indexOf('_'));
	}

	public String getTagName() {
		return this.name().substring(this.name().indexOf('_') + 1);
	}
}
