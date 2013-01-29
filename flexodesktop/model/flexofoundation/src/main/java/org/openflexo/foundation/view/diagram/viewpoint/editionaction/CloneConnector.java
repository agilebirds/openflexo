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
package org.openflexo.foundation.view.diagram.viewpoint.editionaction;

import java.util.logging.Logger;

import org.openflexo.foundation.view.diagram.action.LinkSchemeAction;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

/**
 * This edition primitive addresses the duplication of a connector
 * 
 * @author sylvain
 * 
 */
public class CloneConnector extends AddConnector {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	public CloneConnector(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EditionActionType getEditionActionType() {
		return EditionActionType.CloneConnector;
	}

	@Override
	public String toString() {
		return "CloneConnector " + Integer.toHexString(hashCode()) + " patternRole=" + getPatternRole();
	}

}
