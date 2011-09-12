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


public class FIBTabView<C extends FIBTab> extends FIBPanelView<C> {

	private static final Logger logger = Logger.getLogger(FIBTabView.class.getPackage().getName());

	private boolean wasSelected = false;
	
	public FIBTabView(C model, FIBController controller)
	{
		super(model,controller);
	}

	@Override
	protected void performSetIsVisible (boolean isVisible)
	{
		super.performSetIsVisible(isVisible);
		if (getParentView() instanceof FIBTabPanelView) {
			FIBTabPanelView parent = (FIBTabPanelView)getParentView();
			if (isVisible) {
				parent.getJComponent().add(getResultingJComponent(),getLocalized(getComponent().getTitle()),getComponent().getIndex());
				if (wasSelected) {
					parent.getJComponent().setSelectedComponent(getResultingJComponent());
				}
			}
			else {
				wasSelected = (parent.getJComponent().getSelectedComponent() == getResultingJComponent());
				parent.getJComponent().remove(getResultingJComponent());
			}
		}
	}
	

}
