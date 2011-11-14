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
package org.openflexo.ie.view;

import java.awt.Toolkit;

import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.ie.view.controller.IEController;

public class IEReusableWidgetComponentView extends IEWOComponentView {

	public IEReusableWidgetComponentView(IEController ctrl, ComponentInstance c) {
		super(ctrl, c);
	}

	@Override
	public int getMaxWidth() {
		if (getParent() != null) {
			return getParent().getWidth();
		}
		return 0;
	}

	@Override
	protected int getMaxHeight() {
		if (dropZone == null) {
			return Toolkit.getDefaultToolkit().getScreenSize().height - 100;
		}
		int h = dropZone.getPreferredSize().height + 50;
		if (h < 100) {
			h = 100;
		}
		return h;
	}

	@Override
	public void deleteModuleView() {
		logger.info("ReusableWidgetComponentView view deleted !");
		super.deleteModuleView();
	}

}
