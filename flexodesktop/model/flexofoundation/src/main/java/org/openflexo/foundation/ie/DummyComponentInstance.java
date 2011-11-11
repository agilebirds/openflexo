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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.logging.FlexoLogger;

public class DummyComponentInstance extends ComponentInstance {

	private static final Logger logger = FlexoLogger.getLogger(DummyComponentInstance.class.getPackage().getName());

	public DummyComponentInstance(ComponentDefinition componentDefinition) {
		super(componentDefinition, null);
	}

	@Override
	public String getClassNameKey() {
		return "dummy_component_instance";
	}

	@Override
	public String getFullyQualifiedName() {
		return "DUMMY_COMPONENT_INSTANCE_" + getComponentDefinition().getFullyQualifiedName();
	}

	@Override
	public FlexoProject getProject() {
		if (getComponentDefinition() != null)
			return getComponentDefinition().getProject();
		if (logger.isLoggable(Level.WARNING))
			logger.warning("No component definition on dummy component instance");
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return null;
	}

	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return null;
	}

	@Override
	public IEObject getParent() {
		return null;
	}

	public IEHTMLTableWidget getHTMLTable() {
		return null;
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted && observable == getComponentDefinition()) {
			this.delete();
		}
	}

	@Override
	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getComponentDefinition().getAllEmbeddedIEObjects();
	}
}
