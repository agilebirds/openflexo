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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.docxparser.dto.api.IParsedDocx;
import org.openflexo.docxparser.dto.api.IParsedFlexoContent;
import org.openflexo.docxparser.dto.api.IParsedFlexoDescription;
import org.openflexo.docxparser.dto.api.IParsedFlexoName;
import org.openflexo.docxparser.dto.api.IParsedFlexoObject;
import org.openflexo.docxparser.dto.api.IParsedFlexoTitle;

public class ParsedDocx implements IParsedDocx {
	private Map<String, IParsedFlexoObject> parsedFlexoObjectsByFlexoIdAndUserId;

	public ParsedDocx() {
		this.parsedFlexoObjectsByFlexoIdAndUserId = new HashMap<String, IParsedFlexoObject>();
	}

	@Override
	public IParsedFlexoObject getParsedFlexoObject(String flexoId, String userId) {
		return parsedFlexoObjectsByFlexoIdAndUserId.get(flexoId + "_" + userId);
	}

	@Override
	public Collection<IParsedFlexoObject> getAllParsedFlexoObjects() {
		return parsedFlexoObjectsByFlexoIdAndUserId.values();
	}

	public IParsedFlexoObject getOrCreateParsedFlexoObject(String flexoId, String userId) {
		IParsedFlexoObject parsedObject = getParsedFlexoObject(flexoId, userId);
		if (parsedObject == null) {
			parsedObject = new ParsedFlexoObject(flexoId, userId);
			parsedFlexoObjectsByFlexoIdAndUserId.put(flexoId + "_" + userId, parsedObject);
		}

		return parsedObject;
	}

	@Override
	public IParsedFlexoDescription getParsedDescription(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getParsedFlexoObject(flexoId, userId);
		return flexoObject != null ? flexoObject.getParsedFlexoDescription() : null;
	}

	/**
	 * @return the ParsedFlexoDescription associated to the flexoId if any, otherwise it will create a new one and return it.
	 */
	public IParsedFlexoDescription getOrCreateParsedDescription(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getOrCreateParsedFlexoObject(flexoId, userId);

		IParsedFlexoDescription parsedDescription = flexoObject.getParsedFlexoDescription();
		if (parsedDescription == null)
			parsedDescription = new ParsedFlexoDescription(flexoObject);

		return parsedDescription;
	}

	@Override
	public Collection<IParsedFlexoDescription> getAllParsedFlexoDescriptions() {
		List<IParsedFlexoDescription> list = new ArrayList<IParsedFlexoDescription>();
		for (IParsedFlexoObject flexoObject : getAllParsedFlexoObjects()) {
			if (flexoObject.getParsedFlexoDescription() != null)
				list.add(flexoObject.getParsedFlexoDescription());
		}
		return list;
	}

	/**
	 * @return the ParsedFlexoName associated to the flexoId if any, otherwise it will create a new one and return it.
	 */
	public IParsedFlexoName getOrCreateParsedName(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getOrCreateParsedFlexoObject(flexoId, userId);

		IParsedFlexoName parsedName = flexoObject.getParsedFlexoName();
		if (parsedName == null)
			parsedName = new ParsedFlexoName(flexoObject);

		return parsedName;
	}

	@Override
	public IParsedFlexoName getParsedName(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getParsedFlexoObject(flexoId, userId);
		return flexoObject != null ? flexoObject.getParsedFlexoName() : null;
	}

	@Override
	public Collection<IParsedFlexoName> getAllParsedFlexoNames() {
		List<IParsedFlexoName> list = new ArrayList<IParsedFlexoName>();
		for (IParsedFlexoObject flexoObject : getAllParsedFlexoObjects()) {
			if (flexoObject.getParsedFlexoName() != null)
				list.add(flexoObject.getParsedFlexoName());
		}
		return list;
	}

	/**
	 * @return the ParsedFlexoTitle associated to the flexoId if any, otherwise it will create a new one and return it.
	 */
	public IParsedFlexoTitle getOrCreateParsedTitle(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getOrCreateParsedFlexoObject(flexoId, userId);

		IParsedFlexoTitle parsedTitle = flexoObject.getParsedFlexoTitle();
		if (parsedTitle == null)
			parsedTitle = new ParsedFlexoTitle(flexoObject);

		return parsedTitle;
	}

	@Override
	public IParsedFlexoTitle getParsedTitle(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getParsedFlexoObject(flexoId, userId);
		return flexoObject != null ? flexoObject.getParsedFlexoTitle() : null;
	}

	@Override
	public Collection<IParsedFlexoTitle> getAllParsedFlexoTitles() {
		List<IParsedFlexoTitle> list = new ArrayList<IParsedFlexoTitle>();
		for (IParsedFlexoObject flexoObject : getAllParsedFlexoObjects()) {
			if (flexoObject.getParsedFlexoTitle() != null)
				list.add(flexoObject.getParsedFlexoTitle());
		}
		return list;
	}

	/**
	 * @return the ParsedFlexoContent associated to the flexoId if any, otherwise it will create a new one and return it.
	 */
	public IParsedFlexoContent getOrCreateParsedContent(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getOrCreateParsedFlexoObject(flexoId, userId);

		IParsedFlexoContent parsedContent = flexoObject.getParsedFlexoContent();
		if (parsedContent == null)
			parsedContent = new ParsedFlexoContent(flexoObject);

		return parsedContent;
	}

	@Override
	public IParsedFlexoContent getParsedContent(String flexoId, String userId) {
		IParsedFlexoObject flexoObject = getParsedFlexoObject(flexoId, userId);
		return flexoObject != null ? flexoObject.getParsedFlexoContent() : null;
	}

	@Override
	public Collection<IParsedFlexoContent> getAllParsedFlexoContents() {
		List<IParsedFlexoContent> list = new ArrayList<IParsedFlexoContent>();
		for (IParsedFlexoObject flexoObject : getAllParsedFlexoObjects()) {
			if (flexoObject.getParsedFlexoContent() != null)
				list.add(flexoObject.getParsedFlexoContent());
		}
		return list;
	}
}
