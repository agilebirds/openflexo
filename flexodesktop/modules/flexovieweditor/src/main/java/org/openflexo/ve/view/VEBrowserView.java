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
package org.openflexo.ve.view;

import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.ve.controller.VEBrowser;
import org.openflexo.ve.controller.VEController;

/**
 * Represents the view for the browser of this module
 * 
 * @author yourname
 * 
 */
public class VEBrowserView extends BrowserView {

	private static final Logger logger = Logger.getLogger(VEBrowserView.class.getPackage().getName());

	public VEBrowserView(VEBrowser browser, VEController controller, SelectionPolicy selectionPolicy) {
		super(browser, controller, selectionPolicy);
	}

	@Override
	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView2, ProjectBrowser _browser2) {
		return new OETreeDropTarget(treeView2, _browser2);
	}

	@Override
	public void treeSingleClick(FlexoObject object) {
	}

	@Override
	public void treeDoubleClick(FlexoObject object) {
		if (getController().getCurrentPerspective().hasModuleViewForObject(object)) {
			// Try to display object in view
			getController().selectAndFocusObject(object);
		}
	}

	public class OETreeDropTarget extends TreeDropTarget {

		public OETreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
			super(tree, browser);
		}

		@Override
		public boolean targetAcceptsSource(BrowserElement targ, BrowserElement source) {
			/*	if (source instanceof ViewDefinitionElement) {
					ViewDefinition dragged = ((ViewDefinitionElement) source).getViewDefinition();
					if (targ instanceof ViewFolderElement) {
						ViewFolder over = ((ViewFolderElement) targ).getFolder();
						if (over == null || dragged == null) {
							return false;
						}
						return dragged.getFolder() != over;
					} else if (targ instanceof ViewLibraryElement) {
						ViewLibrary over = ((ViewLibraryElement) targ).getViewLibrary();
						if (over == null || dragged == null) {
							return false;
						}
						return dragged.getFolder() != over.getRootFolder();
					}
				} else if (source instanceof ViewFolderElement) {
					ViewFolder dragged = ((ViewFolderElement) source).getFolder();
					if (targ instanceof ViewFolderElement) {
						ViewFolder over = ((ViewFolderElement) targ).getFolder();
						if (over == null || dragged == null) {
							return false;
						}
						return MoveViewFolder.isFolderMovableTo(dragged, over);
					} else if (targ instanceof ViewLibraryElement) {
						ViewLibrary over = ((ViewLibraryElement) targ).getViewLibrary();
						if (over == null || dragged == null) {
							return false;
						}
						return MoveViewFolder.isFolderMovableTo(dragged, over.getRootFolder());
					}
				}*/
			return false;
		}

		@Override
		public boolean handleDrop(BrowserElement source, BrowserElement targ) {
			/*if (targetAcceptsSource(targ, source)) {
				if (source instanceof ViewDefinitionElement) {
					ViewDefinition dragged = ((ViewDefinitionElement) source).getViewDefinition();
					ViewFolder folder = null;
					if (targ instanceof ViewFolderElement) {
						folder = ((ViewFolderElement) targ).getFolder();
					} else if (targ instanceof ViewLibraryElement) {
						folder = ((ViewLibraryElement) targ).getViewLibrary().getRootFolder();
					}
					if (folder == null) {
						return false;
					}
					Vector<ViewDefinition> v = new Vector<ViewDefinition>();
					v.add(dragged);
					MoveView move = MoveView.actionType.makeNewAction(dragged, v, getEditor());
					move.setFolder(folder);
					move.doAction();
					return move.hasActionExecutionSucceeded();
				} else if (source instanceof ViewFolderElement) {
					ViewFolder dragged = ((ViewFolderElement) source).getFolder();
					ViewFolder folder = null;
					if (targ instanceof ViewFolderElement) {
						folder = ((ViewFolderElement) targ).getFolder();
					} else if (targ instanceof ViewLibraryElement) {
						folder = ((ViewLibraryElement) targ).getViewLibrary().getRootFolder();
					}
					if (folder == null) {
						return false;
					}
					if (!MoveViewFolder.isFolderMovableTo(dragged, folder)) {
						FlexoController.notify(FlexoLocalization.localizedForKey("cannot_move_folder_to_such_location"));
						return false;
					}
					Vector<ViewFolder> v = new Vector<ViewFolder>();
					v.add(dragged);
					MoveViewFolder move = MoveViewFolder.actionType.makeNewAction(dragged, v, getEditor());
					move.setFolder(folder);
					move.doAction();
					return move.hasActionExecutionSucceeded();
				}
			} else {
				if (source == null || targ == null) {
					return false;
				}
				if (source instanceof ViewDefinitionElement) {
					ViewDefinition dragged = ((ViewDefinitionElement) source).getViewDefinition();
					if (targ instanceof ViewFolderElement) {
						ViewFolder over = ((ViewFolderElement) targ).getFolder();
						if (over == null || dragged == null) {
							return false;
						}
					}
				}
				// FlexoController.notify(FlexoLocalization.localizedForKey("drop_cannot_be_performed"));
			}*/
			return false;
		}

	}

}
