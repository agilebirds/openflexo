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
package org.openflexo.foundation.wkf.action;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.WKFObject;

public class WKFMove extends FlexoUndoableAction<WKFMove, WKFObject, WKFObject> {

	protected static final Logger logger = Logger.getLogger(WKFMove.class.getPackage().getName());

	public static FlexoActionType<WKFMove, WKFObject, WKFObject> actionType = new FlexoActionType<WKFMove, WKFObject, WKFObject>(
			"move_objects", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public WKFMove makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new WKFMove(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return globalSelection != null && globalSelection.size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, WKFObject.class);
	}

	private String graphicalContext;
	private Hashtable<WKFObject, Point2D.Double> initialLocations;
	private Hashtable<WKFObject, Point2D.Double> newLocations;
	private boolean wasInteractivelyPerformed = false;

	protected WKFMove(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		initialLocations = new Hashtable<WKFObject, Point2D.Double>();
		newLocations = new Hashtable<WKFObject, Point2D.Double>();
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (!wasInteractivelyPerformed()) {
			putObjectsToNewLocation();
		}
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		putObjectsToInitialLocation();
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		putObjectsToNewLocation();
	}

	private void putObjectsToNewLocation() {
		for (WKFObject o : newLocations.keySet()) {
			Point2D.Double newLocation = newLocations.get(o);
			o.setX(newLocation.x, getGraphicalContext());
			o.setY(newLocation.y, getGraphicalContext());
		}
	}

	private void putObjectsToInitialLocation() {
		for (WKFObject o : initialLocations.keySet()) {
			Point2D.Double initialLocation = initialLocations.get(o);
			o.setX(initialLocation.x, getGraphicalContext());
			o.setY(initialLocation.y, getGraphicalContext());
		}
	}

	public String getGraphicalContext() {
		return graphicalContext;
	}

	public void setGraphicalContext(String graphicalContext) {
		this.graphicalContext = graphicalContext;
	}

	public boolean wasInteractivelyPerformed() {
		return wasInteractivelyPerformed;
	}

	public void setWasInteractivelyPerformed(boolean wasInteractivelyPerformed) {
		this.wasInteractivelyPerformed = wasInteractivelyPerformed;
	}

	public Hashtable<WKFObject, Point2D.Double> getInitialLocations() {
		return initialLocations;
	}

	public Hashtable<WKFObject, Point2D.Double> getNewLocations() {
		return newLocations;
	}

}
