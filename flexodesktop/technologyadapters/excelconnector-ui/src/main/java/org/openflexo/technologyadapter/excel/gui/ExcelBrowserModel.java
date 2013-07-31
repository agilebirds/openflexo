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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.technologyadapter.excel.rm.ExcelWorkbookResource;

/**
 * Model supporting browsing through Excel resources<br>
 * 
 * @author vincent
 */
public class ExcelBrowserModel extends Observable implements FlexoObserver {

	static final Logger logger = Logger.getLogger(ExcelBrowserModel.class.getPackage().getName());

	private ExcelWorkbookResource context;
	private boolean hierarchicalMode = true;
	private boolean strictMode = false;
	private FlexoObject rootClass;
	private BuiltInDataType dataType = null;

	private List<FlexoObject> roots = null;
	private Map<FlexoObject, List<FlexoObject>> structure = null;

	public ExcelBrowserModel(ExcelWorkbookResource context) {
		super();
		setContext(context);
	}

	public List<FlexoObject> getRoots() {
		if (roots == null) {
			recomputeStructure();
		}
		return roots;
	}

	public List<FlexoObject> getChildren(FlexoObject father) {
		return structure.get(father);
	}

	private boolean isRecomputingStructure = false;

	public void recomputeStructure() {

		logger.fine("BEGIN recomputeStructure for " + getContext());

		isRecomputingStructure = true;
		isRecomputingStructure = false;
		setChanged();
		notifyObservers(new OntologyBrowserModelRecomputed());

		logger.fine("END recomputeStructure for " + getContext());
	}

	public static class OntologyBrowserModelRecomputed {

	}

	public void delete() {
		context = null;
	}

	public ExcelWorkbookResource getContext() {
		return context;
	}

	public void setContext(ExcelWorkbookResource context) {
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

	public FlexoObject getRootClass() {
		return rootClass;
	}

	public void setRootClass(FlexoObject rootClass) {
		this.rootClass = rootClass;
	}

	public boolean isDisplayable(FlexoObject object) {

		if (object instanceof FlexoObject) {
			return true;
		} else {
			return false;
		}
	}

	private void addChildren(FlexoObject parent, FlexoObject child) {
		List<FlexoObject> v = structure.get(parent);
		if (v == null) {
			v = new Vector<FlexoObject>();
			structure.put(parent, v);
		}
		if (!v.contains(child)) {
			v.add(child);
		}
	}

	public Font getFont(IFlexoOntologyConcept object, Font baseFont) {
		if (object.getOntology() != getContext()) {
			return baseFont.deriveFont(Font.ITALIC);
		}
		return baseFont;
	}

}
