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
package org.openflexo.view.controller.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public abstract class FlexoPerspective extends ControllerModelObject {

	static final Logger logger = Logger.getLogger(FlexoPerspective.class.getPackage().getName());

	private String name;

	public static final String HEADER = "header";
	public static final String FOOTER = "footer";

	public static final String TOP_LEFT_VIEW = "topLeftView";
	public static final String TOP_RIGHT_VIEW = "topRightView";
	public static final String TOP_CENTER_VIEW = "topCenterView";

	public static final String MIDDLE_LEFT_VIEW = "middleLeftView";
	public static final String MIDDLE_RIGHT_VIEW = "middleRightView";

	public static final String BOTTOM_LEFT_VIEW = "bottomLeftView";
	public static final String BOTTOM_RIGHT_VIEW = "bottomRightView";
	public static final String BOTTOM_CENTER_VIEW = "bottomCenterView";

	public static final String[] PROPERTIES = { HEADER, FOOTER, TOP_LEFT_VIEW, TOP_RIGHT_VIEW, TOP_CENTER_VIEW, MIDDLE_LEFT_VIEW,
			MIDDLE_RIGHT_VIEW, BOTTOM_LEFT_VIEW, BOTTOM_RIGHT_VIEW, BOTTOM_CENTER_VIEW };

	private JComponent topLeftView;
	private JComponent topRightView;
	private JComponent middleLeftView;
	private JComponent middleRightView;
	private JComponent bottomLeftView;
	private JComponent bottomRightView;
	private JComponent topCenterView;
	private JComponent bottomCenterView;

	private JComponent header;
	private JComponent footer;

	public FlexoPerspective(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setupDefaultLayout(Node layout) {
	}

	public abstract ImageIcon getActiveIcon();

	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller, boolean editable) {
		if (!editable) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Perspective " + getName()
						+ " does not override createModuleViewForObject(O object, FlexoController controller, boolean editable)");
			}
		}
		return createModuleViewForObject(object, controller);
	}

	@Deprecated
	public abstract ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller);

	public abstract boolean hasModuleViewForObject(FlexoObject object);

	public abstract FlexoObject getDefaultObject(FlexoObject proposedObject);

	public JComponent getHeader() {
		return header;
	}

	public void setHeader(JComponent header) {
		JComponent old = this.header;
		this.header = header;
		getPropertyChangeSupport().firePropertyChange(HEADER, old, header);
	}

	public JComponent getFooter() {
		return footer;
	}

	public void setFooter(JComponent footer) {
		JComponent old = this.footer;
		this.footer = footer;
		getPropertyChangeSupport().firePropertyChange(FOOTER, old, footer);
	}

	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {

	}

	public JComponent getTopLeftView() {
		return topLeftView;
	}

	public void setTopLeftView(JComponent topLetfView) {
		JComponent old = this.topLeftView;
		this.topLeftView = topLetfView;
		getPropertyChangeSupport().firePropertyChange(TOP_LEFT_VIEW, old, topLetfView);
	}

	public JComponent getTopRightView() {
		return topRightView;
	}

	public void setTopRightView(JComponent topRightView) {
		JComponent old = this.topRightView;
		this.topRightView = topRightView;
		getPropertyChangeSupport().firePropertyChange(TOP_RIGHT_VIEW, old, topRightView);
	}

	public JComponent getBottomLeftView() {
		return bottomLeftView;
	}

	public void setBottomLeftView(JComponent bottomLetfView) {
		JComponent old = this.bottomLeftView;
		this.bottomLeftView = bottomLetfView;
		getPropertyChangeSupport().firePropertyChange(BOTTOM_LEFT_VIEW, old, bottomLetfView);
	}

	public JComponent getBottomRightView() {
		return bottomRightView;
	}

	public void setBottomRightView(JComponent bottomRightView) {
		JComponent old = this.bottomRightView;
		this.bottomRightView = bottomRightView;
		getPropertyChangeSupport().firePropertyChange(BOTTOM_RIGHT_VIEW, old, bottomRightView);
	}

	public JComponent getTopCenterView() {
		return topCenterView;
	}

	public void setTopCenterView(JComponent topCentralView) {
		JComponent old = this.topCenterView;
		this.topCenterView = topCentralView;
		getPropertyChangeSupport().firePropertyChange(TOP_CENTER_VIEW, old, topCentralView);
	}

	public JComponent getBottomCenterView() {
		return bottomCenterView;
	}

	public void setBottomCenterView(JComponent bottomCentralView) {
		JComponent old = this.bottomCenterView;
		this.bottomCenterView = bottomCentralView;
		getPropertyChangeSupport().firePropertyChange(BOTTOM_CENTER_VIEW, old, bottomCentralView);
	}

	public JComponent getMiddleLeftView() {
		return middleLeftView;
	}

	public void setMiddleLeftView(JComponent middleLeftView) {
		JComponent old = this.middleLeftView;
		this.middleLeftView = middleLeftView;
		getPropertyChangeSupport().firePropertyChange(MIDDLE_LEFT_VIEW, old, middleLeftView);
	}

	public JComponent getMiddleRightView() {
		return middleRightView;
	}

	public void setMiddleRightView(JComponent middleRightView) {
		JComponent old = this.middleRightView;
		this.middleRightView = middleRightView;
		getPropertyChangeSupport().firePropertyChange(MIDDLE_RIGHT_VIEW, old, middleRightView);
	}

	public void objectWasClicked(Object object) {
		// logger.info("FlexoPerspective: object was clicked: " + object);
	}

	public void objectWasRightClicked(Object object) {
		// logger.info("FlexoPerspective: object was right-clicked: " + object);
	}

	public void objectWasDoubleClicked(Object object) {
		// logger.info("FlexoPerspective: object was double-clicked: " + object);
	}

}
