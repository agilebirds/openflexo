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
package org.openflexo.wkf.swleditor.gr;

import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.dm.LabelLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneEditorController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public abstract class SWLObjectGR<O extends FlexoModelObject> extends ShapeGraphicalRepresentationImpl implements GraphicalFlexoObserver,
		SWLEditorConstants {

	public SWLObjectGR(O object, ShapeType shapeType, SwimmingLaneRepresentation aDrawing) {
		super(shapeType, object, aDrawing);
		object.addObserver(this);
		addToMouseClickControls(new SwimmingLaneEditorController.ShowContextualMenuControl(false));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new SwimmingLaneEditorController.ShowContextualMenuControl(true));
		}
		updatePropertiesFromWKFPreferences();
	}

	public O getModel() {
		return getDrawable();
	}

	public FlexoWorkflow getWorkflow() {
		if (getModel().getProject() != null) {
			return getModel().getProject().getFlexoWorkflow();
		}
		return null;
	}

	@Override
	public void delete() {
		O model = getModel();
		super.delete();
		model.deleteObserver(this);
	}

	@Override
	public SwimmingLaneRepresentation getDrawing() {
		return (SwimmingLaneRepresentation) super.getDrawing();
	}

	public void updatePropertiesFromWKFPreferences() {
		/*if (supportShadow()
				&& (getWorkflow() != null && getWorkflow().getShowShadows(WKFPreferences.getShowShadows()) || getWorkflow() == null
						&& WKFPreferences.getShowShadows())) {
			setShadowStyle(ShadowStyle.makeDefault());
		} else {*/
		setShadowStyle(ShadowStyle.makeNone());
		// }
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getModel()) {
			if (dataModification instanceof ObjectSizeChanged) {
				notifyObjectResized();
			} else if (dataModification instanceof ObjectLocationChanged) {
				notifyObjectMoved();
			} else if (dataModification instanceof LabelLocationChanged) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.absoluteTextX);
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.absoluteTextY);
			}
		}
	}

	protected abstract boolean supportShadow();
}
