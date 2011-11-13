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
package org.openflexo.foundation.ie.action;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class TopComponentUp extends FlexoAction<TopComponentUp, IEWidget, IEWidget> {
	protected static final Logger logger = FlexoLogger.getLogger(TopComponentUp.class.getPackage().getName());

	public static FlexoActionType<TopComponentUp, IEWidget, IEWidget> actionType = new FlexoActionType<TopComponentUp, IEWidget, IEWidget>(
			"move_up", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public TopComponentUp makeNewAction(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
			return new TopComponentUp(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return (object != null) && object.isTopComponent();
		}

	};

	private IEWidget component;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected TopComponentUp(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void doAction(Object context) throws FlexoException {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Do action top component up");
		}
		if ((getComponent() != null) && (getComponent().getParent() instanceof IESequence)) {
			IESequence<IWidget> c = (IESequence<IWidget>) getComponent().getParent();
			int i = c.indexOf(getComponent());
			if (i > 0) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Moving top component up from: " + i + " to " + (i - 1));
				}
				c.removeFromInnerWidgets(getComponent());
				c.insertElementAt(getComponent(), i - 1);
			}
		}

	}

	public IEWidget getComponent() {
		return component;
	}

	public void setComponent(IEWidget comp) {
		this.component = comp;
	}

}
