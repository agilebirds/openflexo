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

import org.openflexo.docxparser.dto.api.IParsedFlexoObject;
import org.openflexo.docxparser.dto.api.IParsedFlexoTitle;

public class ParsedFlexoTitle implements IParsedFlexoTitle {
	private IParsedFlexoObject flexoObject;
	private String flexoTitle;

	public ParsedFlexoTitle(IParsedFlexoObject flexoObject) {
		this.flexoObject = flexoObject;
		this.flexoObject.setParsedFlexoTitle(this);
	}

	@Override
	public String getFlexoTitle() {
		return flexoTitle;
	}

	@Override
	public void setFlexoTitle(String flexoTitle) {
		this.flexoTitle = flexoTitle;
	}

	@Override
	public IParsedFlexoObject getParsedFlexoObject() {
		return flexoObject;
	}
}
