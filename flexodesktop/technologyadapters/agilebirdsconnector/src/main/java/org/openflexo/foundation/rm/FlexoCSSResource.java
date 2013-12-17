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
package org.openflexo.foundation.rm;

import java.net.MalformedURLException;

import javax.swing.text.html.StyleSheet;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;

public class FlexoCSSResource extends FlexoImportedResource<CSSResourceData> {
	private StyleSheet styleSheet = null;

	public FlexoCSSResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoCSSResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	protected CSSResourceData doImport() throws FlexoException {
		return new CSSResourceData(getProject(), this);
	}

	@Override
	public String getName() {
		return getFile().getName();
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.CSS_FILE;
	}

	public StyleSheet getStyleSheet() throws MalformedURLException {
		if (styleSheet == null) {
			styleSheet = new StyleSheet();
			styleSheet.importStyleSheet(getFile().toURI().toURL());
		}

		return styleSheet;
	}

}
