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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.WKFObject;

public abstract class AddMetricsValue<A extends AddMetricsValue<A, T>, T extends WKFObject> extends FlexoAction<A, T, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddMetricsValue.class.getPackage().getName());

	private MetricsDefinition metricsDefinition;

	AddMetricsValue(FlexoActionType actionType, T focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	protected MetricsValue createMetricsValue() {
		MetricsValue mv = new MetricsValue(getFocusedObject().getProcess());
		mv.setMetricsDefinition(getMetricsDefinition());
		return mv;
	}

	public MetricsDefinition getMetricsDefinition() {
		return metricsDefinition;
	}

	public void setMetricsDefinition(MetricsDefinition metricsDefinition) {
		this.metricsDefinition = metricsDefinition;
	}

}
