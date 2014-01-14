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

import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.annotations.FIBPanel;

/**
 * This edition primitive addresses the duplication of a shape
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/CloneShapePanel.fib")
@ModelEntity
@ImplementationClass(CloneShape.CloneShapeImpl.class)
@XMLElement
public interface CloneShape extends AddShape{


public static abstract  class CloneShapeImpl extends AddShapeImpl implements CloneShape
{

	private static final Logger logger = Logger.getLogger(CloneShape.class.getPackage().getName());

	public CloneShapeImpl() {
		super();
	}

}
}
