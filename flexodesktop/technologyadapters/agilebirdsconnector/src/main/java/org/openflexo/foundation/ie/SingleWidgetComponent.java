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

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * @author bmangez
 * @deprecated <B>Class Description</B>
 */
@Deprecated
public final class SingleWidgetComponent extends IEPartialComponent implements DataFlexoObserver {

	private static final Logger logger = Logger.getLogger(IETabComponent.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private IEWidget _rootWidget;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor invoked during deserialization for IEThumbnailComponent
	 * 
	 * @param componentDefinition
	 */
	public SingleWidgetComponent(FlexoComponentBuilder builder) {
		super(builder);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor for IEThumbnailComponent
	 * 
	 * @param componentDefinition
	 * @deprecated
	 */
	@Deprecated
	public SingleWidgetComponent(ComponentDefinition componentDefinition, FlexoProject prj) {
		super(componentDefinition, prj);
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	public IEWidget getRootWidget() {
		if (!isDeserializing() && _rootWidget == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Reusable component " + getComponentDefinition().getComponentName()
						+ " has no root widget defined-->this component will be deleted");
			}
			getFlexoResource().delete();
		}
		return _rootWidget;
	}

	public void setRootWidget(IEWidget widget) {
		_rootWidget = widget;
		_rootWidget.setWOComponent(this);
		_rootWidget.setParent(this);
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector answer = super.getEmbeddedIEObjects();
		Object o = getRootWidget();
		if (o != null) {
			answer.add(o);
		}
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "ReusableComponent:" + getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "single_widget_component";
	}

	public HTMLListDescriptorCollection getAllHTMLTableList() {
		HTMLListDescriptorCollection v = new HTMLListDescriptorCollection();

		if (getRootWidget() instanceof IEBlocWidget) {
			IEBlocWidget w = (IEBlocWidget) getRootWidget();
			HTMLListDescriptor d = HTMLListDescriptor.createInstanceForBloc(w);
			if (d != null) {
				v.add(d);
			}
		}

		return v;
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		if (getRootWidget() instanceof IETopComponent) {
			return ((IETopComponent) getRootWidget()).getAllTabContainers();
		}
		return new Vector<IESequenceTab>();
	}
}
