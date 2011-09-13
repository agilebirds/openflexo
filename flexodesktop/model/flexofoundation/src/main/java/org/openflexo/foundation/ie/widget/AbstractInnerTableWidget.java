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
package org.openflexo.foundation.ie.widget;

import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.logging.FlexoLogger;

/**
 * Abstract class representing a component that can be embedded in a table
 * 
 * This class has no purpose anymore but is still kept for backward
 * compatibility and for controllers that still expect it. (note by GPO)
 * 
 * @author bmangez
 */
public abstract class AbstractInnerTableWidget extends IEWidget {
	
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger
			.getLogger(AbstractInnerTableWidget.class.getPackage().getName());

	/**
	 * @param parent
	 * 
	 */
	public AbstractInnerTableWidget(IEWOComponent woComponent, IEObject parent,
			FlexoProject prj) {
		super(woComponent, parent, prj);
	}

}
