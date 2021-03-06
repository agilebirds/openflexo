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
package org.openflexo.foundation.param;

import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.inspector.widget.DenaliWidget;

public class ViewParameter extends ParameterDefinition<ViewDefinition> {

	private ViewSelectingConditional _viewSelectingConditional;

	public ViewParameter(String name, String label, ViewDefinition defaultValue) {
		super(name, label, defaultValue);
		addParameter("className", "org.openflexo.components.widget.ViewInspectorWidget");
		_viewSelectingConditional = null;
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.CUSTOM;
	}

	public boolean isAcceptableView(ViewDefinition aView) {
		if (_viewSelectingConditional != null) {
			return _viewSelectingConditional.isSelectable(aView);
		}
		return true;
	}

	public void setViewSelectingConditional(ViewSelectingConditional viewSelectingConditional) {
		_viewSelectingConditional = viewSelectingConditional;
		addParameter("isSelectable", "params." + getName() + ".isAcceptableView");
	}

	public abstract static class ViewSelectingConditional {
		public abstract boolean isSelectable(ViewDefinition view);
	}

}
