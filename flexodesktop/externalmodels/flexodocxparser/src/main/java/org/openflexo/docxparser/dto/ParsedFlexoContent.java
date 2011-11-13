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
package org.openflexo.docxparser.dto;

import org.openflexo.docxparser.dto.api.IParsedFlexoContent;
import org.openflexo.docxparser.dto.api.IParsedFlexoObject;
import org.openflexo.docxparser.dto.api.IParsedHtml;

public class ParsedFlexoContent implements IParsedFlexoContent {
	private IParsedFlexoObject flexoObject;
	private IParsedHtml flexoContent;

	public ParsedFlexoContent(IParsedFlexoObject flexoObject) {
		this.flexoObject = flexoObject;
		this.flexoObject.setParsedFlexoContent(this);
	}

	@Override
	public IParsedHtml getFlexoContent() {
		return flexoContent;
	}

	@Override
	public void setFlexoContent(IParsedHtml flexoContent) {
		this.flexoContent = flexoContent;
	}

	@Override
	public IParsedFlexoObject getParsedFlexoObject() {
		return flexoObject;
	}
}
