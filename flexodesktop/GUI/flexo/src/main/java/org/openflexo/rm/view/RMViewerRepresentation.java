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

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceAdded;
import org.openflexo.foundation.rm.ResourceRemoved;

public class RMViewerRepresentation extends DefaultDrawing<FlexoProject> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(RMViewerRepresentation.class.getPackage().getName());

	private DrawingGraphicalRepresentation<FlexoProject> graphicalRepresentation;

	public RMViewerRepresentation(FlexoProject project) {
		super(project);
		graphicalRepresentation = new DrawingGraphicalRepresentation<FlexoProject>(this);

		project.addObserver(this);

		updateGraphicalObjectsHierarchy();

		performAutomaticLayout();
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		for (FlexoResource res : getProject().getResources().values()) {
			addDrawable(res, getProject());
		}

		for (FlexoResource r1 : getProject().getResources().values()) {
			for (FlexoResource r2 : r1.getDependantResources()) {
				if (r2.isRegistered())
					addDrawable(resourceDependancyBetween(r1, r2), getProject());
				else {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Found dependant resource not in project: " + r2 + " for resource " + r1);
				}
			}
			for (FlexoResource r2 : r1.getSynchronizedResources()) {
				addDrawable(resourceSynchronizationBetween(r1, r2), getProject());
			}
		}

	}

	public FlexoProject getProject() {
		return getModel();
	}

	@Override
	public DrawingGraphicalRepresentation<FlexoProject> getDrawingGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable) {
		return (GraphicalRepresentation<O>) buildGraphicalRepresentation(aDrawable);
	}

	private GraphicalRepresentation<?> buildGraphicalRepresentation(Object aDrawable) {
		if (aDrawable instanceof FlexoResource) {
			return new ResourceGR((FlexoResource) aDrawable, this);
		} else if (aDrawable instanceof ResourceDependancy) {
			return new ResourceDependancyGR((ResourceDependancy) aDrawable, this);
		} else if (aDrawable instanceof ResourceSynchronization) {
			return new ResourceSynchronizationGR((ResourceSynchronization) aDrawable, this);
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
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

	private Hashtable<FlexoResource, Hashtable<FlexoResource, ResourceDependancy>> resourceDependancies;

	public ResourceDependancy resourceDependancyBetween(FlexoResource r1, FlexoResource r2) {
		if (resourceDependancies == null) {
			resourceDependancies = new Hashtable<FlexoResource, Hashtable<FlexoResource, ResourceDependancy>>();
		}
		Hashtable<FlexoResource, ResourceDependancy> rl = resourceDependancies.get(r1);
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

	private Hashtable<FlexoResource, Hashtable<FlexoResource, ResourceSynchronization>> resourceSynchronizations;

	public ResourceSynchronization resourceSynchronizationBetween(FlexoResource r1, FlexoResource r2) {
		if (r1.getFullyQualifiedName().compareTo(r2.getFullyQualifiedName()) < 0)
			return resourceSynchronizationBetween(r2, r1);

		if (resourceSynchronizations == null) {
			resourceSynchronizations = new Hashtable<FlexoResource, Hashtable<FlexoResource, ResourceSynchronization>>();
		}
		Hashtable<FlexoResource, ResourceSynchronization> rl = resourceSynchronizations.get(r1);
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

	public void performAutomaticLayout() {
		double width = getDrawingGraphicalRepresentation().getWidth();
		double height = getDrawingGraphicalRepresentation().getHeight();
		int maxOrder = -1;
		Hashtable<Integer, Vector<FlexoResource>> resources = new Hashtable<Integer, Vector<FlexoResource>>();
		for (FlexoResource res : getProject().getResources().values()) {
			System.out.println("resource: " + res + " order " + res.getResourceOrder());
			if (res.getResourceOrder() > maxOrder)
				maxOrder = res.getResourceOrder();
			Vector<FlexoResource> thisOrderResources = resources.get(res.getResourceOrder());
			if (thisOrderResources == null) {
				thisOrderResources = new Vector<FlexoResource>();
				resources.put(res.getResourceOrder(), thisOrderResources);
			}
			thisOrderResources.add(res);
		}

		for (int i : resources.keySet()) {
			int j = 0;
			for (FlexoResource r : resources.get(i)) {
				((ResourceGR) getGraphicalRepresentation(r)).setLocation(new FGEPoint(i * width / maxOrder, j * height
						/ resources.get(i).size()));
				j++;
			}
		}

	}

}
