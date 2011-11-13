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
package org.openflexo.foundation.ie.operator;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.ListType;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

public abstract class IEOperator extends IEWidget implements InspectableObject {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IEOperator.class.getPackage().getName());

	private IESequence operatedSequence;

	public IEOperator(IEWOComponent wo, IESequence parent, FlexoProject project) {
		super(wo, parent, project);
		operatedSequence = parent;
	}

	@Override
	public void performOnDeleteOperations() {
		operatedSequence.resetOperator();
		operatedSequence.unwrap();
		super.performOnDeleteOperations();
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return new Vector<IObject>();
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public void setIndex(int i) {

	}

	public IESequence getOperatedSequence() {
		return operatedSequence;
	}

	public boolean isLastOperator() {
		return true;
	}

	public void setOperatedSequence(IESequence opSeq) {
		this.operatedSequence = opSeq;
		setParent(opSeq);
		setChanged();
		notifyObservers(new IEDataModification("operatedSequence", null, opSeq));
	}

	@Override
	public String getWidgetType() {
		String cls = getClass().getSimpleName();
		if (cls.startsWith("IE"))
			cls = cls.substring("IE".length());
		if (cls.endsWith("Operator"))
			cls = cls.substring(0, cls.length() - "Operator".length());
		return cls;
	}

	/**
     * 
     */
	public void notifyWidgetRemoval(IWidget w) {
		setChanged();
		notifyObservers(new IEDataModification("widgetRemoval", w, null));
	}

	public void notifyWidgetInsertion(IWidget w) {
		setChanged();
		notifyObservers(new IEDataModification("widgetInsertion", null, w));
	}

	@Override
	public boolean areComponentInstancesValid() {
		return true; // Always
	}

	@Override
	public void removeInvalidComponentInstances() {
		// Nothing to do here
	}

	public abstract ListType getListType();

	public abstract void setListType(ListType lt);
}