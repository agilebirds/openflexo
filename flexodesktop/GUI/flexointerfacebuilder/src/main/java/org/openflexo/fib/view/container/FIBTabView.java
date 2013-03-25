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

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.view.FIBView;

public class FIBTabView<C extends FIBTab> extends FIBPanelView<C> {

	private static final Logger logger = Logger.getLogger(FIBTabView.class.getPackage().getName());

	private boolean wasSelected = false;

	public FIBTabView(C model, FIBController controller) {
		super(model, controller);
	}

	/*
	 * @Override public void updateDataObject(Object dataObject) {
	 * System.out.println("Je suis le FIBTabView " + getComponent().getName());
	 * System.out.println("J'etais visible " + isVisible() +
	 * " et je deviens visible " + isComponentVisible());
	 * super.updateDataObject(dataObject); }
	 */

	@Override
	protected void updateVisibility() {

		// logger.info("Called performSetIsVisible " + isVisible + " on TabComponent " + getComponent().getTitle());

		super.updateVisibility();
		// We need to perform this additional operation here, because JTabbedPane already plays with the "visible" flag to handle the
		// currently selected/visible tab
		if (getParentView() instanceof FIBTabPanelView) {
			FIBTabPanelView parent = (FIBTabPanelView) getParentView();
			if (isVisible() && getResultingJComponent().getParent() == null) {
				int newIndex = 0;
				for (FIBView<?, ?> v : getParentView().getSubViews().values()) {
					if (v instanceof FIBTabView && v.isComponentVisible()) {
						FIBTab tab = ((FIBTabView<?>) v).getComponent();
						if (getComponent().getParent().getIndex(getComponent()) > tab.getParent().getIndex(tab)) {
							newIndex = parent.getJComponent().indexOfComponent(v.getResultingJComponent()) + 1;
						}
					}
				}

				logger.fine("********** Adding component " + getComponent().getTitle() + " at index " + newIndex);

				parent.getJComponent().add(getResultingJComponent(), getLocalized(getComponent().getTitle()), newIndex);
				if (wasSelected) {
					parent.getJComponent().setSelectedComponent(getResultingJComponent());
				}
			} else if (!isVisible() && getResultingJComponent().getParent() != null) {
				wasSelected = parent.getJComponent().getSelectedComponent() == getResultingJComponent();
				parent.getJComponent().remove(getResultingJComponent());
				logger.fine("********** Removing component " + getComponent().getTitle());
			}
		}

	}

}
