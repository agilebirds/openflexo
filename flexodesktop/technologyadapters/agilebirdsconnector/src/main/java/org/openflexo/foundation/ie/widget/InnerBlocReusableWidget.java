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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.ReusableComponentInstance;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class InnerBlocReusableWidget extends IEReusableWidget<ReusableComponentDefinition, ReusableComponentInstance> implements
		InnerBlocWidgetInterface {

	private static final Logger logger = FlexoLogger.getLogger(InnerBlocReusableWidget.class.getPackage().getName());

	public InnerBlocReusableWidget(FlexoComponentBuilder builder) {
		super(builder);
		initializeDeserialization(builder);
	}

	public InnerBlocReusableWidget(IEWOComponent woComponent, ReusableComponentDefinition def, IEBlocWidget parent, FlexoProject prj) {
		super(woComponent, def, parent, prj);
	}

	@Override
	protected ReusableComponentInstance createComponentInstance(ReusableComponentDefinition componentDefinition, IEWOComponent woComponent) {
		return new ReusableComponentInstance(componentDefinition, woComponent);
	}

	@Override
	public String getDefaultInspectorName() {
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "inner_bloc_reusable";
	}

	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		return getReusableComponentInstance().getWOComponent().getAllDateTextfields();
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Implement me!!! please");
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Implement me!!! please");
		}
		return true;
	}
}
