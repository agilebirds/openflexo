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

import java.util.List;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionListener;

/**
 * This interface is implemented by all the ModuleView requiring to be synchronized with a SelectionManager. Will act as a SelectionListener
 * of a SelectionManager
 * 
 * @author sguerin
 */
public interface SelectionSynchronizedModuleView<O extends FlexoModelObject> extends ModuleView<O>, SelectionListener {

	public List<SelectionListener> getSelectionListeners();
}
