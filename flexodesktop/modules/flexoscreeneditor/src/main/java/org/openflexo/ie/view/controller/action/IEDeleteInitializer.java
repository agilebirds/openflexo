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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class IEDeleteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	IEDeleteInitializer(IEControllerActionInitializer actionInitializer) {
		super(IEDelete.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<IEDelete> getDefaultInitializer() {
		return new FlexoActionInitializer<IEDelete>() {
			@Override
			public boolean run(ActionEvent e, IEDelete action) {
				boolean doIt = false;
				if (action.getFocusedObject() == null) {
					return false;
				}
				Vector<ComponentDefinition> components = new Vector<ComponentDefinition>();
				Vector<FlexoComponentFolder> folders = new Vector<FlexoComponentFolder>();
				Vector<FlexoModelObject> v = action.getGlobalSelectionAndFocusedObject();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("IE Delete with " + v);
				}

				Iterator<FlexoModelObject> i = v.iterator();
				while (i.hasNext()) {
					FlexoModelObject object = i.next();
					if (object instanceof ComponentDefinition) {
						if (!components.contains(object)) {
							components.add((ComponentDefinition) object);
						} else {
							i.remove();
						}
					} else if (object instanceof IEWOComponent) {
						if (!components.contains(((IEWOComponent) object).getComponentDefinition())) {
							components.add(((IEWOComponent) object).getComponentDefinition());
						} else {
							i.remove();
						}
					} else if (object instanceof FlexoComponentFolder) {
						folders.add((FlexoComponentFolder) object);
					}
				}

				if (components.size() > 0) {
					if (v.size() > 1) {
						FlexoController.notify(FlexoLocalization
								.localizedForKey("to_delete_a_component_select_it_separately_from_other_object."));
						return false;
					} else {
						ComponentDefinition componentToDelete = components.firstElement();
						if (componentToDelete == null) {
							return false;
						}
						String warnings = componentToDelete.requestDeletion();
						if (warnings == null) {
							doIt = FlexoController.confirmWithWarning(FlexoLocalization
									.localizedForKey("are_you_sure_you_want_to_delete_this_component?"));
						} else {
							String deleteAnyway = FlexoLocalization.localizedForKey("delete_anymay");
							String cancel = FlexoLocalization.localizedForKey("cancel");
							doIt = FlexoController.selectOption(warnings, new String[] { deleteAnyway, cancel }, cancel) == 0;
						}
					}
				} else if (folders.size() > 0) {
					for (FlexoComponentFolder f : folders) {
						if (f.containsComponents()) {
							FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_delete_a_non_empty_folder"));
							return false;
						}
						if (f.equals(f.getComponentLibrary().getRootFolder())) {
							FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_delete_the_root_folder"));
							return false;
						}
					}
				}
				if (!doIt) {
					doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
				}
				if (doIt) {
					HashSet<IEObject> visited = new HashSet<IEObject>();
					Vector<FlexoModelObject> objects = action.getGlobalSelectionAndFocusedObject();
					Vector<TOCEntry> tocEntries = new Vector<TOCEntry>();
					for (FlexoModelObject object : objects) {
						if (!object.isDeleted()) {
							if (object instanceof IEObject) {
								for (IObject o : ((IEObject) object).getAllEmbeddedIEObjects()) {
									if (!visited.contains(o)) {
										for (FlexoModelObjectReference ref : o.getReferencers()) {
											if (ref.getOwner() instanceof TOCEntry && !tocEntries.contains(ref.getOwner())) {
												tocEntries.add((TOCEntry) ref.getOwner());
											}
										}
										visited.add((IEObject) o);
									}
								}
							}
						}
					}
					if (tocEntries.size() > 0) {
						CheckboxListParameter<TOCEntry>[] def = new CheckboxListParameter[1];
						def[0] = new CheckboxListParameter<TOCEntry>("entries", "select_entries_to_delete", tocEntries,
								new Vector<TOCEntry>());
						def[0].setFormatter("displayString");
						AskParametersDialog dialog = AskParametersDialog
								.createAskParametersDialog(
										getProject(),
										getController().getFlexoFrame(),
										FlexoLocalization.localizedForKey("select_toc_entries_to_delete"),
										FlexoLocalization
												.localizedForKey("<html>the_following_toc_entries_are_linked_to_the_objects_you_are_about_to_delete.<br>select_the_ones_you_would_like_to_delete</html>"),
										def);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							Vector<TOCEntry> entriesToDelete = def[0].getValue();
							action.setEntriesToDelete(entriesToDelete);
						} else {
							doIt = false;
						}
					}
				}
				if (doIt) {
					HashSet<IEHyperlinkWidget> visited = new HashSet<IEHyperlinkWidget>();
					Vector<FlexoModelObject> objects = action.getGlobalSelectionAndFocusedObject();
					Vector<ActionNode> actionNodes = new Vector<ActionNode>();
					for (FlexoModelObject object : objects) {
						if (!object.isDeleted()) {
							if (object instanceof IEObject) {
								for (IObject o : ((IEObject) object).getAllEmbeddedIEObjects()) {
									if (!visited.contains(o)) {
										if (o instanceof IEHyperlinkWidget) {
											actionNodes.addAll(((IEHyperlinkWidget) o).getAllActionNodesLinkedToThisButton());
											visited.add((IEHyperlinkWidget) o);
										}
									}
								}
							}
						}
					}
					if (actionNodes.size() > 0) {
						CheckboxListParameter<ActionNode>[] def = new CheckboxListParameter[1];
						def[0] = new CheckboxListParameter<ActionNode>("actions", "select_actions_to_delete", actionNodes, actionNodes);
						def[0].setFormatter("displayString");
						AskParametersDialog dialog = AskParametersDialog
								.createAskParametersDialog(
										getProject(),
										getController().getFlexoFrame(),
										FlexoLocalization.localizedForKey("select_actions_to_delete"),
										FlexoLocalization
												.localizedForKey("<html>the_following_actions_are_linked_to_the_objects_you_are_about_to_delete.<br>select_the_ones_you_would_like_to_delete</html>"),
										def);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							Vector<ActionNode> entriesToDelete = def[0].getValue();
							action.setActionNodesToDelete(entriesToDelete);
						} else {
							doIt = false;
						}
					}
				}
				return doIt;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<IEDelete> getDefaultFinalizer() {
		return new FlexoActionFinalizer<IEDelete>() {
			@Override
			public boolean run(ActionEvent e, IEDelete action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(FlexoCst.BACKSPACE_DELETE_KEY_CODE, 0);
	}

	@Override
	public void init() {
		super.init();
		getControllerActionInitializer().registerAction(IEDelete.actionType, KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0));
	}

}
