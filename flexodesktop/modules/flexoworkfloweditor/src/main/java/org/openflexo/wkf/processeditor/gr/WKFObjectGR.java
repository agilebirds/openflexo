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
package org.openflexo.wkf.processeditor.gr;

import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public abstract class WKFObjectGR<O extends WKFObject> extends ShapeGraphicalRepresentation<O> implements GraphicalFlexoObserver,
		ProcessEditorConstants {

	private static final Logger logger = Logger.getLogger(WKFObjectGR.class.getPackage().getName());

	public WKFObjectGR(O object, ShapeType shapeType, ProcessRepresentation aDrawing) {
		super(shapeType, object, aDrawing);
		object.addObserver(this);
		addToMouseClickControls(new ProcessEditorController.ShowContextualMenuControl(false));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new ProcessEditorController.ShowContextualMenuControl(true));
		}

		updatePropertiesFromWKFPreferences();
	}

	public boolean supportAlignOnGrid() {
		return true;
	}

	@Override
	public FGEArea getLocationConstrainedArea() {
		if (getDrawing() != null)
			return getDrawing().getDrawingGraphicalRepresentation().getLocationConstraintsForObject(this);
		return null;
	}

	public boolean supportResizeToGrid() {
		return false;
	}

	public O getModel() {
		return getDrawable();
	}

	@Override
	public void delete() {
		O model = getModel();
		super.delete();
		model.deleteObserver(this);
	}

	@Override
	public boolean getIsVisible() {
		if (getDrawing() != null)
			return getDrawing().isVisible(getModel());
		else
			return true;
	}

	@Override
	public ProcessRepresentation getDrawing() {
		return (ProcessRepresentation) super.getDrawing();
	}

	public FlexoWorkflow getWorkflow() {
		if (getDrawable().getProcess() != null)
			return getDrawable().getProcess().getWorkflow();
		return null;
	}

	public void checkAndUpdateLocationAndDimension() {
		checkAndUpdateDimensionIfRequired();
		checkAndUpdateLocationIfRequired();
	}

	public void updatePropertiesFromWKFPreferences() {
		if (supportShadow()
				&& ((getWorkflow() != null && getWorkflow().getShowShadows(WKFPreferences.getShowShadows())) || (getWorkflow() == null && WKFPreferences
						.getShowShadows()))) {
			setShadowStyle(ShadowStyle.makeDefault());
		} else {
			setShadowStyle(ShadowStyle.makeNone());
		}
	}

	protected abstract boolean supportShadow();

}
