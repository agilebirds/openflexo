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
package org.openflexo.rm.view;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.ResourceAdded;
import org.openflexo.foundation.rm.ResourceRemoved;

public class RMViewerRepresentation extends DrawingImpl<FlexoProject> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(RMViewerRepresentation.class.getPackage().getName());

	private DrawingGraphicalRepresentation drawingGraphicalRepresentation;
	private ShapeGraphicalRepresentation resourceGraphicalRepresentation;
	private ConnectorGraphicalRepresentation resourceDependancyGraphicalRepresentation;
	private ConnectorGraphicalRepresentation resourceSynchronizationGraphicalRepresentation;

	/*public RMViewerRepresentation(FlexoProject project) {
		super(project);
		graphicalRepresentation = new DrawingGraphicalRepresentation<FlexoProject>(this);

		project.addObserver(this);

		updateGraphicalObjectsHierarchy();

		performAutomaticLayout();
	}*/

	public RMViewerRepresentation(FlexoProject project, FGEModelFactory factory) {
		super(project, factory, PersistenceMode.SharedGraphicalRepresentations);
	}

	@Override
	public void init() {

		drawingGraphicalRepresentation = getFactory().makeDrawingGraphicalRepresentation();

		resourceGraphicalRepresentation = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		resourceGraphicalRepresentation.setWidth(40);
		resourceGraphicalRepresentation.setHeight(60);
		resourceGraphicalRepresentation.setIsFloatingLabel(false);
		((Rectangle) resourceGraphicalRepresentation.getShapeSpecification()).setIsRounded(true);
		resourceGraphicalRepresentation.setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		resourceGraphicalRepresentation.setAdjustMinimalWidthToLabelWidth(true);
		resourceGraphicalRepresentation.setMinimalWidth(150);

		resourceDependancyGraphicalRepresentation = getFactory().makeConnectorGraphicalRepresentation();
		resourceDependancyGraphicalRepresentation.setForeground(getFactory().makeForegroundStyle(Color.DARK_GRAY, 1.6f));
		resourceDependancyGraphicalRepresentation.getConnectorSpecification().setEndSymbol(EndSymbolType.PLAIN_ARROW);
		resourceDependancyGraphicalRepresentation.setTextStyle(getFactory()
				.makeTextStyle(Color.GRAY, new Font("SansSerif", Font.ITALIC, 8)));

		resourceSynchronizationGraphicalRepresentation = getFactory().makeConnectorGraphicalRepresentation();
		resourceSynchronizationGraphicalRepresentation = getFactory().makeConnectorGraphicalRepresentation();
		resourceSynchronizationGraphicalRepresentation.setForeground(getFactory().makeForegroundStyle(Color.RED, 1.6f));
		resourceSynchronizationGraphicalRepresentation.getConnectorSpecification().setStartSymbol(StartSymbolType.PLAIN_ARROW);
		resourceSynchronizationGraphicalRepresentation.getConnectorSpecification().setEndSymbol(EndSymbolType.PLAIN_ARROW);
		resourceSynchronizationGraphicalRepresentation.setTextStyle(getFactory().makeTextStyle(Color.GRAY,
				new Font("SansSerif", Font.ITALIC, 8)));

		final DrawingGRBinding<FlexoProject> drawingBinding = bindDrawing(FlexoProject.class, "project",
				new DrawingGRProvider<FlexoProject>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(FlexoProject drawable, FGEModelFactory factory) {
						return drawingGraphicalRepresentation;
					}
				});
		final ShapeGRBinding<FlexoResource> resourceBinding = bindShape(FlexoResource.class, "resource",
				new ShapeGRProvider<FlexoResource>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(FlexoResource drawable, FGEModelFactory factory) {
						return resourceGraphicalRepresentation;
					}
				});
		final ConnectorGRBinding<ResourceDependancy> dependancyBinding = bindConnector(ResourceDependancy.class, "resource_dependancy",
				resourceBinding, resourceBinding, new ConnectorGRProvider<ResourceDependancy>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(ResourceDependancy drawable, FGEModelFactory factory) {
						return resourceDependancyGraphicalRepresentation;
					}
				});
		final ConnectorGRBinding<ResourceSynchronization> synchroBinding = bindConnector(ResourceSynchronization.class,
				"resource_synchronization", resourceBinding, resourceBinding, new ConnectorGRProvider<ResourceSynchronization>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(ResourceSynchronization drawable, FGEModelFactory factory) {
						return resourceSynchronizationGraphicalRepresentation;
					}
				});

		drawingBinding.addToWalkers(new GRStructureVisitor<FlexoProject>() {

			@Override
			public void visit(FlexoProject project) {
				for (FlexoResource<? extends FlexoResourceData> res : project) {
					drawShape(resourceBinding, res, project);
				}

				for (FlexoResource<? extends FlexoResourceData> r1 : project) {
					for (FlexoResource<FlexoResourceData> r2 : r1.getDependentResources()) {
						if (r2.isRegistered()) {
							drawConnector(dependancyBinding, resourceDependancyBetween(r1, r2), r1, r2, project);
						} else {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Found dependant resource not in project: " + r2 + " for resource " + r1);
							}
						}
					}
					for (FlexoResource<FlexoResourceData> r2 : r1.getSynchronizedResources()) {
						drawConnector(synchroBinding, resourceSynchronizationBetween(r1, r2), r1, r2, project);
					}
				}

			}
		});

		resourceBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}

	@Override
	public void delete() {
		getProject().deleteObserver(this);
		super.delete();
	}

	public FlexoProject getProject() {
		return getModel();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getProject()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof ResourceAdded) {
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ResourceRemoved) {
				updateGraphicalObjectsHierarchy();
				// removeDrawable(((RoleRemoved)dataModification).getRemovedRole(), getRoleList());
			}
		}
	}

	private Map<FlexoResource, Map<FlexoResource, ResourceDependancy>> resourceDependancies;

	public ResourceDependancy resourceDependancyBetween(FlexoResource r1, FlexoResource r2) {
		if (resourceDependancies == null) {
			resourceDependancies = new Hashtable<FlexoResource, Map<FlexoResource, ResourceDependancy>>();
		}
		Map<FlexoResource, ResourceDependancy> rl = resourceDependancies.get(r1);
		if (rl == null) {
			rl = new Hashtable<FlexoResource, ResourceDependancy>();
			resourceDependancies.put(r1, rl);
		}
		ResourceDependancy returned = rl.get(r2);
		if (returned == null) {
			returned = new ResourceDependancy(r1, r2);
			rl.put(r2, returned);
		}
		return returned;
	}

	private Map<FlexoResource, Map<FlexoResource, ResourceSynchronization>> resourceSynchronizations;

	public ResourceSynchronization resourceSynchronizationBetween(FlexoResource r1, FlexoResource r2) {
		if (r1.getFullyQualifiedName().compareTo(r2.getFullyQualifiedName()) < 0) {
			return resourceSynchronizationBetween(r2, r1);
		}

		if (resourceSynchronizations == null) {
			resourceSynchronizations = new Hashtable<FlexoResource, Map<FlexoResource, ResourceSynchronization>>();
		}
		Map<FlexoResource, ResourceSynchronization> rl = resourceSynchronizations.get(r1);
		if (rl == null) {
			rl = new Hashtable<FlexoResource, ResourceSynchronization>();
			resourceSynchronizations.put(r1, rl);
		}
		ResourceSynchronization returned = rl.get(r2);
		if (returned == null) {
			returned = new ResourceSynchronization(r1, r2);
			rl.put(r2, returned);
		}
		return returned;
	}

	public static class ResourceDependancy {
		private FlexoResource r1;
		private FlexoResource r2;

		public ResourceDependancy(FlexoResource r1, FlexoResource r2) {
			super();
			this.r1 = r1;
			this.r2 = r2;
		}

		public FlexoResource getR1() {
			return r1;
		}

		public FlexoResource getR2() {
			return r2;
		}

		public Date getLastSynchronizationDate() {
			return r1.getLastSynchronizedWithResource(r2);
		}

	}

	public static class ResourceSynchronization {
		private FlexoResource r1;
		private FlexoResource r2;

		public ResourceSynchronization(FlexoResource r1, FlexoResource r2) {
			super();
			this.r1 = r1;
			this.r2 = r2;
		}

		public FlexoResource getR1() {
			return r1;
		}

		public FlexoResource getR2() {
			return r2;
		}
	}

}
