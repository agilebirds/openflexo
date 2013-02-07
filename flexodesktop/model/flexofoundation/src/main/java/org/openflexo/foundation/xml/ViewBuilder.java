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
package org.openflexo.foundation.xml;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.view.View;

/**
 * Builder for {@link View}
 * 
 * @author sguerin
 * 
 */
public class ViewBuilder {

	public View view = null;
	private ViewResource resource;

	/**
	 * Use this constructor to build an Operation Component
	 * 
	 * @param componentDefinition
	 */
	public ViewBuilder(ViewResource resource) {
		super();
		this.resource = resource;
		if (resource.isLoaded()) {
			view = resource.getView();
		}
	}

	public ViewResource getResource() {
		return resource;
	}

	public FlexoProject getProject() {
		if (resource != null) {
			return resource.getProject();
		}
		return null;
	}

}
