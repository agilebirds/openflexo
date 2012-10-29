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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.IETabContainerWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class TopComponentDown extends FlexoAction<TopComponentDown, IEWidget, IEWidget> {
	protected static final Logger logger = FlexoLogger.getLogger(TopComponentDown.class.getPackage().getName());

	public static FlexoActionType<TopComponentDown, IEWidget, IEWidget> actionType = new FlexoActionType<TopComponentDown, IEWidget, IEWidget>(
			"move_down", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public TopComponentDown makeNewAction(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
			return new TopComponentDown(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(IEWidget object, Vector<IEWidget> globalSelection) {
			return object != null && object.isTopComponent();
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, IEBlocWidget.class);
		FlexoModelObject.addActionForClass(actionType, IEHTMLTableWidget.class);
		FlexoModelObject.addActionForClass(actionType, IESequenceTab.class);
		FlexoModelObject.addActionForClass(actionType, IESequenceTopComponent.class);
		FlexoModelObject.addActionForClass(actionType, IETabContainerWidget.class);
		FlexoModelObject.addActionForClass(actionType, TopComponentReusableWidget.class);
	}

	private IEWidget component;

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected TopComponentDown(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Do action top component down");
		}
		if (getComponent() != null && getComponent().getParent() instanceof IESequence) {
			IESequence<IWidget> c = (IESequence<IWidget>) getComponent().getParent();
			int i = c.indexOf(getComponent());
			if (i + 1 < c.length()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Moving top component down from: " + i + " to " + (i + 1));
				}
				c.removeFromInnerWidgets(getComponent());
				c.insertElementAt(getComponent(), i + 1);
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
