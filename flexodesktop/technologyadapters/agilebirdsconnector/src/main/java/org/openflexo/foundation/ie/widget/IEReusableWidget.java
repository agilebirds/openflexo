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
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.ComponentInstanceOwner;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.PartialComponentInstance;
import org.openflexo.foundation.ie.cl.PartialComponentDefinition;
import org.openflexo.foundation.ie.dm.ReusableWidgetRemoved;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

public abstract class IEReusableWidget<C extends PartialComponentDefinition, CI extends PartialComponentInstance> extends IEWidget
		implements ComponentInstanceOwner, InspectableObject {
	private static final Logger logger = FlexoLogger.getLogger(IEReusableWidget.class.getPackage().getName());

	protected CI reusableComponentInstance;

	/**
	 * Constructor used by deserialization
	 * 
	 * @param builder
	 */
	public IEReusableWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
	}

	private IEReusableWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	/**
	 * Constructor used at runtime
	 * 
	 * @param woComponent
	 * @param componentDefinition
	 * @param parent
	 * @param prj
	 */
	public IEReusableWidget(IEWOComponent woComponent, C componentDefinition, IEObject parent, FlexoProject prj) {
		this(woComponent, parent, prj);
		if (componentDefinition != null) {
			reusableComponentInstance = createComponentInstance(componentDefinition, woComponent);
			reusableComponentInstance.setReusableWidget(this);
		}
	}

	protected abstract CI createComponentInstance(C componentDefinition, IEWOComponent woComponent);

	@Override
	public void performAfterDeleteOperations() {
		super.performAfterDeleteOperations();
		getReusableComponentInstance().delete();
		setChanged();
		notifyObservers(new ReusableWidgetRemoved(this));
	}

	public CI getReusableComponentInstance() {
		return reusableComponentInstance;
	}

	public void setReusableComponentInstance(CI widgetComponentInstance) {
		if (reusableComponentInstance != null) {
			reusableComponentInstance.delete();
		}
		reusableComponentInstance = widgetComponentInstance;
		if (widgetComponentInstance != null) {
			widgetComponentInstance.setReusableWidget(this);
		}
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (reusableComponentInstance != null) {
			reusableComponentInstance.updateDependancies(getWOComponent(), woComponent);
		}
		super.setWOComponent(woComponent);
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(reusableComponentInstance);
		return answer;
	}

	public IEWidget getRootObject() {
		if (getReusableComponentInstance() != null) {
			return getReusableComponentInstance().getWOComponent().getRootSequence();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Reusable component with no definition");
			}
			return null;
		}
	}

	@Override
	public String getInspectorName() {
		if (_inspectorName == null) {
			return getDefaultInspectorName();
		}
		return _inspectorName;
	}

	@Override
	public boolean areComponentInstancesValid() {
		return this.reusableComponentInstance != null && this.reusableComponentInstance.getComponentDefinition() != null;
	}

	@Override
	public String getDefaultInspectorName() {
		return Inspectors.IE.REUSABLE_WIDGET_INSPECTOR;
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (this.reusableComponentInstance == null || this.reusableComponentInstance.getComponentDefinition() == null) {
			removeFromContainer();
		}
	}

	@Override
	public String getClassNameKey() {
		return "reusable_widget";
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}
}
