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
package org.openflexo.fib.view.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;

public class FIBTabPanelView extends FIBContainerView<FIBTabPanel, JTabbedPane> {

	private static final Logger logger = Logger.getLogger(FIBTabPanelView.class.getPackage().getName());

	private JTabbedPane tabbedPane;

	public FIBTabPanelView(FIBTabPanel model, FIBController controller) {
		super(model, controller);
	}

	@Override
	public void delete() {
		super.delete();
	}

	/*
	 * @Override public void updateDataObject(Object dataObject) { update();
	 * System.out.println("Je suis le FIBTabPanelView " +
	 * getComponent().getName()); if (isComponentVisible()) { for (FIBView v :
	 * subViews) { System.out.println("Je m'occupe de mon fils: " +
	 * v.getComponent().getName()); v.updateDataObject(dataObject); } if
	 * (getDynamicModel() != null) { logger.fine("Container: " + getComponent()
	 * + " value data for " + getDynamicModel() + " is " + getValue());
	 * getDynamicModel().setData(getValue()); notifyDynamicModelChanged(); } } }
	 */

	@Override
	protected JTabbedPane createJComponent() {
		tabbedPane = new JTabbedPane()/*
										* {
										* 
										* @Override public Component add(String
										* title, Component component) {
										* logger.info("Add "+component); return
										* super.add(title, component); }
										* 
										* @Override public Component add(Component
										* component) {
										* logger.info("Add "+component); return
										* super.add(component); }
										* 
										* @Override public void add(Component
										* component, Object constraints) {
										* logger.info("Add "+component);
										* super.add(component, constraints); }
										* 
										* @Override public void add(Component
										* component, Object constraints, int index)
										* { logger.info("Add "+component);
										* super.add(component, constraints, index);
										* }
										* 
										* }
										*/;
		return tabbedPane;
	}

	@Override
	public JTabbedPane getJComponent() {
		return tabbedPane;
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {
		Vector<FIBTab> allTabs = new Vector<FIBTab>();
		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			if (subComponent instanceof FIBTab) {
				allTabs.add((FIBTab) subComponent);
			}
		}

		Collections.sort(allTabs, new Comparator<FIBTab>() {
			@Override
			public int compare(FIBTab o1, FIBTab o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});

		for (FIBTab tab : allTabs) {
			// logger.info("!!!!!!!!!!!!!!!!!!!! Build view for tab " + tab);
			FIBView subView = getController().buildView(tab);
			if (subView != null) {
				registerViewForComponent(subView, tab);
				registerComponentWithConstraints(subView.getResultingJComponent(), getLocalized(tab.getTitle()));
			}
		}

	}

	// TODO: optimize it
	@Override
	public synchronized void updateLayout() {

		int index = tabbedPane.getSelectedIndex();
		for (FIBView v : getSubViews()) {
			v.delete();
		}
		getResultingJComponent().removeAll();
		buildSubComponents();
		updateDataObject(getDataObject());
		index = Math.min(index, tabbedPane.getTabCount() - 1);
		if (index > -1) {
			tabbedPane.setSelectedIndex(index);
		}
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		int index = 0;
		for (FIBView v : subViews) {
			if (v.getComponent() instanceof FIBTab) {
				tabbedPane.setTitleAt(index, getLocalized(((FIBTab) v.getComponent()).getTitle()));
			} else {
				logger.warning("Unexpected component found in TabPanel: " + v.getComponent());
			}
			index++;
		}
	}

	public void setSelectedIndex(int index) {
		getJComponent().setSelectedIndex(index);
	}
}
