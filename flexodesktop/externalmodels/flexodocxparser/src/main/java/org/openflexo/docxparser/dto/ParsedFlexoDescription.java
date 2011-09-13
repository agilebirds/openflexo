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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openflexo.docxparser.dto.api.IParsedFlexoDescription;
import org.openflexo.docxparser.dto.api.IParsedFlexoObject;
import org.openflexo.docxparser.dto.api.IParsedHtml;


public class ParsedFlexoDescription implements IParsedFlexoDescription
{
	private IParsedFlexoObject flexoObject;
	private Map<String, IParsedHtml> htmlDescriptionsByTarget;

	public ParsedFlexoDescription(IParsedFlexoObject flexoObject)
	{
		this.flexoObject = flexoObject;
		this.flexoObject.setParsedFlexoDescription(this);
		this.htmlDescriptionsByTarget = new HashMap<String, IParsedHtml>();
	}

	@Override
	public void addHtmlDescription(String target, IParsedHtml html)
	{
		htmlDescriptionsByTarget.put(target!=null?target:"", html);
	}

	/**
	 * @param target
	 * @return the ParsedHtmlDescription associated to the target if any, null otherwise.
	 */
	@Override
	public IParsedHtml getHtmlDescription(String target)
	{
		return htmlDescriptionsByTarget.get(target!=null?target:"");
	}

	@Override
	public Set<String> getAllTargets()
	{
		return htmlDescriptionsByTarget.keySet();
	}

	@Override
	public IParsedFlexoObject getParsedFlexoObject()
	{
		return flexoObject;
	}
}
