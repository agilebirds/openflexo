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
package org.openflexo.wkf.processeditor;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.wkf.processeditor.AbstractWKFPalette.WKFPaletteElement;


public class AnnotationMouseClickControl extends MouseClickControl {

	private static final class CreateAnnotationAction extends MouseClickControlAction {
		@Override
		public MouseClickControlActionType getActionType() {
			return MouseClickControlActionType.CUSTOM;
		}

		@Override
		public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
				MouseEvent event) {
			WKFPaletteElement annotation = ((ProcessEditorController)controller).getArtefactPalette().getAnnotation();
			if (annotation.acceptDragging(graphicalRepresentation)) {
				annotation.elementDragged(graphicalRepresentation, new FGEPoint(event.getPoint().getX()/controller.getScale()-annotation.getGraphicalRepresentation().getBorder().left,event.getPoint().getY()/controller.getScale()-annotation.getGraphicalRepresentation().getBorder().top));
			}
			return false;
		}
	}

	public AnnotationMouseClickControl() {
		super("create_annotation", MouseButton.LEFT, 2, new CreateAnnotationAction(),false,false,false,false);
	}
}
