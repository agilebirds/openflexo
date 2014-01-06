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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.GraphType;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class IEBIRTWidget extends AbstractInnerTableWidget {

	/**
     * 
     */
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(IEBIRTWidget.class.getPackage().getName());

	private boolean usePercentage = true;
	private GraphType graphType;
	private int widthPixel = 512;
	private int heightPixel = 350;
	private int percentage = 100;

	public IEBIRTWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEBIRTWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public boolean areComponentInstancesValid() {
		return true;
	}

	@Override
	public String getDefaultInspectorName() {
		return Inspectors.IE.BIRT_INSPECTOR;
	}

	@Override
	public void removeInvalidComponentInstances() {

	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> v = new Vector<IObject>();
		v.add(this);
		return v;
	}

	@Override
	public String getClassNameKey() {
		return "birt_widget";
	}

	@Override
	public String getFullyQualifiedName() {
		return "BIRT_WIDGET." + getName();
	}

	public GraphType getGraphType() {
		if (graphType == null) {
			graphType = GraphType.Pie;
		}
		return graphType;
	}

	public String getImageName() {
		return getGraphType().getImageName();
	}

	public void setGraphType(GraphType graphType) {
		GraphType old = this.graphType;
		this.graphType = graphType;
		setChanged();
		notifyModification("graphType", old, graphType);
	}

	public int getWidthPixel() {
		return widthPixel;
	}

	public void setWidthPixel(int widthPixel) {
		this.widthPixel = widthPixel;
		setChanged();
		notifyObservers(new IEDataModification("widthPixel", null, widthPixel));
	}

	public int getHeightPixel() {
		return heightPixel;
	}

	public void setHeightPixel(int heightPixel) {
		this.heightPixel = heightPixel;
		setChanged();
		notifyObservers(new IEDataModification("heightPixel", null, heightPixel));
	}

	public boolean getUsePercentage() {
		return usePercentage;
	}

	public void setUsePercentage(boolean usePercentage) {
		this.usePercentage = usePercentage;
		setChanged();
		notifyObservers(new IEDataModification("usePercentage", !usePercentage, usePercentage));
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
		setChanged();
		notifyObservers(new IEDataModification("percentage", null, percentage));
	}

	public boolean isOriginalSize() {
		if (getGraphType() != null) {
			return getGraphType().getIcon().getIconWidth() == getWidthPixel()
					&& getGraphType().getIcon().getIconHeight() == getHeightPixel();
		}
		return false;
	}

}
