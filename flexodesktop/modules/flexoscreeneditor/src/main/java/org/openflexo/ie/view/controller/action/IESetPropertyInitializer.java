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

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class IESetPropertyInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public IESetPropertyInitializer(ControllerActionInitializer actionInitializer) {
		super(SetPropertyAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<SetPropertyAction> getDefaultInitializer() {
		return new FlexoActionInitializer<SetPropertyAction>() {
			@Override
			public boolean run(EventObject e, SetPropertyAction action) {
				return action.getFocusedObject() != null;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SetPropertyAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SetPropertyAction>() {
			@Override
			public boolean run(EventObject e, SetPropertyAction action) {
				/*if ((action.getKey().equals("listType")||action.getKey().equals("fetchObjects")) && action.getFocusedObject() instanceof RepetitionOperator) {
					RepetitionOperator rep = (RepetitionOperator)action.getFocusedObject();
					rep.getBindingItemDefinition();// Refresh the definition if needed
					if (rep.getBindingItem()!=null && !rep.getBindingItem().isBindingValid()){
						if (FlexoController.confirm(FlexoLocalization.localizedForKey("binding_item_was_alread_bound_but_is_no_more_valid_would_you_like_to_automatically_fix_it?"))) {
							SetPropertyAction set = SetPropertyAction.actionType.makeNewAction((FlexoModelObject) rep.getBindingItem().getBindingPathLastElement(),
									new Vector<FlexoModelObject>(), getEditor());
							set.setKey("type");
							set.setValue(rep.getBindingItemDefinition().getType().clone());
							set.doAction();
							rep.getBindingItem().connect();
						}
					}

				}*/
				if (action.getKey().equals("hyperlinkType") && action.getFocusedObject() instanceof IEHyperlinkWidget) {
					IEHyperlinkWidget link = (IEHyperlinkWidget) action.getFocusedObject();
					HyperlinkType type = (HyperlinkType) action.getPreviousValue();
					if (type != null && link.getHyperlinkType() != type) {
						if (type.isDisplayAction()) {
							if (link.getHyperlinkType() == HyperlinkType.FLEXOACTION) {
								if (link.getAllActionNodesLinkedToThisButton().size() > 0) {
									if (FlexoController
											.confirm(FlexoLocalization
													.localizedForKey("there_were_some_actions_bound_to_this_link_would_you_like_to_set_as_flexo_action"))) {
										for (ActionNode node : link.getAllActionNodesLinkedToThisButton()) {
											SetPropertyAction set = SetPropertyAction.actionType.makeNewAction(node, null, getEditor());
											set.setKey("actionType");
											set.setValue(ActionType.FLEXO_ACTION);
											set.doAction();
										}
									}
								}
							} else {
								if (link.getAllActionNodesLinkedToThisButton().size() > 0) {
									if (FlexoController.confirm(FlexoLocalization
											.localizedForKey("there_were_some_actions_bound_to_this_link_would_you_like_to_delete_them"))) {
										Vector<WKFObject> obj = new Vector<WKFObject>();
										obj.addAll(link.getAllActionNodesLinkedToThisButton());
										WKFDelete del = WKFDelete.actionType.makeNewAction(obj.firstElement(), obj, getEditor());
										del.doAction();
									}
								}
							}
						} else if (type.isFlexoAction()) {
							if (link.getHyperlinkType() == HyperlinkType.DISPLAYACTION) {
								if (link.getAllActionNodesLinkedToThisButton().size() > 0) {
									if (FlexoController
											.confirm(FlexoLocalization
													.localizedForKey("there_were_some_actions_bound_to_this_link_would_you_like_to_set_as_display_action"))) {
										for (ActionNode node : link.getAllActionNodesLinkedToThisButton()) {
											SetPropertyAction set = SetPropertyAction.actionType.makeNewAction(node, null, getEditor());
											set.setKey("actionType");
											set.setValue(ActionType.DISPLAY_ACTION);
											set.doAction();
										}
									}
								}
							} else {
								if (link.getAllActionNodesLinkedToThisButton().size() > 0) {
									if (FlexoController.confirm(FlexoLocalization
											.localizedForKey("there_were_some_actions_bound_to_this_link_would_you_like_to_delete_them"))) {
										Vector<WKFObject> obj = new Vector<WKFObject>();
										obj.addAll(link.getAllActionNodesLinkedToThisButton());
										WKFDelete del = WKFDelete.actionType.makeNewAction(obj.firstElement(), obj, getEditor());
										del.doAction();
									}
								}
							}
						}

					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SetPropertyAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SetPropertyAction>() {
			@Override
			public boolean handleException(final FlexoException exception, final SetPropertyAction action) {
				exception.printStackTrace();
				// GPO: We push the notification to later so that the action can keep on going. This is important so that inspector widgets
				// can refresh themselves
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						FlexoController.notify(FlexoLocalization.localizedForKey("could_not_set_property")
								+ " "
								+ (action.getLocalizedPropertyName() != null ? "'" + action.getLocalizedPropertyName() + "' " : "")
								+ FlexoLocalization.localizedForKey("to")
								+ " "
								+ (action.getValue() == null || action.getValue().equals("") ? FlexoLocalization
										.localizedForKey("empty_value") : action.getValue())
								+ (exception.getLocalizedMessage() != null ? "\n(" + FlexoLocalization.localizedForKey("details: ")
										+ exception.getLocalizedMessage() + ")" : ""));
					}
				});
				return true;
			}
		};
	}

}
