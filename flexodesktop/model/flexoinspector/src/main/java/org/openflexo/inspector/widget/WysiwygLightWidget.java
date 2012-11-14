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

import java.awt.Dimension;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.wysiwyg.FlexoWysiwyg;
import org.openflexo.wysiwyg.FlexoWysiwygLight;
import org.openflexo.wysiwyg.FlexoWysiwygUltraLight;

public class WysiwygLightWidget extends DenaliWidget<String> {

	private FlexoWysiwyg wysiwyg;

	public WysiwygLightWidget(PropertyModel model, AbstractController controller, boolean isUltraLightWysiwyg) {
		super(model, controller);
		boolean showViewSourceButtonInWysiwyg = controller.getConfiguration() != null
				&& controller.getConfiguration().showViewSourceButtonInWysiwyg();
		if (isUltraLightWysiwyg) {
			this.wysiwyg = new FlexoWysiwygUltraLight(showViewSourceButtonInWysiwyg) {
				@Override
				public void notifyTextChanged() {
					updateModelFromWidget();
				}
			};
		} else {
			// TODO -> load the CSS (need project)
			this.wysiwyg = new FlexoWysiwygLight(showViewSourceButtonInWysiwyg) {
				@Override
				public void notifyTextChanged() {
					updateModelFromWidget();
				}
			};
		}
		wysiwyg.addBorderAroundToolbar();
		wysiwyg.setPreferredSize(new Dimension(250, 150));
		wysiwyg.getSelectedEditorComponent().addFocusListener(new WidgetFocusListener(this));
	}

	@Override
	protected synchronized void setModel(InspectableObject value) {
		boolean forceRefresh = getModel() == null;
		boolean wasReadOnly = isReadOnly();
		super.setModel(value);
		boolean isReadOnly = isReadOnly();
		if (forceRefresh || wasReadOnly != isReadOnly) {
			wysiwyg.setPreviewModeOnly(isReadOnly);
			wysiwyg.revalidate();
		}
	}

	@Override
	public Class getDefaultType() {
		return String.class;
	}

	@Override
	public boolean widgetHasFocus() {
		return super.widgetHasFocus() || wysiwyg.hasFocus();
	}

	@Override
	public synchronized void updateWidgetFromModel() {

		widgetUpdating = true;
		wysiwyg.setActivated(false);
		try {
			wysiwyg.setContent(getStringValue());
		} finally {
			wysiwyg.setActivated(true);
			widgetUpdating = false;
		}
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (isReadOnly()) {
			return;
		}
		modelUpdating = true;
		try {
			setStringValue(wysiwyg.getBodyContent());
		} finally {
			modelUpdating = false;
		}
	}

	@Override
	public FlexoWysiwyg getDynamicComponent() {
		return wysiwyg;
	}
}
