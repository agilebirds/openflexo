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

import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.ReusableComponentInstance;
import org.openflexo.foundation.ie.action.TopComponentDown;
import org.openflexo.foundation.ie.action.TopComponentUp;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class TopComponentReusableWidget extends IEReusableWidget<ReusableComponentDefinition, ReusableComponentInstance> implements
		IETopComponent {

	private static final Logger logger = FlexoLogger.getLogger(TopComponentReusableWidget.class.getPackage().getName());

	public TopComponentReusableWidget(FlexoComponentBuilder builder) {
		super(builder.woComponent, null, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public TopComponentReusableWidget(IEWOComponent woComponent, ReusableComponentDefinition def, IEWidget parent, FlexoProject prj) {
		super(woComponent, def, parent, prj);
	}

	@Override
	protected ReusableComponentInstance createComponentInstance(ReusableComponentDefinition componentDefinition, IEWOComponent woComponent) {
		return new ReusableComponentInstance(componentDefinition, woComponent);
	}

	@Override
	public boolean isTopComponent() {
		return true;
	}

	@Override
	public String getDefaultInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector getSpecificActionListForThatClass() {
		Vector v = super.getSpecificActionListForThatClass();
		v.add(TopComponentUp.actionType);
		v.add(TopComponentDown.actionType);
		return v;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "top_component_reusable";
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		if (getRootObject() == null)
			return reply;
		if (getRootObject() instanceof IETopComponent)
			reply.addAll(((IETopComponent) getRootObject()).getAllTabContainers());
		return reply;
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Implement me!!! please");
		return true;
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Implement me!!! please");
	}

	/**
	 * Overrides getTitle
	 * 
	 * @see org.openflexo.foundation.ie.IETopComponent#getTitle()
	 */
	@Override
	public String getTitle() {
		if (getRootObject() instanceof IETopComponent)
			return ((IETopComponent) getRootObject()).getTitle();
		else
			return getRootObject().getLabel();
	}
}
