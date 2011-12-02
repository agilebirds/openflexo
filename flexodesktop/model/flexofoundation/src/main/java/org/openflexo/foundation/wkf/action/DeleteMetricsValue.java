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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.MetricsValue;

public class DeleteMetricsValue extends FlexoAction<DeleteMetricsValue, MetricsValue, MetricsValue> {

	private static final Logger logger = Logger.getLogger(DeleteMetricsValue.class.getPackage().getName());

	public static FlexoActionType<DeleteMetricsValue, MetricsValue, MetricsValue> actionType = new FlexoActionType<DeleteMetricsValue, MetricsValue, MetricsValue>(
			"delete_metrics", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteMetricsValue makeNewAction(MetricsValue focusedObject, Vector<MetricsValue> globalSelection, FlexoEditor editor) {
			return new DeleteMetricsValue(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(MetricsValue role, Vector<MetricsValue> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(MetricsValue role, Vector<MetricsValue> globalSelection) {
			return (role != null || (globalSelection != null && globalSelection.size() > 0));
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, MetricsValue.class);
	}

	protected DeleteMetricsValue(MetricsValue focusedObject, Vector<MetricsValue> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete MetricsValue(s)");
		for (MetricsValue mv : getMetricsValueToDelete()) {
			mv.delete();
		}
	}

	public Vector<MetricsValue> getMetricsValueToDelete() {
		Vector<MetricsValue> metricsValuesToDelete = new Vector<MetricsValue>();
		if (getGlobalSelection() != null) {
			for (FlexoModelObject o : getGlobalSelection()) {
				if (o instanceof MetricsValue) {
					metricsValuesToDelete.add((MetricsValue) o);
				}
			}
		}
		if (!metricsValuesToDelete.contains(getFocusedObject())) {
			metricsValuesToDelete.add(getFocusedObject());
		}
		return metricsValuesToDelete;
	}

	public MetricsValue getMetricsValue() {
		return getFocusedObject();
	}

}
