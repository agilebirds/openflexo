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
package org.openflexo.ie.view.palette;

import java.awt.Color;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.palette.FlexoIEPalette;
import org.openflexo.foundation.ie.palette.FlexoIEPalette.FlexoIEPaletteWidget;
import org.openflexo.foundation.ie.widget.IEAbstractWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.selection.SelectableView;
import org.openflexo.swing.ImageUtils;
import org.openflexo.view.palette.PaletteElement;

/**
 * Represents a widget in the palette. This widget has no parent nor woComponent
 * 
 * @author bmangez
 */
public class IEDSWidget extends IEAbstractWidget implements PaletteElement {

	public static final String TARGET_CLASS_MODEL = "target.class.model";

	private static final Logger logger = Logger.getLogger(IEDSWidget.class.getPackage().getName());

	private FlexoIEPalette<? extends FlexoIEPaletteWidget>.FlexoIEPaletteWidget paletteWidget;
	private boolean resizeScreenshot = true;
	private transient JLabel _label;

	public IEDSWidget(FlexoComponentBuilder builder) {
		super(null);
	}

	public IEDSWidget(FlexoIEPalette.FlexoIEPaletteWidget widget, boolean resizeScreenshot, FlexoCSS css) {
		super(null);
		this.paletteWidget = widget;
		this.resizeScreenshot = resizeScreenshot;
		setName(widget.getName());
		_label = new JLabel();
		_label.setBackground(Color.WHITE);
		_label.setHorizontalAlignment(SwingConstants.CENTER);
		_label.setName(widget.getName());
	}

	@Override
	public void delete() {
		if (paletteWidget.canDeleteWidget()) {
			paletteWidget.deleteWidget();
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Cannot delete these kind of widgets");
		}
	}

	// ==========================================================================
	// ============================= Instance Methods
	// ===========================
	// ==========================================================================

	@Override
	public IEObject getParent() {
		return null;
	}

	public void refresh(FlexoCSS css) {
		if (paletteWidget.getScreenshotFile(css) != null && paletteWidget.getScreenshotFile(css).exists()) {
			_label.setPreferredSize(null);
			if (resizeScreenshot) {
				_label.setIcon(ImageUtils.getThumbnail(new ImageIcon(paletteWidget.getScreenshotFile(css).getAbsolutePath()), 85));
			} else {
				_label.setIcon(new ImageIcon(paletteWidget.getScreenshotFile(css).getAbsolutePath()));
			}
		} else {
			if (_label.getIcon() == null) {
				_label.setText(paletteWidget.getName());
			}
		}
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's null
	 * since there is no parent component
	 * 
	 * @return null
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return null;
	}

	@Override
	public FlexoProject getProject() {
		return _project;
	}

	private FlexoProject _project;
	private ComponentDefinition component;

	public void setProject(FlexoProject prj) {
		_project = prj;
	}

	public JLabel getLabel() {
		return _label;
	}

	public IEWidget getPaletteWidget() {
		try {
			FlexoComponentBuilder builder = (FlexoComponentBuilder) component.getWOComponent().instanciateNewBuilder();
			return paletteWidget.getWidget(builder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public IEWidget getWidget(FlexoProject project) {
		setComponent(null);
		setProject(project);
		return getPaletteWidget();
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(getPaletteWidget());
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "IE_DS_WIDGET." + getName();
	}

	public static class DSWidgetComparator implements Comparator {
		@Override
		public int compare(Object arg0, Object arg1) {
			if (arg0 instanceof IEDSWidget && arg1 instanceof IEDSWidget) {
				return ((IEDSWidget) arg0).getName().compareToIgnoreCase(((IEDSWidget) arg1).getName());
			}
			return 0;
		}

	}

	@Override
	public boolean isEdited() {
		return false;
	}

	@Override
	public void edit() {
		logger.warning("Not implemented yet");
	}

	@Override
	public void closeEdition() {
		logger.warning("Not implemented yet");
	}

	@Override
	public void save() {
		logger.warning("Not implemented yet");
	}

	@Override
	public FlexoModelObject getObject() {
		return this;
	}

	@Override
	public SelectableView getView() {
		logger.warning("Implement me !");
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "ds_widget";
	}

	public IEWidget getWidget(ComponentDefinition aComponent) {
		setComponent(aComponent);
		setProject(aComponent.getProject());
		return getPaletteWidget();
	}

	public ComponentDefinition getComponent() {
		return component;
	}

	public void setComponent(ComponentDefinition aComponent) {
		this.component = aComponent;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean isValid(ValidationModel validationModel) {
		return true;
	}

	@Override
	public ValidationReport validate() {
		return null;
	}

	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return null;
	}

	@Override
	public void validate(ValidationReport report) {

	}

	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {

	}

	public boolean isTopComponent() {
		return paletteWidget.isTopComponent();
	}

	public Class<?> getTargetClassModel() {
		return paletteWidget.getTargetClass();
	}

}
