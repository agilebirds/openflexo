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
package org.openflexo.docxparser.dto.api;

import java.util.Collection;

public interface IParsedDocx {
	/**
	 * @return the ParsedFlexoDescription associated to the flexoId_userId if any, null otherwise.
	 */
	public IParsedFlexoDescription getParsedDescription(String flexoId, String userId);

	public Collection<IParsedFlexoDescription> getAllParsedFlexoDescriptions();

	/**
	 * @return the ParsedFlexoName associated to the flexoId_userId if any, null otherwise.
	 */
	public IParsedFlexoName getParsedName(String flexoId, String userId);

	public Collection<IParsedFlexoName> getAllParsedFlexoNames();

	/**
	 * @return the ParsedFlexoTitle associated to the flexoId_userId if any, null otherwise.
	 */
	public IParsedFlexoTitle getParsedTitle(String flexoId, String userId);

	public Collection<IParsedFlexoTitle> getAllParsedFlexoTitles();

	/**
	 * @return the ParsedFlexoContent associated to the flexoId_userId if any, null otherwise.
	 */
	public IParsedFlexoContent getParsedContent(String flexoId, String userId);

	public Collection<IParsedFlexoContent> getAllParsedFlexoContents();

	/**
	 * @return the ParsedFlexoObject associated to the flexoId_userId if any, null otherwise.
	 */
	public IParsedFlexoObject getParsedFlexoObject(String flexoId, String userId);

	public Collection<IParsedFlexoObject> getAllParsedFlexoObjects();
}
