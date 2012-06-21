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
package org.openflexo.foundation.ie.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.ie.widget.AbstractInnerTableWidget;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.ITableRowReusableWidget;
import org.openflexo.foundation.ie.widget.InnerBlocReusableWidget;
import org.openflexo.foundation.ie.widget.InnerBlocWidgetInterface;
import org.openflexo.foundation.ie.widget.InnerTableReusableWidget;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;

public class MakePartialComponent extends FlexoAction<MakePartialComponent, IEWidget, IEWidget> {

	private static final Logger logger = Logger.getLogger(MakePartialComponent.class.getPackage().getName());
	private IEReusableWidget reusableWidget;
	public static FlexoActionType<MakePartialComponent, IEWidget, IEWidget> actionType = new FlexoActionType<MakePartialComponent, IEWidget, IEWidget>(
			"make_partial_component", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public MakePartialComponent makeNewAction(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
			return new MakePartialComponent(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return !(object instanceof IETabWidget);
		}

		@Override
		public boolean isEnabledForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return !(object instanceof IEReusableWidget);
		}

	};

	private String _newComponentName;
	private FlexoComponentFolder _newComponentFolder;
	private FlexoComponentResource _newComponentResource;

	MakePartialComponent(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoComponentFolder getNewComponentFolder() {
		if (_newComponentFolder == null) {
			_newComponentFolder = getFocusedObject().getProject().getFlexoComponentLibrary().getRootFolder()
					.getFolderTyped(FolderType.PARTIAL_COMPONENT_FOLDER);
		}
		return _newComponentFolder;
	}

	public void setNewComponentFolder(FlexoComponentFolder newComponentFolder) {
		_newComponentFolder = newComponentFolder;
	}

	public String getNewComponentName() {
		return _newComponentName;
	}

	public void setNewComponentName(String newComponentName) {
		_newComponentName = newComponentName;
	}

	public FlexoComponentResource getNewComponentResource() {
		return _newComponentResource;
	}

	@Override
	protected void doAction(Object context) {
		if (getFocusedObject() != null) {
			IEWidget widget = getFocusedObject();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Make partial component from " + widget);
			}
			try {
				_newComponentResource = makeItPartial(widget, getNewComponentName(), getNewComponentFolder());
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No widget defined !");
			}
		}
	}

	public IEReusableComponent getComponent() {
		return comp;
	}

	private IEReusableComponent comp;

	private FlexoComponentResource makeItPartial(IEWidget widget, String newPartialComponentName, FlexoComponentFolder folder)
			throws DuplicateResourceException {
		IEWOComponent woComponent = widget.getWOComponent();
		IEObject parent = widget.getParent();
		Vector<IEWidget> widgets = new Vector<IEWidget>();
		widgets.add(widget);
		int minIndex = widget.getIndex();
		int maxIndex = widget.getIndex();
		if (getGlobalSelection() != null) {
			for (IEWidget w : getGlobalSelection()) {
				if (w.getParent() == parent) {
					widgets.add(w);
					minIndex = Math.min(minIndex, w.getIndex());
					maxIndex = Math.max(maxIndex, w.getIndex());
				}
			}
			if (maxIndex - minIndex + 1 != widgets.size()) {
				widgets.clear();
				widgets.add(widget);
			}
		}
		Collections.sort(widgets, new Comparator<IEWidget>() {
			@Override
			public int compare(IEWidget o1, IEWidget o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
		// first create the component definition in the library
		ReusableComponentDefinition compDef = new ReusableComponentDefinition(newPartialComponentName, folder.getComponentLibrary(),
				folder, widget.getProject());
		FlexoComponentResource compRes = compDef.getComponentResource();// Creates the resource
		comp = new IEReusableComponent(compDef, widget.getProject());
		for (IEWidget w : widgets) {
			w.setWOComponent(comp);
			comp.getRootSequence().addToInnerWidgets(w);
		}
		compRes.setResourceData(comp);
		try {
			compRes.saveResourceData();
		} catch (SaveXMLResourceException e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("ERROR while saving partial component");
			}
			e1.printStackTrace();
		} catch (SaveResourcePermissionDeniedException e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("ERROR while saving partial component. Please check permissions");
			}
			e1.printStackTrace();
		} catch (SaveResourceException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("ERROR while saving partial component");
			}
			e.printStackTrace();
		}

		// 5. create a new ReusableWidget to store the ComponentInstance
		if ((widget instanceof InnerBlocWidgetInterface) && (parent instanceof IEBlocWidget)) {
			reusableWidget = new InnerBlocReusableWidget(woComponent, compDef, (IEBlocWidget) parent, widget.getProject());
		} else if ((widget instanceof AbstractInnerTableWidget) && (parent instanceof IEWidget)) {
			reusableWidget = new InnerTableReusableWidget(woComponent, compDef, (IEWidget) parent, widget.getProject());
		} else if (widget.isTopComponent() && (parent instanceof IESequenceWidget)) {
			reusableWidget = new TopComponentReusableWidget(woComponent, compDef, (IESequenceWidget) parent, widget.getProject());
		} else if ((widget instanceof IESequenceTR) && (parent instanceof IESequenceTR)) {
			reusableWidget = new ITableRowReusableWidget(woComponent, compDef, parent, widget.getProject());
		}
		// 6. Insert the reusableWidget in its parent
		if (reusableWidget != null) {
			if ((reusableWidget instanceof InnerBlocReusableWidget) && (parent instanceof IEBlocWidget)) {
				((IEBlocWidget) parent).replaceWidgetByReusable(widget, (InnerBlocReusableWidget) reusableWidget);
			} else if ((reusableWidget instanceof InnerTableReusableWidget) || (reusableWidget instanceof TopComponentReusableWidget)) {
				if (parent instanceof IESequenceWidget) {
					for (IEWidget w : widgets) {
						((IESequenceWidget) parent).removeFromInnerWidgets(w, false);
						((IESequenceWidget) parent).insertElementAt(reusableWidget, minIndex);
					}
				}
			} else if (reusableWidget instanceof ITableRowReusableWidget) {
				((IESequenceTR) parent).replaceWidgetByReusable((ITableRow) widget, (ITableRowReusableWidget) reusableWidget);
				((IESequenceTR) parent).htmlTable().setTRRowIndex();
			}
		}
		return compRes;
	}

	public IEReusableWidget getReusableWidget() {
		return reusableWidget;
	}
}
