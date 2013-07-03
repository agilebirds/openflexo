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

import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResource;
import org.openflexo.foundation.view.diagram.model.DiagramFactory;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DiagramBuilder extends VirtualModelInstanceBuilder {

	private DiagramFactory factory;

	/**
	 * Use this constructor to build an Operation Component
	 * 
	 * @param componentDefinition
	 */
	public DiagramBuilder(ViewResource viewResource, VirtualModelInstanceResource virtualModelResource, DiagramFactory factory) {
		super(viewResource, virtualModelResource);
		this.factory = factory;
	}

	public DiagramFactory getFactory() {
		return factory;
	}
}
