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
import org.openflexo.docxparser.dto.api.IParsedFlexoDescription;
import org.openflexo.docxparser.dto.api.IParsedFlexoName;
import org.openflexo.docxparser.dto.api.IParsedFlexoObject;
import org.openflexo.docxparser.dto.api.IParsedFlexoTitle;

public class ParsedFlexoObject implements IParsedFlexoObject {
	private String flexoId;
	private String userId;
	private IParsedFlexoDescription flexoDescription;
	private IParsedFlexoName flexoName;
	private IParsedFlexoTitle flexoTitle;
	private IParsedFlexoContent flexoContent;

	public ParsedFlexoObject(String flexoId, String userId) {
		this.flexoId = flexoId;
		this.userId = userId;
	}

	@Override
	public IParsedFlexoDescription getParsedFlexoDescription() {
		return flexoDescription;
	}

	@Override
	public IParsedFlexoName getParsedFlexoName() {
		return flexoName;
	}

	@Override
	public void setParsedFlexoDescription(IParsedFlexoDescription flexoDescription) {
		this.flexoDescription = flexoDescription;
	}

	@Override
	public void setParsedFlexoName(IParsedFlexoName flexoName) {
		this.flexoName = flexoName;
	}

	@Override
	public IParsedFlexoContent getParsedFlexoContent() {
		return flexoContent;
	}

	@Override
	public IParsedFlexoTitle getParsedFlexoTitle() {
		return flexoTitle;
	}

	@Override
	public void setParsedFlexoContent(IParsedFlexoContent flexoContent) {
		this.flexoContent = flexoContent;
	}

	@Override
	public void setParsedFlexoTitle(IParsedFlexoTitle flexoTitle) {
		this.flexoTitle = flexoTitle;
	}

	@Override
	public String getFlexoId() {
		return flexoId;
	}

	@Override
	public String getUserId() {
		return userId;
	}

}
