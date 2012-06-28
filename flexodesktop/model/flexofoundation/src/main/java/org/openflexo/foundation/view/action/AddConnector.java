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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;

public class AddConnector extends FlexoAction<AddConnector, ViewShape, ViewObject> {

	private static final Logger logger = Logger.getLogger(AddConnector.class.getPackage().getName());

	public static FlexoActionType<AddConnector, ViewShape, ViewObject> actionType = new FlexoActionType<AddConnector, ViewShape, ViewObject>(
			"add_connector", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddConnector makeNewAction(ViewShape focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new AddConnector(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewShape shape, Vector<ViewObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ViewShape shape, Vector<ViewObject> globalSelection) {
			return shape != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddConnector.actionType, ViewShape.class);
	}

	private ViewShape _fromShape;
	private ViewShape _toShape;
	private String annotation;
	private ViewConnector _newConnector;

	private boolean automaticallyCreateConnector = false;

	AddConnector(ViewShape focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Add connector");
		}
		if (getFocusedObject() != null && getFromShape() != null && getToShape() != null) {
			ViewObject parent = ViewObject.getFirstCommonAncestor(getFromShape(), getToShape());
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Parent=" + parent);
			}
			if (parent == null) {
				throw new IllegalArgumentException("No common ancestor");
			}
			_newConnector = new ViewConnector(getFromShape().getShema(), getFromShape(), getToShape());
			_newConnector.setDescription(annotation);
			parent.addToChilds(_newConnector);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Focused role is null !");
			}
		}
	}

	public ViewShape getToShape() {
		return _toShape;
	}

	public void setToShape(ViewShape aShape) {
		_toShape = aShape;
	}

	public ViewShape getFromShape() {
		if (_fromShape == null) {
			return getFocusedObject();
		}
		return _fromShape;
	}

	public void setFromShape(ViewShape fromShape) {
		_fromShape = fromShape;
	}

	public ViewConnector getConnector() {
		return _newConnector;
	}

	public boolean getAutomaticallyCreateConnector() {
		return automaticallyCreateConnector;
	}

	public void setAutomaticallyCreateConnector(boolean automaticallyCreateConnector) {
		this.automaticallyCreateConnector = automaticallyCreateConnector;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

}
