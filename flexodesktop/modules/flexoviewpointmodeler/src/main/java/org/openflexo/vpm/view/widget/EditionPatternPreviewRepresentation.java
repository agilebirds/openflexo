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
package org.openflexo.vpm.view.widget;

import java.awt.Color;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class EditionPatternPreviewRepresentation extends DrawingImpl<EditionPattern> implements EditionPatternPreviewConstants {

	private static final Logger logger = Logger.getLogger(EditionPatternPreviewRepresentation.class.getPackage().getName());

	// private EditionPatternPreviewShemaGR graphicalRepresentation;

	// private Boolean ignoreNotifications = true;

	// private Hashtable<PatternRole, EditionPatternPreviewShapeGR> shapesGR;
	// private Hashtable<PatternRole, EditionPatternPreviewConnectorGR> connectorsGR;

	static FGEModelFactory PREVIEW_FACTORY;

	static {
		try {
			PREVIEW_FACTORY = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	private final Hashtable<PatternRole, ConnectorFromArtifact> fromArtifacts;
	private final Hashtable<PatternRole, ConnectorToArtifact> toArtifacts;

	public EditionPatternPreviewRepresentation(EditionPattern model) {
		super(model, PREVIEW_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
		setEditable(false);

		fromArtifacts = new Hashtable<PatternRole, ConnectorFromArtifact>();
		toArtifacts = new Hashtable<PatternRole, ConnectorToArtifact>();
	}

	@Override
	public void init() {

		final DrawingGRBinding<EditionPattern> drawingBinding = bindDrawing(EditionPattern.class, "editionPattern",
				new DrawingGRProvider<EditionPattern>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(EditionPattern drawable, FGEModelFactory factory) {
						DrawingGraphicalRepresentation returned = factory.makeDrawingGraphicalRepresentation();
						returned.setWidth(WIDTH);
						returned.setHeight(HEIGHT);
						returned.setBackgroundColor(BACKGROUND_COLOR);
						returned.setDrawWorkingArea(false);
						return returned;
					}
				});
		final ShapeGRBinding<ShapePatternRole> shapeBinding = bindShape(ShapePatternRole.class, "shapePatternRole",
				new ShapeGRProvider<ShapePatternRole>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ShapePatternRole drawable, FGEModelFactory factory) {
						if (drawable.getGraphicalRepresentation() == null) {
							drawable.setGraphicalRepresentation(makeDefaultShapeGR());
						}
						return drawable.getGraphicalRepresentation();
					}
				});
		final ConnectorGRBinding<ConnectorPatternRole> connectorBinding = bindConnector(ConnectorPatternRole.class, "connector",
				shapeBinding, shapeBinding, new ConnectorGRProvider<ConnectorPatternRole>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(ConnectorPatternRole drawable, FGEModelFactory factory) {
						if (drawable.getGraphicalRepresentation() == null) {
							drawable.setGraphicalRepresentation(makeDefaultConnectorGR());
						}
						return drawable.getGraphicalRepresentation();
					}
				});
		final ShapeGRBinding<ConnectorFromArtifact> fromArtefactBinding = bindShape(ConnectorFromArtifact.class, "fromArtifact",
				new ShapeGRProvider<ConnectorFromArtifact>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ConnectorFromArtifact drawable, FGEModelFactory factory) {
						return makeFromArtefactGR();
					}

				});
		final ShapeGRBinding<ConnectorToArtifact> toArtefactBinding = bindShape(ConnectorToArtifact.class, "toArtifact",
				new ShapeGRProvider<ConnectorToArtifact>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(ConnectorToArtifact drawable, FGEModelFactory factory) {
						return makeToArtefactGR();
					}

				});

		drawingBinding.addToWalkers(new GRStructureVisitor<EditionPattern>() {

			@Override
			public void visit(EditionPattern editionPattern) {

				for (PatternRole<?> role : editionPattern.getPatternRoles()) {
					if (role instanceof ShapePatternRole) {
						if (((ShapePatternRole) role).getParentShapeAsDefinedInAction()) {
							drawShape(shapeBinding, (ShapePatternRole) role, getEditionPattern());
							// System.out.println("Add shape " + role.getPatternRoleName() + " under EditionPattern");
						} else {
							drawShape(shapeBinding, (ShapePatternRole) role, ((ShapePatternRole) role).getParentShapePatternRole());
							// System.out.println("Add shape " + role.getPatternRoleName() + " under "
							// + ((ShapePatternRole) role).getParentShapePatternRole().getPatternRoleName());
						}
					} else if (role instanceof ConnectorPatternRole) {
						ConnectorPatternRole connectorPatternRole = (ConnectorPatternRole) role;
						ShapeGRBinding fromBinding;
						ShapeGRBinding toBinding;
						Object fromDrawable;
						Object toDrawable;
						if (connectorPatternRole.getStartShapePatternRole() == null) {
							drawShape(fromArtefactBinding, getFromArtifact(connectorPatternRole), getEditionPattern());
							fromBinding = fromArtefactBinding;
							fromDrawable = getFromArtifact(connectorPatternRole);
							// System.out.println("Add From artifact under EditionPattern");
						} else {
							fromBinding = shapeBinding;
							fromDrawable = connectorPatternRole.getStartShapePatternRole();
						}
						if (connectorPatternRole.getEndShapePatternRole() == null) {
							drawShape(toArtefactBinding, getToArtifact(connectorPatternRole), getEditionPattern());
							// System.out.println("Add To artifact under EditionPattern");
							toBinding = toArtefactBinding;
							toDrawable = getToArtifact(connectorPatternRole);
						} else {
							toBinding = shapeBinding;
							toDrawable = connectorPatternRole.getEndShapePatternRole();
						}
						// System.out.println("Add connector " + role.getPatternRoleName() + " under EditionPattern");
						drawConnector(connectorBinding, connectorPatternRole, fromBinding, fromDrawable, toBinding, toDrawable);
					}
				}

			}
		});

		shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.exampleLabel"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.exampleLabel"), true);

	}

	@Override
	public void delete() {

		/*if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}*/
		/*if (getEditionPattern() != null) {
			getEditionPattern().deleteObserver(this);
		}*/
		/*for (PatternRole role : getEditionPattern().getPatternRoles()) {
			role.deleteObserver(this);
		}*/
		super.delete();
	}

	public EditionPattern getEditionPattern() {
		return getModel();
	}

	protected ConnectorFromArtifact getFromArtifact(ConnectorPatternRole connector) {
		ConnectorFromArtifact returned = fromArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorFromArtifact(connector);
			fromArtifacts.put(connector, returned);
		}
		return returned;
	}

	protected ConnectorToArtifact getToArtifact(ConnectorPatternRole connector) {
		ConnectorToArtifact returned = toArtifacts.get(connector);
		if (returned == null) {
			returned = new ConnectorToArtifact(connector);
			toArtifacts.put(connector, returned);
		}
		return returned;
	}

	protected class ConnectorFromArtifact {

		private final ConnectorPatternRole connector;

		protected ConnectorFromArtifact(ConnectorPatternRole aConnector) {
			connector = aConnector;
		}

	}

	protected class ConnectorToArtifact {

		private final ConnectorPatternRole connector;

		protected ConnectorToArtifact(ConnectorPatternRole aConnector) {
			connector = aConnector;
		}

	}

	private ShapeGraphicalRepresentation makeFromArtefactGR() {
		ShapeGraphicalRepresentation returned = PREVIEW_FACTORY.makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		returned.setX(80);
		returned.setY(80);
		returned.setWidth(20);
		returned.setHeight(20);
		returned.setForeground(PREVIEW_FACTORY.makeForegroundStyle(new Color(255, 204, 0)));
		returned.setBackground(PREVIEW_FACTORY.makeColoredBackground(new Color(255, 255, 204)));
		returned.setIsFocusable(true);
		returned.setIsSelectable(false);
		return returned;
	}

	private ShapeGraphicalRepresentation makeToArtefactGR() {
		ShapeGraphicalRepresentation returned = PREVIEW_FACTORY.makeShapeGraphicalRepresentation(ShapeType.CIRCLE);
		returned.setX(350);
		returned.setY(80);
		returned.setWidth(20);
		returned.setHeight(20);
		returned.setForeground(PREVIEW_FACTORY.makeForegroundStyle(new Color(255, 204, 0)));
		returned.setBackground(PREVIEW_FACTORY.makeColoredBackground(new Color(255, 255, 204)));
		returned.setIsFocusable(true);
		returned.setIsSelectable(false);
		return returned;
	}

	private ShapeGraphicalRepresentation makeDefaultShapeGR() {
		ShapeGraphicalRepresentation returned = PREVIEW_FACTORY.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setTextStyle(PREVIEW_FACTORY.makeTextStyle(DEFAULT_SHAPE_TEXT_COLOR, DEFAULT_FONT));
		returned.setX((WIDTH - DEFAULT_SHAPE_WIDTH) / 2);
		returned.setY((HEIGHT - DEFAULT_SHAPE_HEIGHT) / 2);
		returned.setWidth(DEFAULT_SHAPE_WIDTH);
		returned.setHeight(DEFAULT_SHAPE_HEIGHT);
		returned.setBackground(PREVIEW_FACTORY.makeColoredBackground(DEFAULT_SHAPE_BACKGROUND_COLOR));
		returned.setIsFloatingLabel(false);
		return returned;
	}

	private ConnectorGraphicalRepresentation makeDefaultConnectorGR() {
		ConnectorGraphicalRepresentation returned = PREVIEW_FACTORY.makeConnectorGraphicalRepresentation(ConnectorType.LINE);
		returned.setTextStyle(PREVIEW_FACTORY.makeTextStyle(DEFAULT_SHAPE_TEXT_COLOR, DEFAULT_FONT));
		return returned;
	}

}
