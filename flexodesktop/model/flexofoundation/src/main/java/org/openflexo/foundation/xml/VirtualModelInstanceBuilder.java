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
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class VirtualModelInstanceBuilder extends XMLResourceDataBuilder<VirtualModelInstanceResource> {

	public VirtualModelInstance<?, ?> vmInstance;
	private ViewResource viewResource;

	/**
	 * Use this constructor to build an Operation Component
	 * 
	 * @param componentDefinition
	 */
	public VirtualModelInstanceBuilder(ViewResource viewResource, VirtualModelInstanceResource virtualModelResource) {
		super(virtualModelResource);
		this.viewResource = viewResource;
		if (virtualModelResource.isLoaded()) {
			vmInstance = virtualModelResource.getVirtualModelInstance();
		}
	}

	public View getView() {
		if (viewResource != null) {
			return viewResource.getView();
		}
		return null;
	}

	public VirtualModel getVirtualModel() {
		if (getResource() != null && getResource().getVirtualModelResource() != null) {
			return getResource().getVirtualModelResource().getVirtualModel();
		}
		return null;
	}

	public FlexoProject getProject() {
		if (getResource() != null) {
			return getResource().getProject();
		}
		return null;
	}

}
