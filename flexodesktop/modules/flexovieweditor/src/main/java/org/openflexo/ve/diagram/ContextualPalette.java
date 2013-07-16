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
package org.openflexo.ve.diagram;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.action.AddShape;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.localization.FlexoLocalization;

public class ContextualPalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(ContextualPalette.class.getPackage().getName());

	private DiagramPalette _calcPalette;

	public ContextualPalette(DiagramPalette viewPointPalette) {
		super((int) ((DrawingGraphicalRepresentation) viewPointPalette.getGraphicalRepresentation()).getWidth(),
				(int) ((DrawingGraphicalRepresentation) viewPointPalette.getGraphicalRepresentation()).getHeight(), viewPointPalette
						.getName());

		_calcPalette = viewPointPalette;

		for (DiagramPaletteElement element : viewPointPalette.getElements()) {
			addElement(makePaletteElement(element));
		}

		makePalettePanel();
		getPaletteView().revalidate();
	}

	@Override
	public DiagramController getController() {
		return (DiagramController) super.getController();
	}

	private Vector<DropScheme> getAvailableDropSchemes(EditionPattern pattern, GraphicalRepresentation target) {
		Vector<DropScheme> returned = new Vector<DropScheme>();
		for (DropScheme dropScheme : pattern.getDropSchemes()) {
			if (dropScheme.isTopTarget() && target instanceof DrawingGraphicalRepresentation) {
				returned.add(dropScheme);
			}
			if (target.getDrawable() instanceof DiagramShape) {
				DiagramShape targetShape = (DiagramShape) target.getDrawable();
				for (FlexoModelObjectReference<EditionPatternInstance> ref : targetShape.getEditionPatternReferences()) {
					if (dropScheme.isValidTarget(ref.getObject().getEditionPattern(), ref.getObject().getRoleForActor(targetShape))) {
						returned.add(dropScheme);
					}
				}
			}

		}
		return returned;
	}

	private boolean hasValidTarget(EditionPattern pattern, GraphicalRepresentation target) {
		for (EditionScheme es : pattern.getEditionSchemes()) {
			if (es instanceof DropScheme) {
				DropScheme dropScheme = (DropScheme) es;
				if (dropScheme.isTopTarget() && target instanceof DrawingGraphicalRepresentation) {
					return true;
				}
				if (target.getDrawable() instanceof DiagramShape) {
					DiagramShape targetShape = (DiagramShape) target.getDrawable();
					for (FlexoModelObjectReference<EditionPatternInstance> ref : targetShape.getEditionPatternReferences()) {
						if (dropScheme.isValidTarget(ref.getObject().getEditionPattern(), ref.getObject().getRoleForActor(targetShape))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private PaletteElement makePaletteElement(final DiagramPaletteElement element) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(element.getGraphicalRepresentation(),
				null, getPaletteDrawing()) {
			@Override
			public String getText() {
				if (element != null && element.getBoundLabelToElementName()) {
					return element.getName();
				}
				return "";
			}
		};

		gr.setText(element.getName());

		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(GraphicalRepresentation target) {
				EditionPattern pattern = element.getEditionPattern();
				if (pattern != null) {
					return hasValidTarget(pattern, target);
				}
				return false;
			}

			@Override
			public boolean elementDragged(GraphicalRepresentation containerGR, final FGEPoint dropLocation) {
				logger.info("Dragging " + getGraphicalRepresentation() + " with text " + getGraphicalRepresentation().getText());

				if (containerGR.getDrawable() instanceof DiagramElement<?>) {

					final DiagramElement<?> container = (DiagramElement<?>) containerGR.getDrawable();

					// final ShapeGraphicalRepresentation shapeGR = getGraphicalRepresentation().clone();
					final ShapeGraphicalRepresentation shapeGR = new DiagramShapeGR(null, null);
					// boolean wasAllowedToLeaveBounds = getGraphicalRepresentation().getAllowToLeaveBounds();
					// getGraphicalRepresentation().setAllowToLeaveBounds(true);

					shapeGR.setsWith(getGraphicalRepresentation());
					// if (!wasAllowedToLeaveBounds) getGraphicalRepresentation().setAllowToLeaveBounds(false);
					shapeGR.setIsSelectable(true);
					shapeGR.setIsFocusable(true);
					shapeGR.setIsReadOnly(false);
					shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
					// if (applyForegroundStyle) shapeGR.setForeground(getController().getToolbox().currentForegroundStyle);
					// if (applyBackgroundStyle) shapeGR.setBackground(getController().getToolbox().currentBackgroundStyle);
					// if (applyShadowStyle) shapeGR.setShadowStyle(getController().getToolbox().currentShadowStyle);
					// if (applyTextStyle) shapeGR.setTextStyle(getController().getToolbox().currentTextStyle);
					shapeGR.setLocation(dropLocation);
					shapeGR.setLayer(containerGR.getLayer() + 1);
					// shapeGR.setAllowToLeaveBounds(true);

					if (element.getEditionPattern() == null) {
						// No associated edition pattern, just drop shape !

						AddShape action = AddShape.actionType.makeNewAction(container, null, getController().getVEController().getEditor());
						action.setGraphicalRepresentation(shapeGR);
						action.setNameSetToNull(false);
						action.setNewShapeName(getGraphicalRepresentation().getText());

						action.doAction();
						return action.hasActionExecutionSucceeded();
					}

					else {

						Vector<DropScheme> availableDropPatterns = getAvailableDropSchemes(element.getEditionPattern(), containerGR);

						if (availableDropPatterns.size() > 1) {
							JPopupMenu popup = new JPopupMenu();
							for (final DropScheme dropScheme : availableDropPatterns) {
								JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(dropScheme.getVirtualModel()
										.getLocalizedDictionary(),
										dropScheme.getLabel() != null ? dropScheme.getLabel() : dropScheme.getName()));
								menuItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										DropSchemeAction action = DropSchemeAction.actionType.makeNewAction(container, null,
												getController().getVEController().getEditor());
										action.dropLocation = dropLocation;
										action.setDropScheme(dropScheme);
										action.setPaletteElement(element);
										action.doAction();
									}
								});
								popup.add(menuItem);
							}
							DrawingView dw = getController().getDrawingView();
							FGEView containerView = dw.viewForObject(containerGR);

							popup.show((Component) containerView, (int) dropLocation.x, (int) dropLocation.y);
							return true;
						} else if (availableDropPatterns.size() == 1) {
							DropSchemeAction action = DropSchemeAction.actionType.makeNewAction(container, null, getController()
									.getVEController().getEditor());
							action.dropLocation = dropLocation;
							action.setDropScheme(availableDropPatterns.firstElement());
							action.setPaletteElement(element);
							action.doAction();
							return action.hasActionExecutionSucceeded();
						}

					}

				}
				return false;
			}

			@Override
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public DrawingPalette getPalette() {
				return ContextualPalette.this;
			}

		};
		gr.setDrawable(returned);
		return returned;
	}
}
