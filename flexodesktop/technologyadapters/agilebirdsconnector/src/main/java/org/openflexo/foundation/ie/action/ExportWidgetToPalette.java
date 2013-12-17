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
package org.openflexo.foundation.ie.action;

import java.awt.image.BufferedImage;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IEWidget;

public class ExportWidgetToPalette extends FlexoAction<ExportWidgetToPalette, IEWidget, IEWidget> {

	private String _widgetName;

	public static FlexoActionType<ExportWidgetToPalette, IEWidget, IEWidget> actionType = new FlexoActionType<ExportWidgetToPalette, IEWidget, IEWidget>(
			"export_widget_to_palette", FlexoActionType.helpGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ExportWidgetToPalette makeNewAction(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
			return new ExportWidgetToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return !(object instanceof IEReusableWidget)
					&& !(object instanceof IESequenceTR)
					&& !(object instanceof IETRWidget)
					&& (globalSelection == null || globalSelection.size() == 0 || globalSelection.size() == 1
							&& globalSelection.firstElement() == object);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, IEWidget.class);
	}

	ExportWidgetToPalette(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private IEWidget widget = null;
	private BufferedImage screenshot;

	@Override
	protected void doAction(Object context) {
		getWidget().getProject().getCustomWidgetPalette().addNewWidgetToIEPaletteDirectory(getWidget(), getWidgetName(), getScreenshot());
	}

	public IEWidget getWidget() {
		if (widget == null) {
			if (getFocusedObject() instanceof IEOperator) {
				widget = ((IEOperator) getFocusedObject()).getOperatedSequence();
			} else if (getFocusedObject() instanceof IETDWidget) {
				widget = ((IETDWidget) getFocusedObject()).getSequenceWidget();
			} else {
				widget = getFocusedObject();
			}
		}
		return widget;
	}

	public void setWidgetName(String w) {
		_widgetName = w;
	}

	public String getWidgetName() {
		return _widgetName;
	}

	public BufferedImage getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(BufferedImage screenshot) {
		this.screenshot = screenshot;
	}
}
