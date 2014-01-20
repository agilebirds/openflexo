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
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.model.action.LinkSchemeAction;

/**
 * This edition primitive addresses the duplication of a connector
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/CloneConnectorPanel.fib")
@ModelEntity
@ImplementationClass(CloneConnector.CloneConnectorImpl.class)
@XMLElement
public interface CloneConnector extends AddConnector {

	public static abstract class CloneConnectorImpl extends AddConnectorImpl implements CloneConnector {

		private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

		public CloneConnectorImpl() {
			super();
		}

		@Override
		public String toString() {
			return "CloneConnector " + Integer.toHexString(hashCode()) + " patternRole=" + getPatternRole();
		}

	}
}
