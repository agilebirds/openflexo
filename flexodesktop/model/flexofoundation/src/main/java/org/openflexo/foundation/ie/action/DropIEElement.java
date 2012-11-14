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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.ButtonedWidgetInterface;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEButtonContainerWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class DropIEElement extends FlexoAction<DropIEElement, IEObject, IEObject> {

	private static final Logger logger = Logger.getLogger(DropIEElement.class.getPackage().getName());

	public static FlexoActionType<DropIEElement, IEObject, IEObject> actionType = new FlexoActionType<DropIEElement, IEObject, IEObject>(
			"drag_ie_element", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DropIEElement makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new DropIEElement(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, IEBlocWidget.class);
		FlexoModelObject.addActionForClass(actionType, IEHTMLTableWidget.class);
		FlexoModelObject.addActionForClass(actionType, IESequenceWidget.class);
		FlexoModelObject.addActionForClass(actionType, IEWOComponent.class);
	}

	DropIEElement(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		parameters = new Hashtable<String, Integer>();
	}

	private static final String INDEX = "index";

	private static final String ROW = "row";

	private static final String COL = "col";

	private IEWidget droppedWidget;

	private boolean updateDates = false;

	private IEObject container;

	private Hashtable<String, Integer> parameters;

	public int getIndex() {
		if (parameters.get(INDEX) == null) {
			return 0;
		}
		return parameters.get(INDEX);
	}

	public void setIndex(int index) {
		parameters.put(INDEX, index);
	}

	public int getRow() {
		if (parameters.get(ROW) == null) {
			return -1;
		}
		return parameters.get(ROW);
	}

	public void setRow(int row) {
		parameters.put(ROW, row);
	}

	public int getCol() {
		if (parameters.get(COL) == null) {
			return -1;
		}
		return parameters.get(COL);
	}

	public void setCol(int col) {
		parameters.put(COL, col);
	}

	public IEObject getContainer() {
		if (container == null) {
			container = getFocusedObject();
			if (container instanceof IEWOComponent) {
				container = ((IEWOComponent) container).getRootSequence();
			}
			if (container instanceof IETDWidget) {
				container = ((IETDWidget) container).getSequenceWidget();
			}
		}
		return container;
	}

	public void setContainer(IEObject container) {
		this.container = container;
	}

	public FlexoProject getProject() {
		if (getContainer() != null) {
			return getContainer().getProject();
		}
		return null;
	}

	public IEWidget getDroppedWidget() {
		return droppedWidget;
	}

	public void setDroppedWidget(IEWidget droppedWidget) {
		this.droppedWidget = droppedWidget;
	}

	@Override
	protected void doAction(Object context) throws InvalidDropException {
		logger.info(new StringBuffer("Insert IE element index=").append(getIndex()).append(" row=").append(getRow()).append(" col=")
				.append(getCol()).toString());

		if (getContainer() == null) {
			throw new InvalidDropException("Cannot drop on a null container !");
		}

		if (getDroppedWidget() == null) {
			throw new InvalidDropException("Cannot drop a null widget !");
		}
		if (isUpdateDates()) {
			Vector<IEStringWidget> stringWidgets = droppedWidget.getAllEmbeddedIEWidgets(IEStringWidget.class);
			for (IEStringWidget w : stringWidgets) {
				if (w.getFieldType() != null && w.getFieldType() == TextFieldType.DATE) {
					w.setValue(w.getDefaultValue());
				}
			}
		}
		getProject().register(droppedWidget);
		droppedWidget.setWOComponent(getWOComponent());
		droppedWidget.setParent(getContainer());

		if (droppedWidget instanceof IEButtonWidget) {
			if (((IEButtonWidget) droppedWidget).getImageName() != null
					&& ((IEButtonWidget) droppedWidget).getImageName().toLowerCase().indexOf("help") > -1) {
				((IEButtonWidget) droppedWidget).setHyperlinkType(HyperlinkType.HELP);
			}
		}

		if (!insertWidget(getDroppedWidget(), getContainer())) {
			throw new InvalidDropException("Cannot drop such a widget on this container !");
		}

	}

	public IEWOComponent getWOComponent() {
		if (getContainer() instanceof IEWOComponent) {
			return (IEWOComponent) getContainer();
		} else if (getContainer() instanceof IEWidget) {
			return ((IEWidget) getContainer()).getWOComponent();
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot resolve component!");
			}
		}
		return null;
	}

	private boolean insertWidget(IEWidget model, IEObject container) throws InvalidDropException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Container=" + container);
			logger.info("IEWidget=" + model);
		}

		if (container instanceof IESequenceTopComponent && model instanceof IETopComponent) {
			if (getIndex() == -1) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
			model.setIndex(getIndex());
			try {
				((IESequenceTopComponent) container).insertElementAt((IETopComponent) model, getIndex());
				if (logger.isLoggable(Level.INFO)) {
					logger.info("CASE 1: insert IETopComponent in IESequenceTopComponent (DropZoneTopComponent)");
				}
				return true;
			} catch (IndexOutOfBoundsException e) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
		}

		if (container instanceof IEBlocWidget && model instanceof IEHTMLTableWidget) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("CASE 3: insert IEHTMLTableWidget in IEBlocWidget (DropTableZone)");
			}
			((IEBlocWidget) container).insertContent((IEHTMLTableWidget) model);
			return true;
		}

		if (container instanceof ButtonedWidgetInterface && model instanceof IEHyperlinkWidget) {
			if (getIndex() == -1) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("CASE 6: insert IEButtonWidget in ButtonedWidgetInterface (ButtonPanel)");
			}
			((ButtonedWidgetInterface) container).insertButtonAtIndex((IEHyperlinkWidget) model, getIndex());
			return true;
		}

		if (container instanceof IESequenceTab && model instanceof IETabWidget) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("CASE 7: insert IETabWidget in IESequenceTab (DropTabZone)");
			}
			((IESequenceTab) container).addToInnerWidgets((IETabWidget) model);
			return true;
		}

		if (container instanceof IEButtonContainerWidget && model instanceof IEButtonWidget) {
			if (getIndex() == -1) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
			if (logger.isLoggable(Level.INFO)) {
				logger.info("CASE 8: insert IEButtonWidget in IEButtonContainerWidget (IEButtonsContainerWIdgetView)");
			}
			((IEButtonContainerWidget) container).addButton((IEButtonWidget) model, getIndex());
			return true;
		}

		if (container instanceof IESequenceWidget) {
			if (getIndex() == -1) {
				throw new InvalidDropException("Cannot drop element at this index: " + getIndex());
			}
			if (model instanceof IETDWidget) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("CASE 9: insert IETDWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
				}
				IESequenceWidget seq = ((IETDWidget) model).getSequenceWidget();
				for (int j = 0; j < seq.size(); j++) {
					model = seq.get(j);
					model.setIndex(getIndex());
					((IESequenceWidget) container).insertElementAt(model, getIndex());
				}
			} else if (model instanceof IESequenceWidget && ((IESequenceWidget) model).getOperator() == null) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("CASE 10: insert IESequenceWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
				}
				IESequenceWidget seq = (IESequenceWidget) model;
				for (int j = 0; j < seq.size(); j++) {
					model = seq.get(j);
					model.setIndex(getIndex() + j);
					((IESequenceWidget) container).insertElementAt(model, getIndex() + j);
				}
			} else {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("CASE 11: insert IEWidget in IESequenceWidget (IESequenceWidgetWidgetView)");
				}
				model.setIndex(getIndex());
				((IESequenceWidget) container).insertElementAt(model, getIndex());
			}
			return true;
		}

		return false;

	}

	private IEWidget buildDroppedElement(String elementXMLRepresentation) {
		IEWidget widget = null;
		try {
			XMLMapping ieMapping = getProject().getXmlMappings().getIEMapping();
			FlexoComponentBuilder builder = new FlexoComponentBuilder(getWOComponent().getComponentDefinition(), getWOComponent()
					.getFlexoResource());
			widget = (IEWidget) XMLDecoder.decodeObjectWithMapping(elementXMLRepresentation, ieMapping, builder, getProject()
					.getStringEncoder());
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed building element: " + elementXMLRepresentation);
			}
			e.printStackTrace();
		}
		return widget;
	}

	private IEWidget buildDroppedElement(File elementXMLFile) {
		Properties answer = new Properties();
		try {
			answer.load(new FileInputStream(elementXMLFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buildDroppedElement(answer.getProperty("xml"));
	}

	public String getElementXMLRepresentation() {
		return droppedWidget.getXMLRepresentation();
	}

	public void setElementXMLRepresentation(String elementXMLRepresentation) {
		droppedWidget = buildDroppedElement(elementXMLRepresentation);
	}

	public void setElementType(WidgetType widgetType) {
		String paletteElementName = getElementName(widgetType);
		File foundFile = locatePaletteElementFile(paletteElementName);
		if (foundFile == null) {
			logger.warning("Could not locate IE palette element " + paletteElementName);
			droppedWidget = null;
		} else {
			droppedWidget = buildDroppedElement(foundFile);
		}
	}

	private static File getPaletteDirectory() {
		return new FileResource("Config/IEPalette");
	}

	private static File locatePaletteElementFile(String paletteElementName) {
		File[] paletteSubDirectories = getPaletteDirectory().listFiles(new FileFilter() {
			@Override
			public boolean accept(File aFile) {
				return aFile.isDirectory() && !aFile.getName().endsWith("CVS");
			}
		});
		for (File subDir : paletteSubDirectories) {
			for (File file : subDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File aFile) {
					return aFile.isFile() && aFile.getName().endsWith(".properties");
				}
			})) {
				if (file.getName().equalsIgnoreCase(paletteElementName)
						|| file.getName().equalsIgnoreCase(paletteElementName + ".properties")) {
					return file;
				}
			}
		}
		return null;
	}

	// ---------------------------------

	protected static String getElementName(WidgetType widgetType) {
		if (widgetType != null) {
			return widgetType.name();
		} else {
			return "";
		}
	}

	public static DropIEElement createBlocInComponent(IEWOComponent component, int index, FlexoEditor editor) {
		DropIEElement dropBloc = DropIEElement.actionType.makeNewAction(component, null, editor);
		dropBloc.setElementType(WidgetType.BLOCK);
		dropBloc.setIndex(index);
		return dropBloc;
	}

	public static DropIEElement createHTMLTableInComponent(IEWOComponent component, int index) {
		DropIEElement dropTable = DropIEElement.actionType.makeNewAction(component, null);
		dropTable.setElementType(WidgetType.HTMLTable);
		dropTable.setIndex(index);
		return dropTable;
	}

	public static DropIEElement createTableInBloc(IEBlocWidget bloc, FlexoEditor editor) {
		DropIEElement dropHTMLTable = DropIEElement.actionType.makeNewAction(bloc, null, editor);
		dropHTMLTable.setElementType(WidgetType.HTMLTable);
		return dropHTMLTable;
	}

	public static DropIEElement createTabsInComponent(IEWOComponent component, int index, FlexoEditor editor) {
		DropIEElement dropTabs = DropIEElement.actionType.makeNewAction(component, null, editor);
		dropTabs.setElementType(WidgetType.TABS);
		dropTabs.setIndex(index);
		return dropTabs;
	}

	public static DropIEElement insertWidgetInTable(IEHTMLTableWidget table, WidgetType widgetType, int row, int col, int index,
			FlexoEditor editor) {
		IETDWidget cell = table.getTDAt(row, col);
		IESequenceWidget sequence = cell.getSequenceWidget();
		DropIEElement dropWidget = DropIEElement.actionType.makeNewAction(sequence, null, editor);
		dropWidget.setElementType(widgetType);
		dropWidget.setIndex(index);
		return dropWidget;
	}

	public boolean isUpdateDates() {
		return updateDates;
	}

	public void setUpdateDates(boolean updateDates) {
		this.updateDates = updateDates;
	}

}
