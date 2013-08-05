/*
 * (c) Copyright 2013 OpenFlexo
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
package org.openflexo.technologyadapter.excel.gui;

import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.technologyadapter.TechnologyObject;

/**
 * Model supporting browsing through resources<br>
 * 
 * @author vincent
 */
public class ModelBrowser extends Observable implements FlexoObserver {

	static final Logger logger = Logger.getLogger(ModelBrowser.class.getPackage().getName());

	private IFlexoTechnology context;
	private TechnologyObject rootClass;

	private List<TechnologyObject> roots = null;
	private Map<TechnologyObject, List<TechnologyObject>> structure = null;

	public ModelBrowser(IFlexoTechnology context) {
		super();
		setContext(context);
	}

	public List<TechnologyObject> getRoots() {
		if (roots == null) {
			recomputeStructure();
		}
		return roots;
	}

	public List<TechnologyObject> getChildren(TechnologyObject father) {
		return structure.get(father);
	}

	private boolean isRecomputingStructure = false;

	public void recomputeStructure() {

		logger.fine("BEGIN recomputeStructure for " + getContext());

		isRecomputingStructure = true;
		isRecomputingStructure = false;
		setChanged();
		notifyObservers(new BrowserModelRecomputed());

		logger.fine("END recomputeStructure for " + getContext());
	}

	public static class BrowserModelRecomputed {

	}

	public void delete() {
		context = null;
	}

	public IFlexoTechnology getContext() {
		return context;
	}

	public void setContext(IFlexoTechnology context) {
		if (this.context != null) {
			((FlexoObservable) context).deleteObserver(this);
		}
		this.context = context;
		if (this.context != null) {
			((FlexoObservable) context).addObserver(this);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (isRecomputingStructure) {
			return;
		}
		recomputeStructure();
	}

	public TechnologyObject getRootClass() {
		return rootClass;
	}

	public void setRootClass(TechnologyObject rootClass) {
		this.rootClass = rootClass;
	}

	public boolean isDisplayable(TechnologyObject object) {

		if (object instanceof TechnologyObject) {
			return true;
		} else {
			return false;
		}
	}

	private void addChildren(TechnologyObject parent, TechnologyObject child) {
		List<TechnologyObject> v = structure.get(parent);
		if (v == null) {
			v = new Vector<TechnologyObject>();
			structure.put(parent, v);
		}
		if (!v.contains(child)) {
			v.add(child);
		}
	}

	public Font getFont(TechnologyObject object, Font baseFont) {
		return baseFont;
	}

}
