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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.AddConnector;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;

public class LinkSchemeAction extends EditionSchemeAction<LinkSchemeAction> {

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
			return object instanceof ViewObject;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, ViewObject.class);
	}

	private ViewShape _fromShape;
	private ViewShape _toShape;
	private ViewConnector _newConnector;

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
		if (getEditionPattern().getViewPoint().getViewpointOntology() != null) {
			getEditionPattern().getViewPoint().getViewpointOntology().loadWhenUnloaded();
		}

		editionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());

		applyEditionActions();

	}

	@Override
	public EditionScheme getEditionScheme() {
		return getLinkScheme();
	}

	public ViewConnector getNewConnector() {
		return _newConnector;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

	public ViewShape getFromShape() {
		return _fromShape;
	}

	public void setFromShape(ViewShape fromShape) {
		_fromShape = fromShape;
	}

	public ViewShape getToShape() {
		return _toShape;
	}

	public void setToShape(ViewShape toShape) {
		_toShape = toShape;
	}

	@Override
	protected View retrieveOEShema() {
		if (getFromShape() != null) {
			return getFromShape().getShema();
		}
		if (getToShape() != null) {
			return getToShape().getShema();
		}
		if (getFocusedObject() instanceof ViewObject) {
			return ((ViewObject) getFocusedObject()).getShema();
		}
		return null;
	}

	@Override
	protected ViewConnector performAddConnector(AddConnector action) {
		_newConnector = super.performAddConnector(action);
		return _newConnector;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionPatternPathElement) {
			if (variable.getVariableName().equals(EditionScheme.FROM_TARGET) && getLinkScheme().getFromTargetEditionPattern() != null) {
				return getFromShape().getEditionPatternInstance();
			}
			if (variable.getVariableName().equals(EditionScheme.TO_TARGET) && getLinkScheme().getToTargetEditionPattern() != null) {
				return getToShape().getEditionPatternInstance();
			}
		}
		return super.getValue(variable);
	}

}