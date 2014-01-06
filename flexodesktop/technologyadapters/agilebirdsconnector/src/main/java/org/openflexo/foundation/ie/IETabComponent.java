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
package org.openflexo.foundation.ie;

import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Represents a 'popup' WOComponent related to a ComponentDefinition.Popup attached to an Operation Node
 * 
 * @author sguerin
 * 
 */
public final class IETabComponent extends IEPartialComponent implements DataFlexoObserver {
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IETabComponent.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization for IEThumbnailComponent
	 * 
	 * @param componentDefinition
	 */
	public IETabComponent(FlexoComponentBuilder builder) {
		super(builder);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for IEThumbnailComponent
	 * 
	 * @param componentDefinition
	 */
	public IETabComponent(TabComponentDefinition aComponentDefinition, FlexoProject prj) {
		super(aComponentDefinition, prj);
	}

	@Override
	public TabComponentDefinition getComponentDefinition() {
		return (TabComponentDefinition) super.getComponentDefinition();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.IE.TAB_COMPONENT_INSPECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Tab:" + getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "tab_component";
	}

}
