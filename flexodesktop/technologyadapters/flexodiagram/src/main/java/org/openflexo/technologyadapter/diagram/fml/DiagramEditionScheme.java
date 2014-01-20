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
package org.openflexo.technologyadapter.diagram.fml;

import org.openflexo.foundation.viewpoint.TechnologySpecificEditionScheme;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;

/**
 * Represents a behavioural feature which triggers in the context of a diagram edition (interaction)
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
public interface DiagramEditionScheme extends TechnologySpecificEditionScheme {

	public static final String TOP_LEVEL = "topLevel";
	public static final String TARGET = "target";
	public static final String FROM_TARGET = "fromTarget";
	public static final String TO_TARGET = "toTarget";
	public static final String DIAGRAM = "diagram";

	@Override
	public DiagramTechnologyAdapter getTechnologyAdapter();

	@Implementation
	public static abstract class DiagramEditionSchemeImpl implements DiagramEditionScheme {

		@Override
		public DiagramTechnologyAdapter getTechnologyAdapter() {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		}
	}

}
