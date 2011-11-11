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
package org.openflexo.inspector.widget;

import javax.swing.ImageIcon;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DropDownButtonWidget extends DropDownWidget {

	public DropDownButtonWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		widgetUpdating = true;
		initJComboBox();
		ImageIcon sel = findLabelWithName((String) getObjectValue());
		if (sel != null)
			_jComboBox.setSelectedItem(sel);
		widgetUpdating = false;
	}

	private ImageIcon findLabelWithName(String name) {
		for (int i = 0; i < _jComboBox.getModel().getSize(); i++) {
			if (((ImageIcon) _jComboBox.getModel().getElementAt(i)).getDescription().equals(name)) {
				return (ImageIcon) _jComboBox.getModel().getElementAt(i);
			}

		}
		return null;
	}

}
