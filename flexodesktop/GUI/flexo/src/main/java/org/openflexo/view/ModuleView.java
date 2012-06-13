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
package org.openflexo.view;

import org.openflexo.foundation.FlexoModelObject;

/**
 * This interface is implemented by all views that will be displayed as a top-level view of a module. This abstract representation is used
 * by a general scheme implemented in module controller to manage the navigation and includes a control panel.
 * 
 * @author sguerin
 */
public interface ModuleView<O extends FlexoModelObject> {

	public O getRepresentedObject();

	public void deleteModuleView();

	/**
	 * This method should return the perspective in which this view is supposed to be seen. DO NOT return null!!!
	 * 
	 * @return
	 */
	public FlexoPerspective getPerspective();

	/**
	 * This method is called before the module view is about to be shown
	 * 
	 */
	public void willShow();

	/**
	 * This method is called before the module view is about to be hidden
	 * 
	 */
	public void willHide();

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	public boolean isAutoscrolled();

}
