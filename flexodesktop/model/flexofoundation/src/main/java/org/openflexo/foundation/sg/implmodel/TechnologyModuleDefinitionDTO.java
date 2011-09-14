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
package org.openflexo.foundation.sg.implmodel;

import java.util.Vector;

import org.openflexo.foundation.sg.implmodel.enums.TechnologyLayer;
import org.openflexo.xmlcode.XMLSerializable;


/**
 * @author nid
 *
 */
public class TechnologyModuleDefinitionDTO implements XMLSerializable {

	public TechnologyLayer technologyLayer;
	public String name;
	public String version;
	public String description;
	public Vector<CompatibilityModule> compatibleModuleList;
	public Vector<CompatibilityModule> incompatibleModuleList;
	public Vector<CompatibilityModule> requiredModuleList;

	public static class CompatibilityModule implements XMLSerializable {
		public String name;
	}
}
