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

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IESpanTDWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.widget.LabeledWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DropIEElementInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DropIEElementInitializer(IEControllerActionInitializer actionInitializer) {
		super(DropIEElement.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public IEController getController() {
		return getControllerActionInitializer().getIEController();
	}

	@Override
	protected FlexoActionInitializer<DropIEElement> getDefaultInitializer() {
		return new FlexoActionInitializer<DropIEElement>() {
			@Override
			public boolean run(ActionEvent e, DropIEElement action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DropIEElement> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DropIEElement>() {
			@Override
			public boolean run(ActionEvent e, DropIEElement action) {
				if (action.getDroppedWidget() instanceof IEButtonWidget) {
					if (((IEButtonWidget) action.getDroppedWidget()).getImageName() != null) {
						if (((IEButtonWidget) action.getDroppedWidget()).getImageName().toLowerCase().indexOf("calendar") > -1) {
							action.getDroppedWidget().setTooltip(FlexoLocalization.localizedForKey("choose_a_date"));
							if (getProject().getFlexoComponentLibrary().getComponentNamed("WDLDateAssistant") instanceof PopupComponentDefinition) {
								((IEButtonWidget) action.getDroppedWidget())
										.setPopupComponentDefinition((PopupComponentDefinition) getProject().getFlexoComponentLibrary()
												.getComponentNamed("WDLDateAssistant"));
								IEWidget widget = action.getDroppedWidget().getPreviousWidget();
								// If it is right before this widget
								if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
									((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
								}
								// If it is right after this widget
								widget = action.getDroppedWidget().getNextWidget();
								if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
									((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
								}
								if (((IEButtonWidget) action.getDroppedWidget()).getDateTextfield() == null
										&& action.getDroppedWidget().findTDInParent() != null) {
									IETDWidget td = action.getDroppedWidget().findTDInParent();
									Vector<IWidget> v = td.getSequenceWidget().getAllNonSequenceWidget();
									if (v.indexOf(action.getDroppedWidget()) > 0) {
										for (int i = v.indexOf(action.getDroppedWidget()) - 1; i >= 0; i--) {
											widget = (IEWidget) v.get(i);
											if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
												((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
											}
										}
									}
									if (((IEButtonWidget) action.getDroppedWidget()).getDateTextfield() == null) {
										for (int i = v.size() - 1; i >= 0; i--) {
											widget = (IEWidget) v.get(i);
											if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
												((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
											}
										}
									}
									while (((IEButtonWidget) action.getDroppedWidget()).getDateTextfield() == null && td != null) {
										td = td.getPrevious();
										if (td == null)
											break;
										if (td instanceof IESpanTDWidget)
											continue;
										v = td.getSequenceWidget().getAllNonSequenceWidget();
										for (int i = v.size() - 1; i >= 0; i--) {
											widget = (IEWidget) v.get(i);
											if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
												((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
											}
										}
									}
									td = action.getDroppedWidget().findTDInParent();
									while (((IEButtonWidget) action.getDroppedWidget()).getDateTextfield() == null && td != null) {
										td = td.getNext();
										if (td == null)
											break;
										if (td instanceof IESpanTDWidget)
											continue;
										v = td.getSequenceWidget().getAllNonSequenceWidget();
										for (int i = 0; i < v.size(); i++) {
											widget = (IEWidget) v.get(i);
											if (widget instanceof IETextFieldWidget && ((IETextFieldWidget) widget).isDate()) {
												((IEButtonWidget) action.getDroppedWidget()).setDateTextfield((IETextFieldWidget) widget);
											}
										}
									}
								}
							}

						} else if (((IEButtonWidget) action.getDroppedWidget()).getImageName().toLowerCase().indexOf("delete") > -1)
							action.getDroppedWidget().setTooltip(FlexoLocalization.localizedForKey("delete"));
						else if (((IEButtonWidget) action.getDroppedWidget()).getImageName().toLowerCase().indexOf("remove") > -1)
							action.getDroppedWidget().setTooltip(FlexoLocalization.localizedForKey("remove"));

					}
					return true;
				} else if (action.getDroppedWidget() instanceof IEHyperlinkWidget) {
					((IEHyperlinkWidget) action.getDroppedWidget()).setValue(((IEHyperlinkWidget) action.getDroppedWidget())
							.getDefaultValue());
				}
				if (action.getDroppedWidget() != null) {
					getController().getIESelectionManager().setSelectedObject(action.getDroppedWidget());
					IEPanel view = getController().viewForObject(action.getDroppedWidget());
					if (view instanceof LabeledWidget) {
						((LabeledWidget) view).editLabel();
					}
				}
				return true;
			}
		};
	}

}
