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
package org.openflexo.technologyadapter.diagram.fml.editionaction;

import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementPatternRole;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.toolbox.StringUtils;

public abstract class AddSchemaElementAction<T extends DiagramElement<?>> extends DiagramAction<T> {

	public AddSchemaElementAction() {
		super();
	}

	/*@Override
	protected void updatePatternRoleType()
	{
	}*/

	@Override
	public GraphicalElementPatternRole<?, ?> getPatternRole() {
		PatternRole<?> superPatternRole = super.getPatternRole();
		if (superPatternRole instanceof GraphicalElementPatternRole) {
			return (GraphicalElementPatternRole<?, ?>) superPatternRole;
		} else if (superPatternRole != null) {
			// logger.warning("Unexpected pattern role of type " + superPatternRole.getClass().getSimpleName());
			return null;
		}
		return null;
	}

	protected String getGraphicalElementSpecificationFMLRepresentation(FMLRepresentationContext context) {

		if (getPatternRole() != null) {
			if (getPatternRole().getGrSpecifications().size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (GraphicalElementSpecification ges : getPatternRole().getGrSpecifications()) {
					if (ges.getValue().isSet()) {
						sb.append("  " + ges.getFeatureName() + " = " + ges.getValue().toString() + ";" + StringUtils.LINE_SEPARATOR);
					}
				}
				return sb.toString();
			}
		}
		return null;
	}

}
