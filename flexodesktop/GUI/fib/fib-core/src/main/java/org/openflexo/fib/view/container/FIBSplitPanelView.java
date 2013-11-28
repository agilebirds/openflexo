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

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBSplitPanel;
import org.openflexo.fib.model.SplitLayoutConstraints;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.KnobDividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;

public class FIBSplitPanelView<T> extends FIBContainerView<FIBSplitPanel, JXMultiSplitPane, T> {

	private static final Logger logger = Logger.getLogger(FIBSplitPanelView.class.getPackage().getName());

	private JXMultiSplitPane splitPane;
	private MultiSplitLayout layout;

	public FIBSplitPanelView(FIBSplitPanel model, FIBController controller) {
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
	protected JXMultiSplitPane createJComponent() {
		layout = new MultiSplitLayout();
		layout.setLayoutByWeight(false);
		layout.setFloatingDividers(true);

		splitPane = new JXMultiSplitPane(layout)/* {

												@Override
												public Component add(String title, Component component) {
												logger.info("Add " + component);
												return super.add(title, component);
												}

												@Override
												public Component add(Component component) {
												logger.info("Add " + component);
												return super.add(component);
												}

												@Override
												public void add(Component component, Object constraints) {
												logger.info("Add " + component);
												super.add(component, constraints);
												}

												@Override
												public void add(Component component, Object constraints, int index) {
												logger.info("Add " + component);
												super.add(component, constraints, index);
												}

												}*/;

		layout.setModel(getComponent().getSplit());

		splitPane.setDividerPainter(new KnobDividerPainter());
		// splitPane.setPreferredSize(new Dimension(200, 200));

		updateLayout();

		// layout.setLayoutByWeight(false);
		// layout.setFloatingDividers(false);

		return splitPane;
	}

	public MultiSplitLayout getLayout() {
		return layout;
	}

	@Override
	public JXMultiSplitPane getJComponent() {
		return splitPane;
	}

	@Override
	protected void retrieveContainedJComponentsAndConstraints() {

		for (FIBComponent subComponent : getComponent().getSubComponents()) {
			FIBView subView = getController().viewForComponent(subComponent);
			if (subView == null) {
				subView = getController().buildView(subComponent);
			}
			// FIBView subView = getController().buildView(subComponent);
			// if (subView != null) {
			registerViewForComponent(subView, subComponent);
			registerComponentWithConstraints(subView.getResultingJComponent(),
					((SplitLayoutConstraints) subComponent.getConstraints()).getSplitIdentifier());
			// }
		}
	}

	@Override
	public synchronized void updateLayout() {

		logger.info("relayout split panel " + getComponent());

		layout.setModel(getComponent().getSplit());

		/*if (getSubViews() != null) {
			for (FIBView v : getSubViews().values()) {
				if (v.getComponent().isDeleted()) {
					v.delete();
				}
			}
		}*/
		getJComponent().removeAll();

		buildSubComponents();
		updateDataObject(getDataObject());

		getJComponent().revalidate();
		getJComponent().repaint();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				layout.setFloatingDividers(false);
			}
		});
	}
	/*@Override
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
	}*/

	/*	public void setSelectedIndex(int index) {
			getJComponent().setSelectedIndex(index);
		}*/
}
