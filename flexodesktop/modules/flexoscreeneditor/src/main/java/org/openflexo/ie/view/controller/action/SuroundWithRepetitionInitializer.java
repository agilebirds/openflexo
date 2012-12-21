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
package org.openflexo.ie.view.controller.action;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.SuroundWithRepetition;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.widget.IESpanTDWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class SuroundWithRepetitionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	SuroundWithRepetitionInitializer(IEControllerActionInitializer actionInitializer) {
		super(SuroundWithRepetition.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<SuroundWithRepetition> getDefaultInitializer() {
		return new FlexoActionInitializer<SuroundWithRepetition>() {
			@Override
			public boolean run(EventObject e, SuroundWithRepetition action) {
				if (action.getFocusedObject() instanceof ITableRow) {
					Vector<FlexoObject> v = (Vector<FlexoObject>) action.getGlobalSelection().clone();
					if (!v.contains(action.getFocusedObject())) {
						v.add(action.getFocusedObject());
					}
					if (action.getFocusedObject() instanceof IEOperator) {
						v.remove(action.getFocusedObject());
						v.add(((IEOperator) action.getFocusedObject()).getOperatedSequence());
					}

					Enumeration en = action.getGlobalSelection().elements();
					while (en.hasMoreElements()) {
						Object o = en.nextElement();
						if (o instanceof IEOperator) {
							v.remove(o);
							v.add(((IEOperator) o).getOperatedSequence());
						}
					}

					en = v.elements();
					while (en.hasMoreElements()) {
						Object o = en.nextElement();
						if (!(o instanceof ITableRow)) {
							FlexoController.notify(FlexoLocalization.localizedForKey("surrounded_objects_must_be_of_same_type"));
							return false;
						}
					}
					en = v.elements();
					Vector<IESpanTDWidget> spans = new Vector<IESpanTDWidget>();
					while (en.hasMoreElements()) {
						ITableRow tr = (ITableRow) en.nextElement();
						Vector<IETDWidget> tds = tr.getAllTD();
						for (int i = 0; i < tds.size(); i++) {
							IETDWidget td = tds.get(i);
							spans.addAll(td.getSpannedTD());
						}
					}
					Enumeration<IESpanTDWidget> enSpans = spans.elements();
					while (enSpans.hasMoreElements()) {
						IESpanTDWidget s = enSpans.nextElement();
						boolean ok = false;
						en = v.elements();
						while (en.hasMoreElements() && !ok) {
							ITableRow tr = (ITableRow) en.nextElement();
							if (tr.containsTD(s)) {
								ok = true;
							}
						}
						if (!ok) {
							FlexoController.notify(FlexoLocalization.localizedForKey("one_of_the_cell_span_is_not_in_the_selection"));
							return false;
						}
					}
					return true;
				}
				FlexoController.notify(FlexoLocalization.localizedForKey("repetition_can_only_surround_lines_of_table"));
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SuroundWithRepetition> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SuroundWithRepetition>() {
			@Override
			public boolean run(EventObject e, SuroundWithRepetition action) {
				((IEController) getController()).getSelectionManager().setSelectedObject(action.getNewRepetition().getOperatedSequence());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.REPETITION_ICON;
	}

}
