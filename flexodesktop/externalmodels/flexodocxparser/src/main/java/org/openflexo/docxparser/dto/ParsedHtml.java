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
import java.util.List;

import org.openflexo.docxparser.dto.api.IParsedHtml;
import org.openflexo.docxparser.dto.api.IParsedHtmlResource;

public class ParsedHtml implements IParsedHtml {
	private StringBuilder html;
	private List<IParsedHtmlResource> neededResources;

	public ParsedHtml() {
		this(null);
	}

	public ParsedHtml(String html) {
		this.html = new StringBuilder();
		if (html != null) {
			this.html.append(html);
		}
		this.neededResources = new ArrayList<IParsedHtmlResource>();
	}

	@Override
	public String getHtml() {
		return html.toString();
	}

	public void addNeededResource(IParsedHtmlResource resource) {
		neededResources.add(resource);
	}

	@Override
	public List<IParsedHtmlResource> getNeededResources() {
		return neededResources;
	}

	public ParsedHtml append(String html) {
		this.html.append(html);
		return this;
	}

	public ParsedHtml appendHtml(String html) {
		return append(html);
	}

	public ParsedHtml append(ParsedHtml parsedHtml) {
		this.appendHtml(parsedHtml.getHtml());
		for (IParsedHtmlResource resource : parsedHtml.getNeededResources()) {
			this.neededResources.add(resource);
		}
		return this;
	}
}
