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
package org.openflexo.foundation.view.diagram.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.viewpoint.EditionAction;

public class LinkSchemeAction extends DiagramEditionSchemeAction<LinkSchemeAction, LinkScheme> {

	private static final Logger logger = Logger.getLogger(LinkSchemeAction.class.getPackage().getName());

	public static FlexoActionType<LinkSchemeAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<LinkSchemeAction, FlexoModelObject, FlexoModelObject>(
			"link_palette_connector", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public LinkSchemeAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new LinkSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof DiagramElement<?>;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DiagramElement.class);
	}

	private DiagramShape _fromShape;
	private DiagramShape _toShape;
	private DiagramConnector _newConnector;

	private LinkScheme _linkScheme;

	LinkSchemeAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	public LinkScheme getLinkScheme() {
		return _linkScheme;
	}

	public void setLinkScheme(LinkScheme linkScheme) {
		_linkScheme = linkScheme;
	}

	private EditionPatternInstance editionPatternInstance;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, NotImplementedException, InvalidParametersException {
		logger.info("Link palette connector");

		// getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();

		editionPatternInstance = getVirtualModelInstance().makeNewEditionPatternInstance(getEditionPattern());

		applyEditionActions();

	}

	@Override
	public LinkScheme getEditionScheme() {
		return getLinkScheme();
	}

	public DiagramConnector getNewConnector() {
		return _newConnector;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	public DiagramShape getFromShape() {
		return _fromShape;
	}

	public void setFromShape(DiagramShape fromShape) {
		_fromShape = fromShape;
	}

	public DiagramShape getToShape() {
		return _toShape;
	}

	public void setToShape(DiagramShape toShape) {
		_toShape = toShape;
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		if (getFromShape() != null) {
			return getFromShape().getDiagram();
		}
		if (getToShape() != null) {
			return getToShape().getDiagram();
		}
		if (getFocusedObject() instanceof DiagramElement<?>) {
			return ((DiagramElement<?>) getFocusedObject()).getDiagram();
		}
		return null;
	}

	@Override
	protected Object performAction(EditionAction anAction, Hashtable<EditionAction, Object> performedActions) {
		Object assignedObject = super.performAction(anAction, performedActions);
		if (anAction instanceof AddConnector) {
			AddConnector action = (AddConnector) anAction;
			_newConnector = (DiagramConnector) assignedObject;
		}
		return assignedObject;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.FROM_TARGET) && getLinkScheme().getFromTargetEditionPattern() != null) {
			return getFromShape().getEditionPatternInstance();
		}
		if (variable.getVariableName().equals(DiagramEditionScheme.TO_TARGET) && getLinkScheme().getToTargetEditionPattern() != null) {
			return getToShape().getEditionPatternInstance();
		}
		return super.getValue(variable);
	}

}