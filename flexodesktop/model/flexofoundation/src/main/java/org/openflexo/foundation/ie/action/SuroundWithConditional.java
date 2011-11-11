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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.SubsequenceModified;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IEWidget;

public class SuroundWithConditional extends IEOperatorAction {

	private static final Logger logger = Logger.getLogger(SuroundWithConditional.class.getPackage().getName());

	@Override
	Logger logger() {
		return logger;
	}

	public static FlexoActionType actionType = new FlexoActionType("Surround with conditional", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new SuroundWithConditional(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return isSelectionValid(sel(object, globalSelection));
		}

	};

	private ConditionalOperator newConditional;

	SuroundWithConditional(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		IEObject currentCommonFather = ((IEWidget) getGlobalSelection().elementAt(0)).getParent();
		IEWOComponent wo = (currentCommonFather instanceof IEWOComponent ? (IEWOComponent) currentCommonFather
				: ((IEWidget) currentCommonFather).getWOComponent());
		sequenceIsNew = false;
		IESequence seq = findSequenceSurrounding(true);
		if (seq != null) {
			newConditional = new ConditionalOperator(wo, seq, currentCommonFather.getProject());
			seq.setOperator(newConditional);

			if (sequenceIsNew) {
				// logger.info("New sequence... notification SubsequenceInserted
				// from a "+seq.getParent());
				sequenceIsNew = false;
				// seq.getParent().setChanged();
				// seq.getParent().notifyObservers(new
				// SubsequenceInserted(seq));
			} else {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Sequence modified... notification SubsequenceModified");
				}
				seq.getParent().setChanged();
				seq.getParent().notifyObservers(new SubsequenceModified(seq));
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unable to create the sequence... sorry");
			}
		}
	}

	public ConditionalOperator getNewConditional() {
		return newConditional;
	}

}
