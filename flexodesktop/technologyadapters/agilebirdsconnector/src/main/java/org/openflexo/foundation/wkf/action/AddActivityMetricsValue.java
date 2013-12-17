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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;

public class AddActivityMetricsValue extends AddMetricsValue<AddActivityMetricsValue, AbstractActivityNode> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddActivityMetricsValue.class.getPackage().getName());

	public static FlexoActionType<AddActivityMetricsValue, AbstractActivityNode, WKFObject> actionType = new FlexoActionType<AddActivityMetricsValue, AbstractActivityNode, WKFObject>(
			"add_metrics_value", FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(AbstractActivityNode object, Vector<WKFObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(AbstractActivityNode object, Vector<WKFObject> globalSelection) {
			return false;
		}

		@Override
		public AddActivityMetricsValue makeNewAction(AbstractActivityNode focusedObject, Vector<WKFObject> globalSelection,
				FlexoEditor editor) {
			return new AddActivityMetricsValue(focusedObject, globalSelection, editor);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, AbstractActivityNode.class);
	}

	AddActivityMetricsValue(AbstractActivityNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		getFocusedObject().addToMetricsValues(createMetricsValue());
	}
}
